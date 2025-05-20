package com.alexduzi.dscommerce.services;

import com.alexduzi.dscommerce.dto.OrderDTO;
import com.alexduzi.dscommerce.dto.OrderItemDTO;
import com.alexduzi.dscommerce.entities.Order;
import com.alexduzi.dscommerce.entities.OrderItem;
import com.alexduzi.dscommerce.entities.OrderStatus;
import com.alexduzi.dscommerce.entities.Product;
import com.alexduzi.dscommerce.repositories.OrderItemRepository;
import com.alexduzi.dscommerce.repositories.OrderRepository;
import com.alexduzi.dscommerce.repositories.ProductRepository;
import com.alexduzi.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        order.setClient(userService.authenticated());

        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        repository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
