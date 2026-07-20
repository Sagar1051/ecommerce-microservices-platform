package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and retrieval")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/place")
    @Operation(
            summary = "Place an order from the user's current cart",
            description = "Reads the cart via a service-to-service call to cart-service, saves the " +
                    "order, publishes an OrderPlacedEvent to Kafka, then clears the cart."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty — nothing to order")
    })
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable Long userId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(userId));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get a single order by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.findById(orderId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user", description = "Most recent orders first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully (empty list if none)")
    })
    public ResponseEntity<List<OrderResponse>> getOrdersForUser(@PathVariable Long userId) {

        return ResponseEntity.ok(orderService.findByUser(userId));
    }
}