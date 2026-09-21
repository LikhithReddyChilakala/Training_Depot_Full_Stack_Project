package com.trainingdepot.gear.kit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Everything a kit.jsp or checkout.jsp needs, computed server-side. */
public class KitView {

    private final List<KitLine> lines;
    private final List<Integer> removedProductIds;

    public KitView(List<KitLine> lines, List<Integer> removedProductIds) {
        this.lines = lines;
        this.removedProductIds = removedProductIds;
    }

    public List<KitLine> getLines() {
        return lines;
    }

    /** Product ids that were in the kit but no longer resolve to an active product. */
    public List<Integer> getRemovedProductIds() {
        return removedProductIds;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public boolean hasIssues() {
        if (!removedProductIds.isEmpty()) {
            return true;
        }
        return lines.stream().anyMatch(KitLine::isQuantityReduced);
    }

    public int getTotalUnitCount() {
        return lines.stream().mapToInt(KitLine::getQuantity).sum();
    }

    public BigDecimal getSubtotal() {
        return lines.stream()
                .map(KitLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
