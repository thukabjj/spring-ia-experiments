package com.techisthoughts.ia.movieclassification.infrastructure.observability;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Comprehensive metrics service for observability
 * Tracks tokens, performance, errors, and business metrics
 */
@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    // Token tracking
    private final Counter inputTokensCounter;
    private final Counter outputTokensCounter;
    private final AtomicLong totalInputTokens = new AtomicLong(0);
    private final AtomicLong totalOutputTokens = new AtomicLong(0);

    // Performance metrics
    private final Timer embeddingCreationTimer;
    private final Timer questionGenerationTimer;
    private final Timer chunkProcessingTimer;
    private final Timer endToEndProcessingTimer;

    // Cache metrics
    private final Counter cacheHitsCounter;
    private final Counter cacheMissesCounter;
    private final AtomicLong cacheSize = new AtomicLong(0);

    // Business metrics
    private final Counter moviesProcessedCounter;
    private final Counter chunksCreatedCounter;
    private final Counter questionsGeneratedCounter;
    private final Counter embeddingsCreatedCounter;

    // Error tracking
    private final Counter errorsCounter;
    private final Counter circuitBreakerTripsCounter;
    private final Counter retryAttemptsCounter;

    // Performance strategy tracking
    private final Map<String, Counter> strategyUsageCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> strategyPerformanceTimers = new ConcurrentHashMap<>();

    // Real-time metrics
    private final AtomicReference<Double> currentThroughput = new AtomicReference<>(0.0);
    private final AtomicReference<Duration> averageResponseTime = new AtomicReference<>(Duration.ZERO);

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Initialize token counters
        this.inputTokensCounter = Counter.builder("llm.tokens.input")
            .description("Total input tokens processed")
            .register(meterRegistry);

        this.outputTokensCounter = Counter.builder("llm.tokens.output")
            .description("Total output tokens generated")
            .register(meterRegistry);

        // Initialize performance timers
        this.embeddingCreationTimer = Timer.builder("llm.embedding.creation.time")
            .description("Time to create embeddings")
            .register(meterRegistry);

        this.questionGenerationTimer = Timer.builder("llm.question.generation.time")
            .description("Time to generate questions")
            .register(meterRegistry);

        this.chunkProcessingTimer = Timer.builder("processing.chunk.time")
            .description("Time to process a single chunk")
            .register(meterRegistry);

        this.endToEndProcessingTimer = Timer.builder("processing.end.to.end.time")
            .description("Total end-to-end processing time")
            .register(meterRegistry);

        // Initialize cache metrics
        this.cacheHitsCounter = Counter.builder("cache.hits")
            .description("Cache hit count")
            .register(meterRegistry);

        this.cacheMissesCounter = Counter.builder("cache.misses")
            .description("Cache miss count")
            .register(meterRegistry);

        // Initialize business metrics
        this.moviesProcessedCounter = Counter.builder("business.movies.processed")
            .description("Total movies processed")
            .register(meterRegistry);

        this.chunksCreatedCounter = Counter.builder("business.chunks.created")
            .description("Total chunks created")
            .register(meterRegistry);

        this.questionsGeneratedCounter = Counter.builder("business.questions.generated")
            .description("Total questions generated")
            .register(meterRegistry);

        this.embeddingsCreatedCounter = Counter.builder("business.embeddings.created")
            .description("Total embeddings created")
            .register(meterRegistry);

        // Initialize error counters
        this.errorsCounter = Counter.builder("errors.total")
            .description("Total errors encountered")
            .register(meterRegistry);

        this.circuitBreakerTripsCounter = Counter.builder("circuit.breaker.trips")
            .description("Circuit breaker activations")
            .register(meterRegistry);

        this.retryAttemptsCounter = Counter.builder("retry.attempts")
            .description("Retry attempts made")
            .register(meterRegistry);

                // Register gauges for real-time metrics
        Gauge.builder("tokens.input.total", this, m -> m.totalInputTokens.get())
            .description("Total input tokens processed")
            .register(meterRegistry);

        Gauge.builder("tokens.output.total", this, m -> m.totalOutputTokens.get())
            .description("Total output tokens generated")
            .register(meterRegistry);

        Gauge.builder("cache.size", this, m -> m.cacheSize.get())
            .description("Current cache size")
            .register(meterRegistry);

        Gauge.builder("performance.throughput.current", this, m -> m.currentThroughput.get())
            .description("Current throughput (items/sec)")
            .register(meterRegistry);

        Gauge.builder("performance.response.time.average.ms", this, m -> m.averageResponseTime.get().toMillis())
            .description("Average response time in milliseconds")
            .register(meterRegistry);
    }

    // Token tracking methods
    public void recordInputTokens(long tokens) {
        inputTokensCounter.increment(tokens);
        totalInputTokens.addAndGet(tokens);
    }

    public void recordOutputTokens(long tokens) {
        outputTokensCounter.increment(tokens);
        totalOutputTokens.addAndGet(tokens);
    }

    public void recordTokenUsage(long inputTokens, long outputTokens) {
        recordInputTokens(inputTokens);
        recordOutputTokens(outputTokens);
    }

    // Performance timing methods
    public Timer.Sample startEmbeddingTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordEmbeddingCreation(Timer.Sample sample) {
        sample.stop(embeddingCreationTimer);
    }

    public Timer.Sample startQuestionGenerationTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordQuestionGeneration(Timer.Sample sample) {
        sample.stop(questionGenerationTimer);
    }

    public Timer.Sample startChunkProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordChunkProcessing(Timer.Sample sample) {
        sample.stop(chunkProcessingTimer);
    }

    public Timer.Sample startEndToEndTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordEndToEndProcessing(Timer.Sample sample) {
        sample.stop(endToEndProcessingTimer);
    }

    // Cache metrics
    public void recordCacheHit() {
        cacheHitsCounter.increment();
    }

    public void recordCacheMiss() {
        cacheMissesCounter.increment();
    }

    public void updateCacheSize(long size) {
        cacheSize.set(size);
    }

    // Business metrics
    public void recordMoviesProcessed(long count) {
        moviesProcessedCounter.increment(count);
    }

    public void recordChunksCreated(long count) {
        chunksCreatedCounter.increment(count);
    }

    public void recordQuestionsGenerated(long count) {
        questionsGeneratedCounter.increment(count);
    }

    public void recordEmbeddingsCreated(long count) {
        embeddingsCreatedCounter.increment(count);
    }

    // Error tracking
    public void recordError(String errorType) {
        Counter.builder("errors.total")
            .description("Total errors encountered")
            .tag("type", errorType)
            .register(meterRegistry)
            .increment();
    }

    public void recordCircuitBreakerTrip() {
        circuitBreakerTripsCounter.increment();
    }

    public void recordRetryAttempt() {
        retryAttemptsCounter.increment();
    }

    // Strategy performance tracking
    public void recordStrategyUsage(String strategy) {
        strategyUsageCounters.computeIfAbsent(strategy, s ->
            Counter.builder("strategy.usage")
                .description("Strategy usage count")
                .tag("strategy", s)
                .register(meterRegistry)
        ).increment();
    }

    public Timer.Sample startStrategyTimer(String strategy) {
        return Timer.start(meterRegistry);
    }

    public void recordStrategyPerformance(String strategy, Timer.Sample sample) {
        Timer timer = strategyPerformanceTimers.computeIfAbsent(strategy, s ->
            Timer.builder("strategy.performance")
                .description("Strategy performance timing")
                .tag("strategy", s)
                .register(meterRegistry)
        );
        sample.stop(timer);
    }

    // Real-time metrics updates
    public void updateThroughput(double throughput) {
        currentThroughput.set(throughput);
    }

    public void updateAverageResponseTime(Duration responseTime) {
        averageResponseTime.set(responseTime);
    }

    // Metrics aggregation for dashboard
    public Map<String, Object> getMetricsSummary() {
        return Map.of(
            "tokens", Map.of(
                "inputTotal", totalInputTokens.get(),
                "outputTotal", totalOutputTokens.get(),
                "totalProcessed", totalInputTokens.get() + totalOutputTokens.get()
            ),
            "performance", Map.of(
                "currentThroughput", currentThroughput.get(),
                "averageResponseTimeMs", averageResponseTime.get().toMillis(),
                "embeddingCreationAvgMs", embeddingCreationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS),
                "questionGenerationAvgMs", questionGenerationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
            ),
            "cache", Map.of(
                "hits", cacheHitsCounter.count(),
                "misses", cacheMissesCounter.count(),
                "hitRate", calculateCacheHitRate(),
                "size", cacheSize.get()
            ),
            "business", Map.of(
                "moviesProcessed", moviesProcessedCounter.count(),
                "chunksCreated", chunksCreatedCounter.count(),
                "questionsGenerated", questionsGeneratedCounter.count(),
                "embeddingsCreated", embeddingsCreatedCounter.count()
            ),
            "errors", Map.of(
                "total", errorsCounter.count(),
                "circuitBreakerTrips", circuitBreakerTripsCounter.count(),
                "retryAttempts", retryAttemptsCounter.count()
            )
        );
    }

    private double calculateCacheHitRate() {
        double hits = cacheHitsCounter.count();
        double total = hits + cacheMissesCounter.count();
        return total > 0 ? hits / total : 0.0;
    }

    // Get detailed metrics for specific strategy
    public Map<String, Object> getStrategyMetrics(String strategy) {
        Counter usageCounter = strategyUsageCounters.get(strategy);
        Timer performanceTimer = strategyPerformanceTimers.get(strategy);

        return Map.of(
            "strategy", strategy,
            "usageCount", usageCounter != null ? usageCounter.count() : 0,
            "averageTimeMs", performanceTimer != null ?
                performanceTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS) : 0,
            "totalTimeMs", performanceTimer != null ?
                performanceTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) : 0
        );
    }
}
