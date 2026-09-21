package com.trainingdepot.gear.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.trainingdepot.gear.exception.ProductNotFoundException;
import com.trainingdepot.gear.kit.Kit;
import com.trainingdepot.gear.kit.KitLine;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.service.KitService;

@Service
public class KitServiceImpl implements KitService {

    private static final String SESSION_KEY = "depotKit";

    private final ProductRepository productRepository;

    public KitServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Kit getKit(HttpSession session) {
        Object existing = session.getAttribute(SESSION_KEY);
        if (existing instanceof Kit kit) {
            return kit;
        }
        Kit fresh = new Kit();
        session.setAttribute(SESSION_KEY, fresh);
        return fresh;
    }

    @Override
    public void add(HttpSession session, int productId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        requireActiveProduct(productId);
        getKit(session).add(productId, quantity);
    }

    @Override
    public void updateQuantity(HttpSession session, int productId, int quantity) {
        if (quantity <= 0) {
            // Same contract as remove() - dropping a line never needs the
            // product to still exist.
            getKit(session).setQuantity(productId, quantity);
            return;
        }
        requireActiveProduct(productId);
        getKit(session).setQuantity(productId, quantity);
    }

    @Override
    public void remove(HttpSession session, int productId) {
        getKit(session).remove(productId);
    }

    @Override
    public void clear(HttpSession session) {
        getKit(session).clear();
    }

    @Override
    public KitView view(HttpSession session) {
        Kit kit = getKit(session);
        List<KitLine> lines = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : kit.getItems().entrySet()) {
            int productId = entry.getKey();
            int requestedQuantity = entry.getValue();
            Optional<Product> product = productRepository.findById(productId).filter(Product::isActive);
            if (product.isEmpty()) {
                removed.add(productId);
                continue;
            }
            int usableQuantity = Math.min(requestedQuantity, Math.max(product.get().getStockQuantity(), 0));
            if (usableQuantity <= 0) {
                // Out of stock entirely - keep it visible as a zero-quantity line so the
                // shopper can see it and remove it, rather than silently vanishing.
                lines.add(new KitLine(product.get(), 0, requestedQuantity));
            } else {
                lines.add(new KitLine(product.get(), usableQuantity, requestedQuantity));
            }
        }
        return new KitView(lines, removed);
    }

    @Override
    public int totalUnitCount(HttpSession session) {
        return getKit(session).totalUnitCount();
    }

    /**
     * Add and update-to-a-positive-quantity must not let a nonexistent or
     * inactive product id into the session kit in the first place - view()
     * already copes gracefully with a product that was valid when added and
     * later removed/deactivated, but there's no reason to accept a bad id at
     * the door.
     */
    private void requireActiveProduct(int productId) {
        productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
