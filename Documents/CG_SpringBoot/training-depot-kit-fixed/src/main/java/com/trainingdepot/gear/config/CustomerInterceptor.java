package com.trainingdepot.gear.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;

/**
 * Keeps the customer storefront USER-only. Registered on the same
 * customer-facing routes as AuthInterceptor and always runs after it, so by
 * the time this fires we already know someone is logged in - we only need
 * to steer an ADMIN session away from customer pages.
 *
 * A redirect back to /admin (not a 403) is deliberate: an administrator
 * clicking a stale link or typing /checkout isn't attacking the app, they
 * just don't belong there, and their own dashboard is the useful place to
 * send them.
 */
@Component
public class CustomerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object user = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);
        if (user instanceof User loggedInUser && loggedInUser.getRole() == Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return false;
        }
        return true;
    }
}
