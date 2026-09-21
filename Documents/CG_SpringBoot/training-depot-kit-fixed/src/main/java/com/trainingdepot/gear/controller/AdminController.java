package com.trainingdepot.gear.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.OrderStatus;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.service.OrderService;
import com.trainingdepot.gear.service.ProductService;

/** Everything under /admin - access is already gated by AdminInterceptor. */
@Controller
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        List<Product> allProducts = productService.getAllForAdmin();
        List<Order> allOrders = orderService.getAllOrdersForAdmin();

        long activeCount = allProducts.stream().filter(Product::isActive).count();
        long lowStockCount = allProducts.stream().filter(p -> p.isActive() && p.isLowStock()).count();
        long outOfStockCount = allProducts.stream().filter(p -> p.isActive() && !p.isInStock()).count();
        long confirmedCount = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count();
        BigDecimal revenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("activeCount", activeCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("outOfStockCount", outOfStockCount);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("revenue", revenue);
        model.addAttribute("recentOrders", allOrders.stream().limit(6).toList());
        return "admin/dashboard";
    }

    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrdersForAdmin());
        return "admin/orders";
    }
}
