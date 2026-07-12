package com.ecommerce.recommendation.service;

import com.ecommerce.recommendation.entity.UserInteraction;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollaborativeFilteringService {

    private final UserInteractionRepository interactionRepository;

    /**
     * Classic "people who interacted with what you did also interacted with
     * these" — implemented as three plain queries instead of one clever SQL
     * query, so it's easy to reason about and to explain in an interview:
     *
     *   1. What has this user already touched?
     *   2. Who else touched any of those same products? (similar users)
     *   3. What else did those similar users touch, that this user hasn't?
     *      -> rank by how many similar users touched each candidate.
     */
    public List<Long> findCandidateProductIds(Long userId, int limit) {
        List<Long> myProducts = interactionRepository.findDistinctProductIdsByUserId(userId);

        if (myProducts.isEmpty()) {
            // Cold start — brand new user, nothing to base similarity on yet.
            return interactionRepository.findTrendingProductIds().stream()
                    .limit(limit)
                    .toList();
        }

        List<Long> similarUserIds = interactionRepository.findSimilarUserIds(myProducts, userId);

        if (similarUserIds.isEmpty()) {
            return interactionRepository.findTrendingProductIds().stream()
                    .filter(id -> !myProducts.contains(id))
                    .limit(limit)
                    .toList();
        }

        List<UserInteraction> theirInteractions = interactionRepository.findByUserIdIn(similarUserIds);

        Map<Long, Long> scoreByProduct = theirInteractions.stream()
                .filter(interaction -> !myProducts.contains(interaction.getProductId()))
                .collect(Collectors.groupingBy(UserInteraction::getProductId, Collectors.counting()));

        return scoreByProduct.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
