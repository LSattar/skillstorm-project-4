package com.skillstorm.animalshelter.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class OllamaEmbeddingProvider implements EmbeddingProvider {

    private static final Logger log = LoggerFactory.getLogger(OllamaEmbeddingProvider.class);
    private static final String MATCHING_MODE_OLLAMA = "HYBRID";

    private final HttpClient httpClient;
    private final Map<String, double[]> cache = new ConcurrentHashMap<>();
    private final AtomicBoolean aiReady = new AtomicBoolean(false);
    private final boolean aiMatchingEnabled;
    private final String baseUrl;
    private final String model;
    private final int timeoutMs;

    public OllamaEmbeddingProvider(
            @Value("${app.ai.matching-enabled:true}") boolean aiMatchingEnabled,
            @Value("${app.ai.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${app.ai.ollama.embed-model:nomic-embed-text}") String model,
            @Value("${app.ai.ollama.timeout-ms:1200}") int timeoutMs) {
        this.aiMatchingEnabled = aiMatchingEnabled;
        this.baseUrl = baseUrl;
        this.model = model;
        this.timeoutMs = timeoutMs;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @PostConstruct
    public void startupProbe() {
        if (!aiMatchingEnabled) {
            log.info("AI mode: rules-only fallback (app.ai.matching-enabled=false)");
            return;
        }
        if (probe()) {
            aiReady.set(true);
            log.info("AI mode: ollama (model={}, baseUrl={})", model, baseUrl);
            return;
        }
        aiReady.set(false);
        log.warn("AI mode: rules-only fallback (Ollama probe failed at startup)");
    }

    @Scheduled(fixedDelayString = "${app.ai.ollama.recheck-ms:90000}")
    public void recheckAvailability() {
        if (!aiMatchingEnabled) {
            aiReady.set(false);
            return;
        }
        if (aiReady.get()) {
            return;
        }
        if (probe()) {
            aiReady.set(true);
            log.info("Ollama recovered; AI mode switched to {}", MATCHING_MODE_OLLAMA);
        }
    }

    @Override
    public Optional<double[]> embed(String cacheKey, String text) {
        if (!isReady() || text == null || text.isBlank()) {
            return Optional.empty();
        }
        double[] cached = cache.get(cacheKey);
        if (cached != null) {
            return Optional.of(cached);
        }
        Optional<double[]> vector = requestEmbedding(text);
        vector.ifPresent(v -> cache.put(cacheKey, v));
        return vector;
    }

    @Override
    public boolean isReady() {
        return aiMatchingEnabled && aiReady.get();
    }

    private boolean probe() {
        return requestEmbedding("ollama health check").isPresent();
    }

    private Optional<double[]> requestEmbedding(String text) {
        String body = "{\"model\":\"" + escape(model) + "\",\"prompt\":\"" + escape(text) + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/embeddings"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(timeoutMs))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                aiReady.set(false);
                log.warn("Ollama embeddings request failed status={}", response.statusCode());
                return Optional.empty();
            }
            int arrayStart = response.body().indexOf('[');
            int arrayEnd = response.body().indexOf(']', arrayStart);
            if (arrayStart < 0 || arrayEnd <= arrayStart) {
                aiReady.set(false);
                log.warn("Ollama embeddings response missing vector");
                return Optional.empty();
            }
            String[] parts = response.body().substring(arrayStart + 1, arrayEnd).split(",");
            if (parts.length == 0) {
                aiReady.set(false);
                log.warn("Ollama embeddings response had empty vector");
                return Optional.empty();
            }
            double[] vector = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Double.parseDouble(parts[i].trim());
            }
            aiReady.set(true);
            return Optional.of(vector);
        } catch (IOException | InterruptedException | NumberFormatException e) {
            aiReady.set(false);
            log.warn("Ollama embeddings request error: {}", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
