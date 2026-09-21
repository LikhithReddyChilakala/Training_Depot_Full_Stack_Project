package com.trainingdepot.gear.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
