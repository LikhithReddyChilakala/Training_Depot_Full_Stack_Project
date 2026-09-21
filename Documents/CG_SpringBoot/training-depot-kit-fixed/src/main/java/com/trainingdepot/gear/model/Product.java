package com.trainingdepot.gear.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

/**
 * An equipment record. Deliberately flat: no Brand relationship and no
 * variant model - a 5kg and a 10kg dumbbell are two separate Products.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Whole-number percentage off, 0-100. Not a monetary value, so a plain int is fine. */
    @Column(nullable = false)
    private int discountPercent = 0;

    @Column(nullable = false)
    private int stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EquipmentCategory category;

    /** Soft-delete flag: false hides the record from the storefront and admin list. */
    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 255)
    private String imagePath;

    @ElementCollection
    @CollectionTable(name = "product_specifications", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value", length = 255)
    private Map<String, String> specifications = new LinkedHashMap<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Optimistic lock - a light safety net alongside the pessimistic read-lock used at checkout. */
    @Version
    private Long version;

    // Parallel arrays used only by the add/edit equipment record forms; never persisted.
    @Transient
    private List<String> specKeys;
    @Transient
    private List<String> specValues;

    public List<String> getSpecKeys() {
        return specKeys;
    }

    public void setSpecKeys(List<String> specKeys) {
        this.specKeys = specKeys;
    }

    public List<String> getSpecValues() {
        return specValues;
    }

    public void setSpecValues(List<String> specValues) {
        this.specValues = specValues;
    }

    /** Rebuilds the specifications map from the form's parallel key/value arrays. */
    public void applySpecInputsToMap() {
        Map<String, String> rebuilt = new LinkedHashMap<>();
        if (specKeys != null && specValues != null) {
            int size = Math.min(specKeys.size(), specValues.size());
            for (int i = 0; i < size; i++) {
                String key = specKeys.get(i);
                String value = specValues.get(i);
                if (key != null && !key.isBlank()) {
                    rebuilt.put(key.trim(), value == null ? "" : value.trim());
                }
            }
        }
        this.specifications = rebuilt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    /** Authoritative sale price: price minus the discount, rounded to the rupee paisa. */
    public BigDecimal getFinalPrice() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountFraction = BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = price.multiply(discountFraction);
        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /** Used by the stock meter to decide the LOW threshold without a magic number scattered everywhere. */
    public boolean isLowStock() {
        return stockQuantity > 0 && stockQuantity <= 5;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Map<String, String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, String> specifications) {
        this.specifications = specifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    /** Stable, human-facing catalog id, e.g. "REC-014". Purely presentational. */
    public String getRecordNumber() {
        return id == null ? "REC-???" : String.format("REC-%03d", id);
    }
}
