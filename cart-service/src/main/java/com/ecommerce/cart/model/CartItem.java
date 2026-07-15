package com.ecommerce.cart.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Schema(example = "1", description = "Product id")
    private Long productId;

    @Schema(example = "Wireless Mouse", description = "Product name")
    private String productName;

    @Schema(example = "799.00", description = "Price at the time it was added to the cart")
    private Double price;

    @Schema(example = "2", description = "Number of units in the cart")
    private Integer quantity;
}
