package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trainingdepot.gear.config.SessionKeys;
import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.service.UserService;

/**
 * The CUSTOMER authentication flow only. This never admits a Role.ADMIN
 * account - see the check in login() below - so the customer and admin
 * flows stay fully separate. Admin has its own AdminAuthController.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String redirectTo, HttpServletRequest request,
            Model model) {
        Object sessionUser = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);
        if (sessionUser instanceof User loggedInUser) {
            return "redirect:" + roleLandingPage(loggedInUser);
        }
        model.addAttribute("redirectTo", redirectTo);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
            @RequestParam(required = false) String redirectTo, HttpSession session,
            RedirectAttributes redirectAttributes) {
        return userService.authenticate(username, password)
                .map(user -> {
                    if (user.getRole() == Role.ADMIN) {
                        // Correct credentials, wrong door: do not let an
                        // admin account into a customer session.
                        redirectAttributes.addFlashAttribute("error",
                                "That account is a depot administrator account. Use Administrator Login instead.");
                        return "redirect:/login";
                    }
                    session.setAttribute(SessionKeys.LOGGED_IN_USER, user);
                    return "redirect:" + safeCustomerRedirectTarget(redirectTo);
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Depot access denied - check your username and password.");
                    return "redirect:/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm(HttpServletRequest request, Model model) {
        Object sessionUser = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);
        if (sessionUser instanceof User loggedInUser) {
            return "redirect:" + roleLandingPage(loggedInUser);
        }
        model.addAttribute("user", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegistrationForm form, RedirectAttributes redirectAttributes) {
        try {
            User created = userService.register(form.getUsername(), form.getPassword(), form.getEmail(),
                    form.getPhoneNumber());
            redirectAttributes.addFlashAttribute("success",
                    "Depot access created for " + created.getUsername() + ". Sign in below.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Never redirects a freshly-logged-in customer into /admin/**, even if
     * redirectTo was tampered with - AdminInterceptor would bounce them
     * right back out anyway, but there's no reason to send them there at all.
     */
    private String safeCustomerRedirectTarget(String redirectTo) {
        if (redirectTo != null && redirectTo.startsWith("/") && !redirectTo.startsWith("//")
                && !redirectTo.startsWith("/admin")) {
            return redirectTo;
        }
        return "/";
    }

    private String roleLandingPage(User user) {
        return user.getRole() == Role.ADMIN ? "/admin" : "/";
    }

    /** Plain form-backing object - kept out of the User entity so a raw password never touches JPA. */
    public static class RegistrationForm {
        private String username;
        private String password;
        private String email;
        private String phoneNumber;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
