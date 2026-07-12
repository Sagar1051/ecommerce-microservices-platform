package com.ecommerce.recommendation.service;

import com.ecommerce.recommendation.client.AnthropicClient;
import com.ecommerce.recommendation.client.ProductDto;
import com.ecommerce.recommendation.client.ProductServiceClient;
import com.ecommerce.recommendation.dto.RecommendationResponse;
import com.ecommerce.recommendation.entity.UserInteraction;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int CANDIDATE_POOL_SIZE = 15;

    private final CollaborativeFilteringService collaborativeFilteringService;
    private final ProductServiceClient productServiceClient;
    private final AnthropicClient anthropicClient;
    private final UserInteractionRepository interactionRepository;

    public RecommendationResponse recommend(Long userId, int limit) {
        // Gather a wider candidate pool than we'll return — gives the LLM
        // actual choices to pick between rather than just re-labeling
        // whatever collaborative filtering already ranked first.
        List<Long> candidateIds = collaborativeFilteringService.findCandidateProductIds(userId, CANDIDATE_POOL_SIZE);

        if (candidateIds.isEmpty()) {
            return RecommendationResponse.builder()
                    .userId(userId)
                    .recommendations(List.of())
                    .aiRanked(false)
                    .build();
        }

        List<ProductDto> candidates = productServiceClient.fetchProducts(candidateIds);
        List<String> recentProductNames = getRecentProductNames(userId);

        Map<Long, String> llmReasons = anthropicClient.rankAndExplain(candidates, recentProductNames, limit);
        boolean aiRanked = !llmReasons.isEmpty();

        List<RecommendationResponse.RecommendationItem> items = aiRanked
                ? buildFromLlmOrder(candidates, llmReasons)
                : buildFromCollaborativeOrder(candidates, limit);

        return RecommendationResponse.builder()
                .userId(userId)
                .recommendations(items)
                .aiRanked(aiRanked)
                .build();
    }

    private List<String> getRecentProductNames(Long userId) {
        List<Long> recentProductIds = interactionRepository.findByUserIdIn(List.of(userId)).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(UserInteraction::getProductId)
                .toList();

        if (recentProductIds.isEmpty()) {
            return List.of("nothing yet — this is a new shopper");
        }

        return productServiceClient.fetchProducts(recentProductIds).stream()
                .map(ProductDto::name)
                .toList();
    }

    private List<RecommendationResponse.RecommendationItem> buildFromLlmOrder(
            List<ProductDto> candidates, Map<Long, String> llmReasons) {

        Map<Long, ProductDto> byId = candidates.stream()
                .collect(java.util.stream.Collectors.toMap(ProductDto::id, p -> p));

        return llmReasons.entrySet().stream()
                .map(entry -> {
                    ProductDto product = byId.get(entry.getKey());
                    if (product == null) return null;
                    return toItem(product, entry.getValue());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<RecommendationResponse.RecommendationItem> buildFromCollaborativeOrder(
            List<ProductDto> candidates, int limit) {
        return candidates.stream()
                .limit(limit)
                .map(p -> toItem(p, "Popular with shoppers who bought similar items"))
                .toList();
    }

    private RecommendationResponse.RecommendationItem toItem(ProductDto product, String reason) {
        return RecommendationResponse.RecommendationItem.builder()
                .productId(product.id())
                .name(product.name())
                .category(product.category())
                .price(product.price())
                .reason(reason)
                .build();
    }
}
