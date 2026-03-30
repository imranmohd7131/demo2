package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShopController {

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = List.of(
            new Product("Diamond Solitaire Ring",   "Rings",     "₹45,000", "💍", "Classic diamond ring in 18k white gold",          true,  "4.9", "128"),
            new Product("Gold Hoop Earrings",        "Earrings",  "₹12,500", "✨", "Lightweight 22k gold hoops for daily wear",        false, "4.7", "95"),
            new Product("Pearl Necklace",            "Necklaces", "₹28,000", "📿", "Elegant freshwater pearl strand necklace",         true,  "4.8", "74"),
            new Product("Emerald Bracelet",          "Bracelets", "₹35,000", "💚", "Stunning emerald and gold tennis bracelet",        false, "4.6", "52"),
            new Product("Ruby Pendant",              "Necklaces", "₹22,000", "❤️", "Heart-shaped ruby pendant in rose gold",           true,  "4.9", "110"),
            new Product("Sapphire Stud Earrings",    "Earrings",  "₹18,000", "💙", "Blue sapphire studs set in platinum",              false, "4.5", "63"),
            new Product("Gold Bangle Set",           "Bracelets", "₹55,000", "🔱", "Traditional 22k gold bangle set of 4",             true,  "5.0", "200"),
            new Product("Diamond Nose Pin",          "Nose Pins", "₹8,500",  "💎", "Delicate diamond nose pin in white gold",          false, "4.7", "88"),
            new Product("Antique Choker",            "Necklaces", "₹40,000", "👑", "Handcrafted antique gold choker necklace",         false, "4.8", "45"),
            new Product("Rose Gold Ring",            "Rings",     "₹32,000", "🌹", "Floral rose gold ring with micro diamonds",        false, "4.6", "37"),
            new Product("Kundan Earrings",           "Earrings",  "₹9,500",  "🌟", "Traditional kundan drop earrings",                 false, "4.7", "59"),
            new Product("Silver Anklet Pair",        "Anklets",   "₹4,200",  "🦶", "Handcrafted pure silver anklet pair",              false, "4.4", "81")
        );

        List<Testimonial> testimonials = List.of(
            new Testimonial("Priya Sharma",    "Bought the gold bangle set for my wedding — absolutely stunning quality!", "⭐⭐⭐⭐⭐"),
            new Testimonial("Rahul Mehta",     "Great craftsmanship and very helpful staff. Highly recommend!",            "⭐⭐⭐⭐⭐"),
            new Testimonial("Ananya Reddy",    "The pearl necklace was exactly as described. Fast delivery too.",          "⭐⭐⭐⭐⭐")
        );

        model.addAttribute("products", products);
        model.addAttribute("testimonials", testimonials);
        model.addAttribute("shopName", "Lakshmi Jewellers");
        model.addAttribute("tagline", "Crafting Elegance Since 1985");
        return "index";
    }

    public record Product(String name, String category, String price, String icon,
                          String description, boolean featured, String rating, String reviews) {}

    public record Testimonial(String name, String message, String stars) {}
}
