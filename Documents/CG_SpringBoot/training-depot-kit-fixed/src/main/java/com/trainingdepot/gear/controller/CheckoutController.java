package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.trainingdepot.gear.checkout.CheckoutResult;
import com.trainingdepot.gear.config.SessionKeys;
import com.trainingdepot.gear.exception.EmptyKitException;
import com.trainingdepot.gear.exception.InsufficientStockException;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.service.KitService;
import com.trainingdepot.gear.service.OrderService;

@Controller
public class CheckoutController {

    private final KitService kitService;
    private final OrderService orderService;

    public CheckoutController(KitService kitService, OrderService orderService) {
        this.kitService = kitService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String review(HttpSession session, Model model) {
        KitView kitView = kitService.view(session);
        if (kitView.isEmpty()) {
            return "redirect:/kit";
        }
        model.addAttribute("kit", kitView);
        return "checkout";
    }

    @PostMapping("/checkout/pay")
    public String pay(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionKeys.LOGGED_IN_USER);
        KitView kitView = kitService.view(session);

        try {
            CheckoutResult result = orderService.startCheckout(user, kitView);
            model.addAttribute("kit", kitView);
            model.addAttribute("payment", result);
            return "checkout";
        } catch (EmptyKitException e) {
            return "redirect:/kit";
        } catch (InsufficientStockException e) {
            model.addAttribute("kit", kitView);
            model.addAttribute("error", e.getMessage());
            return "checkout";
        }
    }
}
