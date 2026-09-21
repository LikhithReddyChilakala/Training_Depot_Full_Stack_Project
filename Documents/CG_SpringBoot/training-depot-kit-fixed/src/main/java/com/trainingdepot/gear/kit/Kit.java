package com.trainingdepot.gear.kit;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The user-facing KIT (cart), stored as an HttpSession attribute.
 *
 * On purpose this holds only productId -> quantity, never a price or a JPA
 * entity. Every price shown anywhere (kit page, checkout, order creation) is
 * read fresh from the database at that moment, so a price or stock change
 * made by an admin is reflected immediately and nothing stale ever reaches
 * checkout. See KitService for how this is resolved into something
 * renderable.
 */
public class Kit implements Serializable {

    /**
     * Upper bound on a single line's quantity, independent of stock. This
     * isn't a business rule about how much of anything the depot sells - it
     * only exists so a malformed or adversarial request can't push a line
     * into an unreasonable value or overflow int arithmetic on repeated
     * adds. Real availability is enforced separately, against live stock,
     * in KitService#view and again at checkout.
     */
    public static final int MAX_QUANTITY_PER_LINE = 999;

    private final Map<Integer, Integer> items = new LinkedHashMap<>();

    public void add(int productId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        int bounded = Math.min(quantity, MAX_QUANTITY_PER_LINE);
        items.merge(productId, bounded, (existing, toAdd) -> Math.min(existing + toAdd, MAX_QUANTITY_PER_LINE));
    }

    public void setQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, Math.min(quantity, MAX_QUANTITY_PER_LINE));
        }
    }

    public void remove(int productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Defensive copy - so a caller iterating this (KitService#view) can never
     * ConcurrentModificationException against another request on the same
     * session mutating the kit at the same moment.
     */
    public Map<Integer, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }

    /** Total number of units across all lines - used for the KIT badge in the header. */
    public int totalUnitCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}
