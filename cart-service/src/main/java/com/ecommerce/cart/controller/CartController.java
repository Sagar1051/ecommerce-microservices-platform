package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        cartService.addItem(userId, request);
        return ResponseEntity.ok(buildResponse(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(buildResponse(userId));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok(buildResponse(userId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private CartResponse buildResponse(Long userId) {
        return CartResponse.builder()
                .items(cartService.getCart(userId))
                .total(cartService.getCartTotal(userId))
                .build();
    }
}
