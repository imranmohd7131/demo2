package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private static final String SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SESSION_KEY, cart);
        }
        return cart;
    }

    public void addItem(HttpSession session, String name, String price, double amount) {
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getName().equals(name)) {
                item.incrementQty();
                return;
            }
        }
        cart.add(new CartItem(name, price, amount));
    }

    public void removeItem(HttpSession session, String name) {
        getCart(session).removeIf(i -> i.getName().equals(name));
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public int getCount(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQty).sum();
    }

    public double getTotal(HttpSession session) {
        return getCart(session).stream().mapToDouble(i -> i.getAmount() * i.getQty()).sum();
    }

    // Mutable cart item (not a record, needs qty increment)
    public static class CartItem {
        private String name;
        private String price;
        private double amount;
        private int qty;

        public CartItem(String name, String price, double amount) {
            this.name = name;
            this.price = price;
            this.amount = amount;
            this.qty = 1;
        }

        public void incrementQty() { this.qty++; }

        public String getName()     { return name; }
        public String getPrice()    { return price; }
        public double getAmount()   { return amount; }
        public int    getQty()      { return qty; }
        public String getSubtotal() {
            return String.format("\u20B9%,.0f", amount * qty);
        }
    }
}
