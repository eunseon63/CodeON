package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.app.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
