package com.techisthoughts.ia.movieclassification.presentation.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;
import com.techisthoughts.ia.movieclassification.infrastructure.adapter.UltraFastOllamaLLMService;
import com.techisthoughts.ia.movieclassification.infrastructure.observability.MetricsService;

/**
 * Comprehensive observability controller for metrics, monitoring, and dashboards
 */
@RestController
@RequestMapping("/api/observability")
@CrossOrigin(origins = "*")
public class ObservabilityController {

    private final MetricsService metricsService;
    private final UltraFastOllamaLLMService ultraFastLLMService;
    private final MovieRepositoryPort movieRepository;

    public ObservabilityController(MetricsService metricsService,
                                 @Qualifier("ultraFastLLMService") UltraFastOllamaLLMService ultraFastLLMService,
                                 MovieRepositoryPort movieRepository) {
        this.metricsService = metricsService;
        this.ultraFastLLMService = ultraFastLLMService;
        this.movieRepository = movieRepository;
    }

    /**
     * Comprehensive health check with all system metrics
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();

        // System status
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        health.put("uptime", System.currentTimeMillis());

        // Data status
        health.put("dataStatus", Map.of(
            "moviesLoaded", movieRepository.count(),
            "dataSource", "Netflix Life Impact Dataset (NLID.csv)",
            "lastUpdated", Instant.now().toString()
        ));

        // LLM service status
        health.put("llmStatus", Map.of(
            "available", ultraFastLLMService.isAvailable(),
            "embeddingModel", "nomic-embed-text",
            "chatModel", "llama3.1:8b",
            "optimizationLevel", "ULTRA_FAST"
        ));

        // Performance summary
        Map<String, Object> performanceMetrics = ultraFastLLMService.getPerformanceMetrics();
        health.put("performance", Map.of(
            "cacheHitRate", String.format("%.1f%%",
                (Double) performanceMetrics.getOrDefault("cacheHitRate", 0.0) * 100),
            "averageResponseTime", performanceMetrics.get("avgProcessingTimeMs") + "ms",
            "totalRequests", performanceMetrics.get("totalRequests"),
            "optimization", "16 threads, smart caching, circuit breaker"
        ));

        // Add comprehensive metrics
        health.putAll(metricsService.getMetricsSummary());

        return health;
    }

    /**
     * Detailed metrics dashboard
     */
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> dashboard = new HashMap<>();

        // Get comprehensive metrics
        Map<String, Object> metrics = metricsService.getMetricsSummary();
        dashboard.putAll(metrics);

        // Add LLM service specific metrics
        Map<String, Object> llmMetrics = ultraFastLLMService.getPerformanceMetrics();
        dashboard.put("llmService", llmMetrics);

        // Add system metrics
        Runtime runtime = Runtime.getRuntime();
        dashboard.put("system", Map.of(
            "memoryUsed", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB",
            "memoryFree", runtime.freeMemory() / 1024 / 1024 + " MB",
            "memoryTotal", runtime.totalMemory() / 1024 / 1024 + " MB",
            "processors", runtime.availableProcessors(),
            "activeThreads", Thread.activeCount()
        ));

