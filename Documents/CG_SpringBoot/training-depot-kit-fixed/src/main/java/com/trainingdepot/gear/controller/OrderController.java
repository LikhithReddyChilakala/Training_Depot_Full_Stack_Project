package com.trainingdepot.gear.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.trainingdepot.gear.config.SessionKeys;
import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.service.OrderService;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        List<Order> orders = orderService.getOrdersForUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String detail(@PathVariable long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        Order order = orderService.getOrderForUser(id, user);
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
