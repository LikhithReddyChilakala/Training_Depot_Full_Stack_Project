package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trainingdepot.gear.config.SessionKeys;
import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.service.UserService;

/**
 * The ADMINISTRATOR authentication flow - deliberately separate from
 * AuthController's customer /login. Same users table, same UserService,
 * same password hashing; the only difference is the Role.ADMIN check
 * below, and the fact that the two flows never share a form, an error
 * message, or a redirect target.
 *
 * The seeded admin credentials themselves live in application.properties
 * (depot.seed.admin-username / depot.seed.admin-password, read by
 * DataSeeder) - nothing here hardcodes them.
 */
@Controller
public class AdminAuthController {

    private final UserService userService;

    public AdminAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/login")
    public String loginForm(@RequestParam(required = false) String redirectTo, HttpServletRequest request,
            Model model) {
        Object sessionUser = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);
        if (sessionUser instanceof User loggedInUser) {
            return "redirect:" + (loggedInUser.getRole() == Role.ADMIN ? "/admin" : "/");
        }
        model.addAttribute("redirectTo", redirectTo);
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username, @RequestParam String password,
            @RequestParam(required = false) String redirectTo, HttpSession session,
            RedirectAttributes redirectAttributes) {
        return userService.authenticate(username, password)
                .map(user -> {
                    if (user.getRole() != Role.ADMIN) {
                        // Correct credentials, wrong door: a normal customer
                        // account never gets an admin session here.
                        redirectAttributes.addFlashAttribute("error",
                                "That account does not have administrator access.");
                        return "redirect:/admin/login";
                    }
                    session.setAttribute(SessionKeys.LOGGED_IN_USER, user);
                    return "redirect:" + safeAdminRedirectTarget(redirectTo);
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error",
                            "Administrator access denied - check your credentials.");
                    return "redirect:/admin/login";
                });
    }

    /** Only ever sends a freshly-logged-in admin somewhere inside /admin - never the customer storefront. */
    private String safeAdminRedirectTarget(String redirectTo) {
        if (redirectTo != null && redirectTo.startsWith("/admin") && !redirectTo.startsWith("//")) {
            return redirectTo;
        }
        return "/admin";
    }
}
