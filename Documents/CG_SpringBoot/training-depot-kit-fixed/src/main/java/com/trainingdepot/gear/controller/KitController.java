package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trainingdepot.gear.exception.ProductNotFoundException;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.service.KitService;

@Controller
public class KitController {

    private final KitService kitService;

    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    @GetMapping("/kit")
    public String view(HttpSession session, Model model) {
        KitView kitView = kitService.view(session);
        model.addAttribute("kit", kitView);
        return "kit";
    }

    @PostMapping("/kit/add")
    public String add(@RequestParam int productId, @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String redirectTo, HttpSession session,
            RedirectAttributes redirectAttributes) {
        String target = safeRedirectTarget(redirectTo, "/equipment");
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Choose a quantity of 1 or more.");
            return "redirect:" + target;
        }
        try {
            kitService.add(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Added to kit.");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "That item is no longer available.");
        }
        return "redirect:" + target;
    }

    @PostMapping("/kit/update")
    public String update(@RequestParam int productId, @RequestParam int quantity, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            kitService.updateQuantity(session, productId, quantity);
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "That item is no longer available.");
        }
        return "redirect:/kit";
    }

    @PostMapping("/kit/remove")
    public String remove(@RequestParam int productId, HttpSession session) {
        kitService.remove(session, productId);
        return "redirect:/kit";
    }

    /**
     * These three endpoints all bind productId/quantity straight from the
     * request. kit.jsp and the equipment pages always send valid integers,
     * but a hand-crafted, replayed, or stale POST (missing field, a
     * non-numeric value) previously fell through to the generic 500 handler
     * as "Something Went Wrong" - it's a bad request, not a server fault, so
     * it's handled here instead and sent back to a working kit page.
     */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
    public String handleMalformedRequest(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "That request didn't look right - please try again.");
        return "redirect:/kit";
    }

    private String safeRedirectTarget(String redirectTo, String fallback) {
        if (redirectTo == null || redirectTo.isBlank()) {
            return fallback;
        }

        if (redirectTo.equals("/equipment") || redirectTo.equals("/kit")) {
            return redirectTo;
        }

        return fallback;
    }
}
