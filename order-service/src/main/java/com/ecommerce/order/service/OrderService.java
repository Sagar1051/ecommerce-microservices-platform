package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClientResponse;
import com.ecommerce.order.client.CartServiceClient;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.EmptyCartException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResponse placeOrder(Long userId) {

        CartClientResponse cart = cartServiceClient.getCart(userId);

        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            throw new EmptyCartException(userId);
        }

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(BigDecimal.valueOf(cart.total()))
                .build();

        for (CartClientResponse.CartItemDto cartItem : cart.items()) {

            OrderItem item = OrderItem.builder()
                    .productId(cartItem.productId())
                    .productName(cartItem.productName())
                    .price(cartItem.price())
                    .quantity(cartItem.quantity())
                    .build();

            order.addItem(item);
        }

        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderPlaced(saved);
        cartServiceClient.clearCart(userId);

        return toResponse(saved);
    }

    public OrderResponse findById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return toResponse(order);
    }

    public List<OrderResponse> findByUser(Long userId) {

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {

        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}