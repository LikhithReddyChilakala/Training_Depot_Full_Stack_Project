package com.trainingdepot.gear.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByActiveTrueOrderByNameAsc();

    /**
     * One flexible query instead of a family of derived-name combinations:
     * category and query are both optional (pass null to skip a filter).
     *
     * @EntityGraph fetch-joins "specifications" in this same query so the
     * map is already populated by the time the transaction that Spring Data
     * wraps around every repository call closes - open-in-view is false, so
     * the JSP would otherwise hit LazyInitializationException trying to
     * iterate it. DISTINCT keeps the join from fanning a product with
     * several spec rows out into duplicate entries in the returned list.
     * This is one query for the whole list, regardless of product count -
     * not N+1.
     */
    @EntityGraph(attributePaths = "specifications")
    @Query("SELECT DISTINCT p FROM Product p WHERE p.active = true "
            + "AND (:category IS NULL OR p.category = :category) "
            + "AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) "
            + "ORDER BY p.name ASC")
    List<Product> search(@Param("category") EquipmentCategory category, @Param("query") String query);

    /** Same reasoning as search() above, for the single-record lookups (detail page, compare, admin edit). */
    @EntityGraph(attributePaths = "specifications")
    @Query("SELECT DISTINCT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithSpecifications(@Param("id") Integer id);

    boolean existsByCategory(EquipmentCategory category);

    /** Row-locking read used only inside the payment-verification transaction, to keep concurrent purchases from over-selling the last unit. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Integer id);
}
