package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
}

