package com.trainingdepot.gear.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.trainingdepot.gear.exception.OrderNotFoundException;
import com.trainingdepot.gear.exception.ProductNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ ProductNotFoundException.class, OrderNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    /**
     * Browsers request /favicon.ico (and sometimes similar paths) unprompted
     * on nearly every page load. A missing optional static asset is a
     * normal, expected 404 - not an application error - so this is handled
     * ahead of the generic Exception handler below and does not log
     * anything or render the 500 page.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleMissingStaticResource() {
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception e, Model model) {
        // Log the real detail server-side; never show a stack trace to the shopper.
        log.error("Unhandled exception reached the depot floor", e);
        return "error/500";
    }
}
