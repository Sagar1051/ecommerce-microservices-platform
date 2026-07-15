package com.ecommerce.recommendation.controller;

import com.ecommerce.recommendation.dto.RecommendationResponse;
import com.ecommerce.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recommendations", description = "AI-powered product recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get personalized recommendations for a user",
            description = "Collaborative filtering finds candidates from Kafka-derived interaction " +
                    "history, then Groq (Llama 3.1) re-ranks and explains them. Falls back to plain " +
                    "collaborative filtering if the LLM call fails or isn't configured."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully " +
                    "(empty list for a brand-new user with no interaction history yet)")
    })
    public ResponseEntity<RecommendationResponse> recommend(
            @PathVariable Long userId,
            @Parameter(description = "Max number of recommendations to return")
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(recommendationService.recommend(userId, limit));
    }
}
