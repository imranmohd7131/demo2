package com.example.demo;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class OrderStore {
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    public Order findById(String id) {
        return orders.get(id);
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>(orders.values());
        Collections.reverse(list);
        return list;
    }

    public void updateStatus(String id, String status) {
        Order o = orders.get(id);
        if (o != null) o.setOrderStatus(status);
    }
}
