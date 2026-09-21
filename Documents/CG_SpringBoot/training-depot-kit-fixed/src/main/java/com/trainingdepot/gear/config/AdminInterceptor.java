package com.trainingdepot.gear.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;

/**
 * Gates the entire /admin/** area by itself. Unlike the customer routes,
 * admin is not fronted by AuthInterceptor first - this interceptor owns
 * both the "not logged in" and "wrong role" checks, so the admin area has
 * its own login flow and redirect targets end to end, never the customer
 * ones. /admin/login is excluded from this interceptor in WebConfig so the
 * redirect below can't loop back on itself.
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object user = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);

        if (!(user instanceof User loggedInUser)) {
            String target = request.getRequestURI();
            String query = request.getQueryString();
            String redirectTarget = query == null ? target : target + "?" + query;
            response.sendRedirect(request.getContextPath() + "/admin/login?redirectTo="
                    + java.net.URLEncoder.encode(redirectTarget, java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }

        if (loggedInUser.getRole() != Role.ADMIN) {
            // A normal customer poking at admin URLs - send them back to
            // their own storefront rather than a 403 for what's likely just
            // a stray bookmark or a typed-in guess.
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true;
    }
}
