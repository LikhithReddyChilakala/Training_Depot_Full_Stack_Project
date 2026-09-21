package com.trainingdepot.gear.model;

/**
 * Simple, fixed product classification - deliberately not a database-backed
 * entity/table. An enum gives type safety and clean filtering without a
 * category management subsystem.
 */
public enum EquipmentCategory {
    DUMBBELLS("Dumbbells"),
    KETTLEBELLS("Kettlebells"),
    RESISTANCE_BANDS("Resistance Bands"),
    YOGA("Yoga"),
    CARDIO_EQUIPMENT("Cardio Equipment"),
    STRENGTH_EQUIPMENT("Strength Equipment"),
    GYM_ACCESSORIES("Gym Accessories"),
    RECOVERY_EQUIPMENT("Recovery Equipment");

    private final String label;

    EquipmentCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
