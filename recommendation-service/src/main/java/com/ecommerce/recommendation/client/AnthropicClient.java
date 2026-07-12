package com.ecommerce.recommendation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AnthropicClient {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${anthropic.api-key:}")
    private String apiKey;

    @Value("${anthropic.model:claude-haiku-4-5-20251001}")
    private String model;

    public AnthropicClient(@Qualifier("plainRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Sends the candidate products + the user's recent interaction history to
     * Claude, asks it to pick the best {limit} and explain each one in a
     * short phrase. Returns productId -> reason. Returns an empty map (never
     * throws) if the API isn't configured or the call fails — callers should
     * fall back to the collaborative-filtering order with a generic reason.
     */
    public Map<Long, String> rankAndExplain(List<ProductDto> candidates, List<String> recentProductNames, int limit) {
        if (!isConfigured()) {
            log.debug("Anthropic API key not configured — skipping LLM re-ranking");
            return Map.of();
        }

        try {
            String prompt = buildPrompt(candidates, recentProductNames, limit);
            String responseText = callApi(prompt);
            return parseResponse(responseText);
        } catch (Exception ex) {
            log.warn("LLM re-ranking failed, falling back to plain collaborative filtering: {}", ex.getMessage());
            return Map.of();
        }
    }

    private String buildPrompt(List<ProductDto> candidates, List<String> recentProductNames, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("A shopper recently interacted with these products: ")
                .append(String.join(", ", recentProductNames))
                .append(".\n\n");
        sb.append("Here are candidate products to recommend to them:\n");
        for (ProductDto p : candidates) {
            sb.append("- id=").append(p.id())
                    .append(", name=\"").append(p.name())
                    .append("\", category=").append(p.category())
                    .append(", price=").append(p.price())
                    .append("\n");
        }
        sb.append("\nPick the best ").append(limit).append(" products for this shopper and explain each pick ")
                .append("in under 12 words. Respond with ONLY a JSON array, no other text, in this exact shape:\n")
                .append("[{\"productId\": 1, \"reason\": \"short reason here\"}]");
        return sb.toString();
    }

    private String callApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1024);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        JsonNode response = restTemplate.postForObject(API_URL, request, JsonNode.class);

        if (response == null || !response.has("content")) {
            throw new IllegalStateException("Unexpected Anthropic API response shape");
        }

        return response.get("content").get(0).get("text").asText();
    }

    private Map<Long, String> parseResponse(String responseText) throws Exception {
        // Claude occasionally wraps JSON in markdown fences despite instructions —
        // strip those defensively before parsing.
        String cleaned = responseText.trim()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();

        JsonNode array = objectMapper.readTree(cleaned);
        Map<Long, String> result = new java.util.LinkedHashMap<>();

        for (JsonNode node : array) {
            long productId = node.get("productId").asLong();
            String reason = node.get("reason").asText();
            result.put(productId, reason);
        }

        return result;
    }
}
