package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartPageController {

    private final CartService cartService;

    public CartPageController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        model.addAttribute("items", cartService.getCart(session));
        model.addAttribute("total", String.format("₹%,.0f", cartService.getTotal(session)));
        return "cart";
    }

    @PostMapping("/cart/delete")
    public String deleteItem(@RequestParam String name, HttpSession session) {
        cartService.removeItem(session, name);
        return "redirect:/cart";
    }
}
