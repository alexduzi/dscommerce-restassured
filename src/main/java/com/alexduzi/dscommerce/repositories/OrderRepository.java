package com.alexduzi.dscommerce.repositories;

import com.alexduzi.dscommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
