package com.trainingdepot.gear.config;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedEnabled;
    private final String adminUsername;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository, ProductRepository productRepository,
            PasswordEncoder passwordEncoder,
            @Value("${depot.seed.enabled:true}") boolean seedEnabled,
            @Value("${depot.seed.admin-username:admin}") String adminUsername,
            @Value("${depot.seed.admin-password:DepotAdmin#2026}") String adminPassword) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            return;
        }
        seedAdmin();
        seedCatalog();
    }

    private void seedAdmin() {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setEmail(deriveAdminEmail(adminUsername));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Seeded depot admin account '{}' - change this password after first login.", adminUsername);
    }

    /**
     * The configured admin username doubles as the login identity. If it's
     * already an email address (the common case - e.g. "kadali@fitness.com"),
     * use it as-is instead of appending a domain and producing something
     * like "kadali@fitness.com@trainingdepot.local". Plain usernames (e.g.
     * "admin") keep the placeholder domain so User.email is never blank.
     */
    private String deriveAdminEmail(String username) {
        return username.contains("@") ? username : username + "@trainingdepot.local";
    }

    private void seedCatalog() {
        if (productRepository.count() > 0) {
            return;
        }
        java.util.List<Product> catalog = starterCatalog();
        for (Product product : catalog) {
            productRepository.save(product);
        }
        log.info("Seeded starter equipment catalog ({} records).", catalog.size());
    }

    private java.util.List<Product> starterCatalog() {
        java.util.List<Product> products = new java.util.ArrayList<>();

        products.add(build("5 KG Rubber Dumbbell",
                "Cast iron core with a rubber-coated shell for quiet, floor-safe reps.",
                new BigDecimal("999"), 0, 18, EquipmentCategory.DUMBBELLS,
                spec("Weight", "05 KG", "Material", "Cast Iron", "Coating", "Rubber", "Handle", "Knurled Steel")));

        products.add(build("10 KG Cast Iron Dumbbell",
                "Uncoated cast iron dumbbell for lifters who prefer a traditional finish.",
                new BigDecimal("1799"), 5, 12, EquipmentCategory.DUMBBELLS,
                spec("Weight", "10 KG", "Material", "Cast Iron", "Coating", "None", "Handle", "Knurled Steel")));

        products.add(build("20 KG Hex Dumbbell",
                "Hexagonal head keeps it from rolling on the depot floor between sets.",
                new BigDecimal("3299"), 0, 6, EquipmentCategory.DUMBBELLS,
                spec("Weight", "20 KG", "Material", "Cast Iron", "Head Shape", "Hexagonal", "Handle", "Chrome")));

        products.add(build("16 KG Kettlebell",
                "Single-piece cast kettlebell with a wide handle for two-hand swings.",
                new BigDecimal("2199"), 0, 4, EquipmentCategory.KETTLEBELLS,
                spec("Weight", "16 KG", "Material", "Cast Iron", "Handle Width", "33 MM", "Finish", "Powder Coat")));

        products.add(build("24 KG Kettlebell",
                "Competition-profile kettlebell for advanced conditioning work.",
                new BigDecimal("3499"), 0, 3, EquipmentCategory.KETTLEBELLS,
                spec("Weight", "24 KG", "Material", "Cast Iron", "Handle Width", "35 MM", "Finish", "Powder Coat")));

        products.add(build("Resistance Band - Medium",
                "Latex loop band rated for light-to-medium accessory work and warm-ups.",
                new BigDecimal("449"), 10, 40, EquipmentCategory.RESISTANCE_BANDS,
                spec("Resistance", "Medium (5-15 KG)", "Material", "Natural Latex", "Length", "600 MM", "Width", "13 MM")));

        products.add(build("Resistance Band - Heavy",
                "Higher-tension loop band for banded squats and lower-body accessory sets.",
                new BigDecimal("549"), 0, 25, EquipmentCategory.RESISTANCE_BANDS,
                spec("Resistance", "Heavy (15-35 KG)", "Material", "Natural Latex", "Length", "600 MM", "Width", "22 MM")));

        products.add(build("TPE Yoga Mat",
                "Lightly cushioned dual-layer mat with a textured, grip-first surface.",
                new BigDecimal("1299"), 15, 30, EquipmentCategory.YOGA,
                spec("Thickness", "08 MM", "Material", "TPE", "Length", "183 CM", "Width", "61 CM")));

        products.add(build("Cork Yoga Mat",
                "Natural cork surface that grips better the more you sweat on it.",
                new BigDecimal("2199"), 0, 10, EquipmentCategory.YOGA,
                spec("Thickness", "05 MM", "Material", "Cork + TPE Base", "Length", "183 CM", "Width", "66 CM")));

        products.add(build("Jump Rope - Speed",
                "Ball-bearing swivel and a PVC-coated cable for double-under work.",
                new BigDecimal("699"), 0, 22, EquipmentCategory.CARDIO_EQUIPMENT,
                spec("Cable", "PVC-Coated Steel", "Bearing", "Ball Bearing Swivel", "Handle", "Aluminium", "Length", "Adjustable")));

        products.add(build("Foldable Treadmill Board",
                "Compact walking-pad style deck for depot floors with limited space.",
                new BigDecimal("18999"), 8, 3, EquipmentCategory.CARDIO_EQUIPMENT,
                spec("Max Speed", "10 KM/H", "Deck Size", "1150 x 420 MM", "Fold", "Vertical", "Motor", "1.5 HP")));

        products.add(build("Olympic Weight Plate - 10 KG",
                "Standard 50mm-bore iron plate for barbell loading.",
                new BigDecimal("2499"), 0, 16, EquipmentCategory.STRENGTH_EQUIPMENT,
                spec("Weight", "10 KG", "Bore", "50 MM", "Material", "Cast Iron", "Finish", "Painted")));

        products.add(build("Adjustable Flat/Incline Bench",
                "Multi-position bench with a six-notch backrest for press variations.",
                new BigDecimal("7999"), 10, 5, EquipmentCategory.STRENGTH_EQUIPMENT,
                spec("Backrest Positions", "6", "Max Load", "200 KG", "Frame", "Powder-Coated Steel", "Pad", "High-Density Foam")));

        products.add(build("Lifting Straps",
                "Cotton straps for grip-limited pulling sets on deadlifts and rows.",
                new BigDecimal("399"), 0, 35, EquipmentCategory.GYM_ACCESSORIES,
                spec("Material", "Cotton Webbing", "Length", "560 MM", "Closure", "Loop", "Pair", "Yes")));

        products.add(build("Chalk Block - 4 Pack",
                "Magnesium carbonate blocks for grip on bars, plates, and kettlebell handles.",
                new BigDecimal("299"), 0, 50, EquipmentCategory.GYM_ACCESSORIES,
                spec("Material", "Magnesium Carbonate", "Blocks", "4", "Weight Each", "56 G", "Form", "Block")));

        products.add(build("Foam Roller - 45 CM",
                "Medium-density roller for post-session myofascial release.",
                new BigDecimal("899"), 0, 14, EquipmentCategory.RECOVERY_EQUIPMENT,
                spec("Length", "45 CM", "Diameter", "13 CM", "Density", "Medium", "Material", "EPP Foam")));

        products.add(build("Massage Gun - Compact",
                "Handheld percussion device with four head attachments for recovery work.",
                new BigDecimal("4499"), 12, 0, EquipmentCategory.RECOVERY_EQUIPMENT,
                spec("Speeds", "6", "Battery", "2400 mAh", "Attachments", "4", "Runtime", "~5 Hours")));

        return products;
    }

    private Product build(String name, String description, BigDecimal price, int discountPercent, int stock,
            EquipmentCategory category, Map<String, String> specs) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscountPercent(discountPercent);
        product.setStockQuantity(stock);
        product.setCategory(category);
        product.setActive(true);
        product.setSpecifications(specs);
        return product;
    }

    private Map<String, String> spec(String... keyValuePairs) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValuePairs.length; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }
}
