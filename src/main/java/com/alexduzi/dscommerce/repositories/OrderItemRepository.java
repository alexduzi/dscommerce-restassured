package com.alexduzi.dscommerce.repositories;

import com.alexduzi.dscommerce.entities.OrderItem;
import com.alexduzi.dscommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
