package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .stockQuantity(request.stockQuantity())
                .imageUrl(request.imageUrl())
                .build();

        return toResponse(productRepository.save(product));
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<ProductResponse> findByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCase(category, pageable).map(this::toResponse);
    }

    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    /**
     * Fetches a single product and publishes a "viewed" event to Kafka.
     * userId is optional (null if the caller isn't authenticated) — the
     * recommendation-service will filter those out when building profiles.
     */
    @Transactional(readOnly = true)
    public ProductResponse findByIdAndRecordView(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        eventPublisher.publishViewedEvent(product, userId);

        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
