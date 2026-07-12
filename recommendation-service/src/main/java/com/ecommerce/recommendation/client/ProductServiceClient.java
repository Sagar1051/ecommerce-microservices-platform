package com.ecommerce.recommendation.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    public ProductServiceClient(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProductDto> fetchProducts(List<Long> productIds) {
        return productIds.stream()
                .map(this::fetchProduct)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<ProductDto> fetchProduct(Long id) {
        try {
            ProductDto product = restTemplate.getForObject(
                    "http://product-service/api/products/" + id, ProductDto.class);
            return Optional.ofNullable(product);
        } catch (RestClientException ex) {
            // A candidate product that's since been deleted shouldn't blow up
            // the whole recommendation request — just skip it.
            log.warn("Could not fetch productId={} from product-service: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }
}
