package com.trainingdepot.gear.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.service.ProductService;

import org.springframework.ui.Model;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> catalog = productService.getStorefrontCatalog(null, null);
        // A small, varied preview for the landing page - not the full catalog.
        List<Product> preview = catalog.stream().limit(6).toList();
        model.addAttribute("preview", preview);
        model.addAttribute("categories", EquipmentCategory.values());
        model.addAttribute("recordCount", catalog.size());
        return "home";
    }
}
