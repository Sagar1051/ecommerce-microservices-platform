package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;

    private String cartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    public void addItem(Long userId, AddToCartRequest request) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        String key = cartKey(userId);
        String field = request.productId().toString();

        CartItem existing = (CartItem) hashOps.get(key, field);

        if (existing != null) {
            // Already in cart — bump the quantity instead of duplicating the entry
            existing.setQuantity(existing.getQuantity() + request.quantity());
            hashOps.put(key, field, existing);
        } else {
            CartItem newItem = new CartItem(
                    request.productId(),
                    request.productName(),
                    request.price(),
                    request.quantity()
            );
            hashOps.put(key, field, newItem);
        }
    }

    public List<CartItem> getCart(Long userId) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Map<Object, Object> entries = hashOps.entries(cartKey(userId));

        return entries.values().stream()
                .map(item -> (CartItem) item)
                .toList();
    }

    public void removeItem(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(cartKey(userId), productId.toString());
    }

    public void clearCart(Long userId) {
        redisTemplate.delete(cartKey(userId));
    }

    public double getCartTotal(Long userId) {
        return getCart(userId).stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
