package com.ecommerce.recommendation.dto;

import com.ecommerce.recommendation.client.ProductDto;

public record RankedRecommendation(
        ProductDto product,
        String reason
) {}
