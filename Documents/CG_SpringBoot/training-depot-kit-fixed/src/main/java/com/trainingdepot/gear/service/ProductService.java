package com.trainingdepot.gear.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;

public interface ProductService {

    /** Active records only, optionally filtered - either argument may be null. */
    List<Product> getStorefrontCatalog(EquipmentCategory category, String query);

    /** Throws ProductNotFoundException if missing or inactive. */
    Product getStorefrontProduct(int id);

    /** Active records among the given ids, in the order given, silently skipping any that don't resolve. Capped to 3 for the compare view. */
    List<Product> getStorefrontProducts(List<Integer> ids);

    /** All records, active and inactive, for the admin inventory list. */
    List<Product> getAllForAdmin();

    /** Any record regardless of active status - throws ProductNotFoundException if truly missing. */
    Product getForAdmin(int id);

    Product save(Product product);

    /** Soft-deletes (deactivates) if the record has order history, otherwise removes it outright. */
    void delete(int id);

    /** Returns null if the file is empty (caller should keep the previous image path). */
    String storeImage(MultipartFile file) throws IOException;
}
