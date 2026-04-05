package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderStore orderStore;

    public AdminController(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("orders", orderStore.findAll());
        return "admin";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String orderId, @RequestParam String status) {
        orderStore.updateStatus(orderId, status);
        return "redirect:/admin";
    }
}
