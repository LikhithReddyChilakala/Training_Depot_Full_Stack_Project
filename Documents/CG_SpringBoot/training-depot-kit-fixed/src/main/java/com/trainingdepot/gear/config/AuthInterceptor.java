package com.trainingdepot.gear.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.trainingdepot.gear.model.User;

/** Requires depot access (login) for any route it's registered against. */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object user = request.getSession().getAttribute(SessionKeys.LOGGED_IN_USER);
        if (!(user instanceof User)) {
            String target = request.getRequestURI();
            String query = request.getQueryString();
            String redirectTarget = query == null ? target : target + "?" + query;
            response.sendRedirect(request.getContextPath() + "/login?redirectTo="
                    + java.net.URLEncoder.encode(redirectTarget, java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }
        return true;
    }
}
