package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Redis-backed shopping cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/add")
    @Operation(summary = "Add an item to the cart", description = "If the product is already in the cart, its quantity is increased instead of duplicating the entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added, current cart returned"),
            @ApiResponse(responseCode = "400", description = "Validation failed on the request body")
    })
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        cartService.addItem(userId, request);
        return ResponseEntity.ok(buildResponse(userId));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully (empty cart returns an empty items list)")
    })
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(buildResponse(userId));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    @Operation(summary = "Remove one product from the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed, current cart returned")
    })
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok(buildResponse(userId));
    }

    @DeleteMapping("/{userId}/clear")
    @Operation(summary = "Empty the entire cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully")
    })
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