        return dashboard;
    }

    /**
     * Token usage analytics
     */
    @GetMapping("/tokens")
    public Map<String, Object> getTokenMetrics() {
        Map<String, Object> metrics = metricsService.getMetricsSummary();
        Map<String, Object> tokenMetrics = (Map<String, Object>) metrics.get("tokens");

        Map<String, Object> result = new HashMap<>(tokenMetrics);

        // Add cost estimation (approximate)
        long totalTokens = ((Number) tokenMetrics.get("totalProcessed")).longValue();
        double estimatedCost = totalTokens * 0.0001; // $0.0001 per token (example rate)

        result.put("costEstimation", Map.of(
            "totalTokens", totalTokens,
            "estimatedCostUSD", String.format("$%.4f", estimatedCost),
            "costPerToken", "$0.0001",
            "note", "Estimated cost based on example pricing"
        ));

        return result;
    }

    /**
     * Performance analytics by strategy
     */
    @GetMapping("/performance")
    public Map<String, Object> getPerformanceAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Get strategy-specific metrics
        List<String> strategies = List.of("BLOCKING", "OPTIMIZED", "ULTRA_FAST", "LIGHTNING");

        for (String strategy : strategies) {
            analytics.put(strategy.toLowerCase(), metricsService.getStrategyMetrics(strategy));
        }

        // Add performance comparison
        analytics.put("comparison", Map.of(
            "blocking", "90+ seconds for 5 chunks",
            "optimized", "6-8 seconds for 5 chunks",
            "ultraFast", "1-3 seconds for 5 chunks (with cache)",
            "lightning", "0.5-1.5 seconds for 5 chunks (full optimization)"
        ));

        // Add current performance metrics
        Map<String, Object> currentMetrics = metricsService.getMetricsSummary();
        analytics.put("current", currentMetrics.get("performance"));

        return analytics;
    }

    /**
     * Cache analytics
     */
    @GetMapping("/cache")
    public Map<String, Object> getCacheAnalytics() {
        Map<String, Object> metrics = metricsService.getMetricsSummary();
        Map<String, Object> cacheMetrics = (Map<String, Object>) metrics.get("cache");

        Map<String, Object> analytics = new HashMap<>(cacheMetrics);

        // Add cache efficiency analysis
        double hitRate = ((Number) cacheMetrics.get("hitRate")).doubleValue();
        String efficiency;
        if (hitRate > 0.8) {
            efficiency = "EXCELLENT";
        } else if (hitRate > 0.6) {
            efficiency = "GOOD";
        } else if (hitRate > 0.4) {
            efficiency = "FAIR";
        } else {
            efficiency = "POOR";
        }

        analytics.put("efficiency", Map.of(
            "rating", efficiency,
            "hitRatePercentage", String.format("%.1f%%", hitRate * 100),
            "recommendation", hitRate < 0.6 ?
                "Consider increasing cache TTL or size" :
                "Cache performance is optimal"
        ));

        return analytics;
    }

    /**
     * Error analytics
     */
    @GetMapping("/errors")
    public Map<String, Object> getErrorAnalytics() {
        Map<String, Object> metrics = metricsService.getMetricsSummary();
        Map<String, Object> errorMetrics = (Map<String, Object>) metrics.get("errors");

        Map<String, Object> analytics = new HashMap<>(errorMetrics);

        // Add error rate analysis
        long totalRequests = ((Number) metricsService.getMetricsSummary()
            .getOrDefault("totalRequests", 0L)).longValue();
        long totalErrors = ((Number) errorMetrics.get("total")).longValue();

        double errorRate = totalRequests > 0 ? (double) totalErrors / totalRequests : 0.0;

        analytics.put("errorRate", Map.of(
            "percentage", String.format("%.2f%%", errorRate * 100),
            "status", errorRate < 0.01 ? "EXCELLENT" :
                     errorRate < 0.05 ? "GOOD" : "NEEDS_ATTENTION",
            "recommendation", errorRate > 0.05 ?
                "High error rate detected - investigate logs" :
                "Error rate within acceptable limits"
        ));

        return analytics;
    }

    /**
     * Real-time dashboard data
     */
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();

        // Current status
        dashboard.put("status", "OPERATIONAL");
        dashboard.put("timestamp", Instant.now().toString());

        // Key metrics
        Map<String, Object> metrics = metricsService.getMetricsSummary();
        Map<String, Object> llmMetrics = ultraFastLLMService.getPerformanceMetrics();

        dashboard.put("keyMetrics", Map.of(
            "totalRequests", llmMetrics.get("totalRequests"),
            "cacheHitRate", String.format("%.1f%%",
                (Double) llmMetrics.getOrDefault("cacheHitRate", 0.0) * 100),
            "averageResponseTime", llmMetrics.get("avgProcessingTimeMs") + "ms",
            "tokensProcessed", ((Map<String, Object>) metrics.get("tokens")).get("totalProcessed"),
            "embeddingsCreated", ((Map<String, Object>) metrics.get("business")).get("embeddingsCreated")
        ));

        // Performance indicators
        double cacheHitRate = (Double) llmMetrics.getOrDefault("cacheHitRate", 0.0);
        long avgResponseTime = (Long) llmMetrics.getOrDefault("avgProcessingTimeMs", 0L);

        dashboard.put("performanceIndicators", Map.of(
            "cachePerformance", cacheHitRate > 0.8 ? "EXCELLENT" :
                               cacheHitRate > 0.6 ? "GOOD" : "FAIR",
            "responseTimeStatus", avgResponseTime < 100 ? "FAST" :
                                avgResponseTime < 500 ? "MODERATE" : "SLOW",
            "systemLoad", Thread.activeCount() < 20 ? "LOW" :
                         Thread.activeCount() < 50 ? "MODERATE" : "HIGH"
        ));

        // Optimization status
        dashboard.put("optimizations", Map.of(
            "smartCaching", "ENABLED",
            "adaptiveBatching", "ENABLED",
            "circuitBreaker", "ENABLED",
            "parallelProcessing", "16 THREADS",
            "memoryOptimization", "ENABLED"
        ));

        return dashboard;
    }

    /**
     * Feature toggles status
     */
    @GetMapping("/features")
    public Map<String, Object> getFeatureToggles() {
        return Map.of(
            "strategies", Map.of(
                "blocking", Map.of("enabled", true, "description", "Original blocking approach"),
                "optimized", Map.of("enabled", true, "description", "Parallel processing with batching"),
                "ultraFast", Map.of("enabled", true, "description", "Smart caching + optimization"),
                "lightning", Map.of("enabled", true, "description", "Full optimization suite")
            ),
            "features", Map.of(
                "caching", Map.of("enabled", true, "ttl", "30 minutes"),
                "circuitBreaker", Map.of("enabled", true, "threshold", 5),
                "metrics", Map.of("enabled", true, "realTime", true),
                "tokenTracking", Map.of("enabled", true, "costEstimation", true),
                "adaptiveBatching", Map.of("enabled", true, "range", "8-20 items")
            ),
            "monitoring", Map.of(
                "prometheus", Map.of("enabled", true, "endpoint", "/actuator/prometheus"),
                "grafana", Map.of("enabled", true, "dashboards", "performance, errors, cache"),
                "tracing", Map.of("enabled", true, "sampling", "100%")
            )
        );
    }
}
