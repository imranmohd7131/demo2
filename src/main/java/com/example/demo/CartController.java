package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(
            @RequestParam String name,
            @RequestParam String price,
            @RequestParam double amount,
            HttpSession session) {
        cartService.addItem(session, name, price, amount);
        return ResponseEntity.ok(Map.of(
            "count", cartService.getCount(session),
            "total", cartService.getTotal(session)
        ));
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> remove(
            @RequestParam String name,
            HttpSession session) {
        cartService.removeItem(session, name);
        return ResponseEntity.ok(Map.of(
            "count", cartService.getCount(session),
            "total", cartService.getTotal(session)
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> count(HttpSession session) {
        return ResponseEntity.ok(Map.of(
            "count", cartService.getCount(session),
            "total", cartService.getTotal(session)
        ));
    }
}
