package com.example.demo;

import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {

    private final OrderStore orderStore;
    private final CartService cartService;
    private final RazorpayService razorpayService;

    public CheckoutController(OrderStore orderStore, CartService cartService, RazorpayService razorpayService) {
        this.orderStore = orderStore;
        this.cartService = cartService;
        this.razorpayService = razorpayService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        List<CartService.CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) return "redirect:/";
        model.addAttribute("items", cart);
        model.addAttribute("total", String.format("\u20B9%,.0f", cartService.getTotal(session)));
        return "checkout";
    }

    /**
     * Step 1: Customer submits details → create Razorpay order → show payment popup
     */
    @PostMapping("/initiate-payment")
    public String initiatePayment(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String address,
            HttpSession session,
            Model model) {

        List<CartService.CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) return "redirect:/";

        double total = cartService.getTotal(session);

        try {
            String razorpayOrderId = razorpayService.createOrder(total);

            // Save pending order details in session for after payment
            session.setAttribute("pending_name", name);
            session.setAttribute("pending_phone", phone);
            session.setAttribute("pending_address", address);
            session.setAttribute("pending_total", total);
            session.setAttribute("pending_razorpay_order_id", razorpayOrderId);

            model.addAttribute("razorpayOrderId", razorpayOrderId);
            model.addAttribute("razorpayKeyId", razorpayService.getKeyId());
            model.addAttribute("amount", (long)(total * 100)); // paise
            model.addAttribute("customerName", name);
            model.addAttribute("customerPhone", phone);
            model.addAttribute("total", String.format("\u20B9%,.0f", total));
            return "payment";

        } catch (RazorpayException e) {
            model.addAttribute("error", "Could not initiate payment. Please try again.");
            model.addAttribute("items", cart);
            model.addAttribute("total", String.format("\u20B9%,.0f", total));
            return "checkout";
        }
    }

    /**
     * Step 2: Razorpay calls back with payment result → verify signature → confirm order
     */
    @PostMapping("/verify-payment")
    public String verifyPayment(
            @RequestParam String razorpay_order_id,
            @RequestParam String razorpay_payment_id,
            @RequestParam String razorpay_signature,
            @RequestParam String payment_method,
            HttpSession session,
            Model model) {

        boolean valid = razorpayService.verifySignature(razorpay_order_id, razorpay_payment_id, razorpay_signature);

        if (!valid) {
            model.addAttribute("error", "Payment verification failed. Please contact support.");
            return "payment-failed";
        }

        // Build confirmed order
        String name    = (String) session.getAttribute("pending_name");
        String phone   = (String) session.getAttribute("pending_phone");
        String address = (String) session.getAttribute("pending_address");
        double total   = (double) session.getAttribute("pending_total");

        List<CartService.CartItem> cart = cartService.getCart(session);

        Order order = new Order();
        order.setCustomerName(name);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPaymentMethod(payment_method);
        order.setPaymentStatus("SUCCESS");
        order.setRazorpayOrderId(razorpay_order_id);
        order.setRazorpayPaymentId(razorpay_payment_id);

        List<Order.CartItem> orderItems = cart.stream()
            .map(i -> new Order.CartItem(i.getName(), i.getPrice(), i.getQty(), i.getAmount() * i.getQty()))
            .toList();
        order.setItems(orderItems);
        order.setTotal(total);

        orderStore.save(order);
        cartService.clearCart(session);

        // Clear pending session data
        session.removeAttribute("pending_name");
        session.removeAttribute("pending_phone");
        session.removeAttribute("pending_address");
        session.removeAttribute("pending_total");
        session.removeAttribute("pending_razorpay_order_id");

        return "redirect:/order-confirmed/" + order.getId();
    }

    @GetMapping("/order-confirmed/{id}")
    public String confirmed(@PathVariable String id, Model model) {
        Order order = orderStore.findById(id);
        if (order == null) return "redirect:/";
        model.addAttribute("order", order);
        return "order-confirmed";
    }

    @GetMapping("/track/{id}")
    public String track(@PathVariable String id, Model model) {
        Order order = orderStore.findById(id);
        if (order == null) return "redirect:/";
        model.addAttribute("order", order);
        return "track-order";
    }
}
