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

/**
 * Groq hosts open-weight models (Llama, etc.) on custom inference chips and
 * offers a genuinely free tier with no credit card. The API is
 * OpenAI-compatible (chat completions shape), which is why this looks a
 * little different from a typical Anthropic Messages API call.
 */
@Component
@Slf4j
public class GroqClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${groq.api-key:}")
    private String apiKey;

    @Value("${groq.model:llama-3.1-8b-instant}")
    private String model;

    public GroqClient(@Qualifier("plainRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Sends the candidate products + the user's recent interaction history to
     * the model, asks it to pick the best {limit} and explain each one in a
     * short phrase. Returns productId -> reason. Returns an empty map (never
     * throws) if the API isn't configured or the call fails — callers should
     * fall back to the collaborative-filtering order with a generic reason.
     */
    public Map<Long, String> rankAndExplain(List<ProductDto> candidates, List<String> recentProductNames, int limit) {
        if (!isConfigured()) {
            log.debug("Groq API key not configured — skipping LLM re-ranking");
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
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(message));
        body.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        JsonNode response = restTemplate.postForObject(API_URL, request, JsonNode.class);

        if (response == null || !response.has("choices")) {
            throw new IllegalStateException("Unexpected Groq API response shape");
        }

        return response.get("choices").get(0).get("message").get("content").asText();
    }

    private Map<Long, String> parseResponse(String responseText) throws Exception {
        // Open models are chattier about wrapping JSON in markdown fences or
        // adding a stray sentence before it, despite instructions — strip
        // defensively before parsing.
        String cleaned = responseText.trim()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();

        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }

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
