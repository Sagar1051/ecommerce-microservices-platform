package com.ecommerce.recommendation.controller;

import com.ecommerce.recommendation.dto.RecommendationResponse;
import com.ecommerce.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> recommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(recommendationService.recommend(userId, limit));
    }
}
