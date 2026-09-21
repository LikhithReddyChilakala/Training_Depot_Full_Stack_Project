package com.trainingdepot.gear.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.service.ProductService;

@Controller
public class EquipmentController {

    private final ProductService productService;

    public EquipmentController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/equipment")
    public String list(@RequestParam(required = false) EquipmentCategory category,
            @RequestParam(required = false) String q, Model model) {
        List<Product> results = productService.getStorefrontCatalog(category, q);
        model.addAttribute("results", results);
        model.addAttribute("categories", EquipmentCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("query", q);
        return "equipment-list";
    }

    @GetMapping("/equipment/{id}")
    public String detail(@PathVariable int id, Model model) {
        Product product = productService.getStorefrontProduct(id);
        model.addAttribute("product", product);
        return "equipment-detail";
    }

    @GetMapping("/equipment/compare")
    public String compare(@RequestParam String ids, Model model) {
        List<Integer> parsedIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
        List<Product> products = productService.getStorefrontProducts(parsedIds);
        model.addAttribute("products", products);
        return "compare";
    }
}
