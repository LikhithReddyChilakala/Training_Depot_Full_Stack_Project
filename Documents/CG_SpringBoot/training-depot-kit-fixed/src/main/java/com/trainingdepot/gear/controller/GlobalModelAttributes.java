package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.trainingdepot.gear.service.KitService;

@ControllerAdvice
public class GlobalModelAttributes {

    private final KitService kitService;

    public GlobalModelAttributes(KitService kitService) {
        this.kitService = kitService;
    }

    @ModelAttribute("kitCount")
    public int kitCount(HttpSession session) {
        return kitService.totalUnitCount(session);
    }
}
