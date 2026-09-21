package com.trainingdepot.gear.kit;

import java.math.BigDecimal;

import com.trainingdepot.gear.model.Product;

/**
 * A KIT line resolved against the live database, ready to render. Never
 * stored anywhere - built fresh by KitService each time the kit is viewed.
 */
public class KitLine {

    private final Product product;
    private final int quantity;
    private final boolean quantityReduced;
    private final int requestedQuantity;

    public KitLine(Product product, int quantity, int requestedQuantity) {
        this.product = product;
        this.quantity = quantity;
        this.requestedQuantity = requestedQuantity;
        this.quantityReduced = quantity < requestedQuantity;
    }

    public Product getProduct() {
        return product;
    }

    /** Quantity actually usable right now (capped to current stock). */
    public int getQuantity() {
        return quantity;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    /** True when the kit asked for more than is currently in stock. */
    public boolean isQuantityReduced() {
        return quantityReduced;
    }

    public BigDecimal getUnitPrice() {
        return product.getFinalPrice();
    }

    public BigDecimal getLineTotal() {
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
