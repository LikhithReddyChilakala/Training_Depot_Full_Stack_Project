package com.trainingdepot.gear.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.trainingdepot.gear.exception.ProductNotFoundException;
import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.repository.OrderItemRepository;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.service.ImageStorageService;
import com.trainingdepot.gear.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageStorageService imageStorageService;

    public ProductServiceImpl(ProductRepository productRepository, OrderItemRepository orderItemRepository,
            ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public List<Product> getStorefrontCatalog(EquipmentCategory category, String query) {
        String normalizedQuery = (query == null || query.isBlank()) ? null : query.trim();
        return productRepository.search(category, normalizedQuery);
    }

    @Override
    public Product getStorefrontProduct(int id) {
        Product product = productRepository.findByIdWithSpecifications(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (!product.isActive()) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    @Override
    public List<Product> getStorefrontProducts(List<Integer> ids) {
        List<Product> resolved = new ArrayList<>();
        for (Integer id : ids) {
            if (id == null || resolved.size() >= 3) {
                continue;
            }
            productRepository.findByIdWithSpecifications(id)
                    .filter(Product::isActive)
                    .ifPresent(resolved::add);
        }
        return resolved;
    }

    @Override
    public List<Product> getAllForAdmin() {
        return productRepository.findAll();
    }

    @Override
    public Product getForAdmin(int id) {
        return productRepository.findByIdWithSpecifications(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional
    public Product save(Product product) {
        product.applySpecInputsToMap();
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void delete(int id) {
        Product product = getForAdmin(id);
        if (orderItemRepository.existsByProduct_Id(id)) {
            // Has order history - keep the row for those orders' sake, just hide it.
            product.setActive(false);
            productRepository.save(product);
        } else {
            productRepository.delete(product);
        }
    }

    @Override
    public String storeImage(MultipartFile file) throws IOException {
        return imageStorageService.store(file);
    }
}
