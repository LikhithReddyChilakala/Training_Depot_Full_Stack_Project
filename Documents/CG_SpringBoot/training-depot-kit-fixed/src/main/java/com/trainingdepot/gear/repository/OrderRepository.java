package com.trainingdepot.gear.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "user")
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    @EntityGraph(attributePaths = "user")
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN FETCH o.user
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product
        WHERE o.id = :id
    """)
    Optional<Order> findByIdWithDetails(Long id);
}