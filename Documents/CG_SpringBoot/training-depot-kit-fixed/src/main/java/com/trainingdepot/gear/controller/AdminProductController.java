package com.trainingdepot.gear.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.service.ProductService;

@Controller
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/admin/products")
    public String list(Model model) {
        model.addAttribute("products", productService.getAllForAdmin());
        return "admin/products";
    }

    @GetMapping("/admin/products/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", EquipmentCategory.values());
        model.addAttribute("isNew", true);
        return "admin/product-form";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        Product product = productService.getForAdmin(id);
        product.setSpecKeys(new ArrayList<>(product.getSpecifications().keySet()));
        product.setSpecValues(new ArrayList<>(product.getSpecifications().values()));
        model.addAttribute("product", product);
        model.addAttribute("categories", EquipmentCategory.values());
        model.addAttribute("isNew", false);
        return "admin/product-form";
    }

    @PostMapping("/admin/products")
    public String create(@ModelAttribute Product product, @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) throws IOException {
        String storedImage = productService.storeImage(image);
        if (storedImage != null) {
            product.setImagePath(storedImage);
        }
        Product saved = productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Equipment record " + saved.getRecordNumber() + " created.");
        return "redirect:/admin/products";
    }

    /**
     * Loads the existing managed record and copies the editable fields onto
     * it, rather than saving the freshly-bound form object directly - that
     * keeps the optimistic-lock version and any fields the form doesn't
     * carry (createdAt, id) intact.
     */
    @PostMapping("/admin/products/{id}")
    public String update(@PathVariable int id, @ModelAttribute Product form,
            @RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) throws IOException {
        Product existing = productService.getForAdmin(id);
        existing.setName(form.getName());
        existing.setDescription(form.getDescription());
        existing.setPrice(form.getPrice());
        existing.setDiscountPercent(form.getDiscountPercent());
        existing.setStockQuantity(form.getStockQuantity());
        existing.setCategory(form.getCategory());
        existing.setActive(form.isActive());
        existing.setSpecKeys(form.getSpecKeys());
        existing.setSpecValues(form.getSpecValues());

        String storedImage = productService.storeImage(image);
        if (storedImage != null) {
            existing.setImagePath(storedImage);
        }

        Product saved = productService.save(existing);
        redirectAttributes.addFlashAttribute("success", "Equipment record " + saved.getRecordNumber() + " updated.");
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Equipment record removed from the active floor.");
        return "redirect:/admin/products";
    }
}
