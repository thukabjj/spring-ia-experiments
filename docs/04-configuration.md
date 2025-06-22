# 🔧 Configuration Guide

## 📋 Configuration Overview

The Movie Classification System uses Spring Boot's flexible configuration system with multiple layers:

1. **Application Properties** - Default configuration
2. **Profile-specific Properties** - Environment-specific overrides
3. **Environment Variables** - Runtime configuration
4. **Command Line Arguments** - Deployment-specific settings
5. **External Configuration** - Production configuration files

## 🏗️ Configuration Architecture

```
Configuration Hierarchy (Higher priority overrides lower)
┌─────────────────────────────────────────────────┐
│ 1. Command Line Arguments                       │
├─────────────────────────────────────────────────┤
│ 2. Environment Variables                        │
├─────────────────────────────────────────────────┤
│ 3. External application-{profile}.properties   │
├─────────────────────────────────────────────────┤
│ 4. Internal application-{profile}.properties   │
├─────────────────────────────────────────────────┤
│ 5. Internal application.properties (default)   │
└─────────────────────────────────────────────────┘
```

## 📁 Configuration Files

### Main Configuration File
**Location**: `src/main/resources/application.properties`
```properties
# ===================================================================
# MOVIE CLASSIFICATION SYSTEM - MAIN CONFIGURATION
# ===================================================================

# ===================================================================
# SERVER CONFIGURATION
# ===================================================================
server.port=8080
server.servlet.context-path=/
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

# ===================================================================
# SPRING CONFIGURATION
# ===================================================================
spring.application.name=movie-classification-system
spring.profiles.active=dev
spring.main.banner-mode=console

# ===================================================================
# OLLAMA AI CONFIGURATION
# ===================================================================
# Base URL for Ollama service
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.timeout=60s
spring.ai.ollama.connection-timeout=30s
spring.ai.ollama.read-timeout=60s

# Chat model configuration
spring.ai.ollama.chat.model=llama2
spring.ai.ollama.chat.temperature=0.7
spring.ai.ollama.chat.top-p=1.0
spring.ai.ollama.chat.max-tokens=1000

# Embedding model configuration
spring.ai.ollama.embedding.model=nomic-embed-text
spring.ai.ollama.embedding.dimensions=768

# ===================================================================
# REDIS CONFIGURATION
# ===================================================================
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=2000ms
spring.redis.connect-timeout=1000ms

# Redis connection pool
spring.redis.lettuce.pool.max-active=20
spring.redis.lettuce.pool.max-idle=10
spring.redis.lettuce.pool.min-idle=2
spring.redis.lettuce.pool.max-wait=1000ms

# ===================================================================
# APPLICATION-SPECIFIC CONFIGURATION
# ===================================================================
# CSV file processing
app.csv.file-path=classpath:NLID.csv
app.csv.encoding=UTF-8
app.csv.skip-header=true
app.csv.delimiter=,

# Movie processing
app.processing.batch-size=50
app.processing.max-parallel-threads=4
app.processing.async-enabled=true
app.processing.timeout=300s

# Chunking configuration
app.chunking.strategy=fixed-size
app.chunking.chunk-size=200
app.chunking.overlap-size=50
app.chunking.max-chunks-per-movie=10

# Search configuration
app.search.default-limit=20
app.search.max-limit=100
app.search.similarity-threshold=0.7
app.search.enable-fuzzy-matching=true

# RAG configuration
app.rag.max-context-movies=10
app.rag.similarity-threshold=0.6
app.rag.max-answer-length=500
app.rag.include-sources=true

# ===================================================================
# CACHING CONFIGURATION
# ===================================================================
spring.cache.type=redis
spring.cache.redis.time-to-live=3600s
spring.cache.redis.cache-null-values=false

# Cache names and TTL
app.cache.movies.ttl=7200s
app.cache.search-results.ttl=1800s
app.cache.embeddings.ttl=86400s

# ===================================================================
# LOGGING CONFIGURATION
# ===================================================================
logging.level.com.techisthoughts.ia.movieclassification=INFO
logging.level.org.springframework.ai=INFO
logging.level.org.springframework.data.redis=WARN
logging.level.org.springframework.web=INFO

# Log file configuration
logging.file.name=logs/application.log
logging.file.max-size=100MB
logging.file.max-history=30
logging.logback.rollingpolicy.clean-history-on-start=true

# Log pattern
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# ===================================================================
# ACTUATOR CONFIGURATION (MONITORING)
# ===================================================================
management.endpoints.web.base-path=/actuator
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=when-authorized
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true

# Health indicators
management.health.redis.enabled=true
management.health.ping.enabled=true
management.health.diskspace.enabled=true

# Metrics configuration
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.tags.application=movie-classification

# ===================================================================
# SECURITY CONFIGURATION (PLACEHOLDER)
# ===================================================================
# Currently no security implemented
# TODO: Add authentication and authorization
management.security.enabled=false

# ===================================================================
# PERFORMANCE CONFIGURATION
# ===================================================================
# HTTP client configuration
spring.http.client.connect-timeout=30s
spring.http.client.read-timeout=60s

# Task execution
spring.task.execution.pool.core-size=4
spring.task.execution.pool.max-size=16
spring.task.execution.pool.queue-capacity=100
spring.task.execution.thread-name-prefix=movie-task-

# Task scheduling
spring.task.scheduling.pool.size=4
spring.task.scheduling.thread-name-prefix=movie-scheduled-
```

### Development Profile
**Location**: `src/main/resources/application-dev.properties`
```properties
# ===================================================================
# DEVELOPMENT PROFILE CONFIGURATION
# ===================================================================

# Development server settings
server.port=8080
server.error.include-stacktrace=always
server.error.include-message=always

# Enhanced logging for development
logging.level.com.techisthoughts.ia.movieclassification=DEBUG
logging.level.org.springframework.ai=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.data.redis=DEBUG

# Show SQL queries (if using JPA)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Development-friendly settings
app.processing.batch-size=10
app.search.default-limit=5
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

# Fast feedback for development
spring.ai.ollama.timeout=30s
app.processing.timeout=60s

# Disable caching for development
spring.cache.type=none
```

### Test Profile
**Location**: `src/main/resources/application-test.properties`
```properties
# ===================================================================
# TEST PROFILE CONFIGURATION
# ===================================================================

# Test server configuration
server.port=0
spring.main.banner-mode=off

# Disable external dependencies for testing
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
  org.springframework.boot.autoconfigure.ai.ollama.OllamaAutoConfiguration

# Test-specific settings
app.csv.file-path=classpath:test-movies.csv
app.processing.batch-size=5
app.search.default-limit=3

# Fast test execution
app.processing.async-enabled=false
app.processing.timeout=10s

# Test logging
logging.level.com.techisthoughts.ia.movieclassification=WARN
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

### Production Profile
**Location**: `src/main/resources/application-prod.properties`
```properties
# ===================================================================
# PRODUCTION PROFILE CONFIGURATION
# ===================================================================

# Production server settings
server.port=${SERVER_PORT:8080}
server.compression.enabled=true
server.http2.enabled=true

# External configuration
spring.config.import=optional:file:./config/application-prod.properties

# Production Ollama settings
spring.ai.ollama.base-url=${OLLAMA_BASE_URL:http://ollama-service:11434}
spring.ai.ollama.timeout=${OLLAMA_TIMEOUT:120s}

# Production Redis settings
spring.redis.host=${REDIS_HOST:redis-service}
spring.redis.port=${REDIS_PORT:6379}
spring.redis.password=${REDIS_PASSWORD:}
spring.redis.ssl=${REDIS_SSL:true}

# Connection pool for production load
spring.redis.lettuce.pool.max-active=${REDIS_POOL_MAX_ACTIVE:50}
spring.redis.lettuce.pool.max-idle=${REDIS_POOL_MAX_IDLE:20}
spring.redis.lettuce.pool.min-idle=${REDIS_POOL_MIN_IDLE:5}

# Production processing settings
app.processing.batch-size=${PROCESSING_BATCH_SIZE:100}
app.processing.max-parallel-threads=${PROCESSING_THREADS:8}
app.processing.timeout=${PROCESSING_TIMEOUT:600s}

# Production search limits
app.search.max-limit=${SEARCH_MAX_LIMIT:50}
app.rag.max-context-movies=${RAG_MAX_CONTEXT:20}

# Production logging
logging.level.com.techisthoughts.ia.movieclassification=${LOG_LEVEL:INFO}
logging.level.org.springframework.ai=${AI_LOG_LEVEL:WARN}
logging.file.name=${LOG_FILE:/var/log/movie-classification/application.log}

# Production monitoring
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=never
management.metrics.export.prometheus.enabled=true

# Production security (when implemented)
management.security.enabled=true
```

## 🔧 Configuration Classes

### Type-Safe Configuration Properties
**Location**: `src/main/java/com/techisthoughts/ia/movieclassification/config/ApplicationProperties.java`

```java
@ConfigurationProperties(prefix = "app")
@Data
@Component
public class ApplicationProperties {

    private Csv csv = new Csv();
    private Processing processing = new Processing();
    private Chunking chunking = new Chunking();
    private Search search = new Search();
    private Rag rag = new Rag();
    private Cache cache = new Cache();

    @Data
    public static class Csv {
        private String filePath = "classpath:NLID.csv";
        private String encoding = "UTF-8";
        private boolean skipHeader = true;
        private String delimiter = ",";
    }

    @Data
    public static class Processing {
        private int batchSize = 50;
        private int maxParallelThreads = 4;
        private boolean asyncEnabled = true;
        private Duration timeout = Duration.ofMinutes(5);
    }

    @Data
    public static class Chunking {
        private String strategy = "fixed-size";
        private int chunkSize = 200;
        private int overlapSize = 50;
        private int maxChunksPerMovie = 10;
    }

    @Data
    public static class Search {
        private int defaultLimit = 20;
        private int maxLimit = 100;
        private double similarityThreshold = 0.7;
        private boolean enableFuzzyMatching = true;
    }

    @Data
    public static class Rag {
        private int maxContextMovies = 10;
        private double similarityThreshold = 0.6;
        private int maxAnswerLength = 500;
        private boolean includeSources = true;
    }

    @Data
    public static class Cache {
        private Duration moviesTtl = Duration.ofHours(2);
        private Duration searchResultsTtl = Duration.ofMinutes(30);
        private Duration embeddingsTtl = Duration.ofHours(24);
    }
}
```

## 🌍 Environment Variables

### Production Environment Variables
```bash
# Server Configuration
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod

# Ollama Configuration
export OLLAMA_BASE_URL=http://ollama-service:11434
export OLLAMA_TIMEOUT=120s
export OLLAMA_CHAT_MODEL=llama2:13b
export OLLAMA_EMBEDDING_MODEL=nomic-embed-text

# Redis Configuration
export REDIS_HOST=redis-cluster-service
export REDIS_PORT=6379
export REDIS_PASSWORD=your-secure-password
export REDIS_SSL=true
export REDIS_POOL_MAX_ACTIVE=50

# Processing Configuration
export PROCESSING_BATCH_SIZE=100
export PROCESSING_THREADS=8
export PROCESSING_TIMEOUT=600s

# Search Configuration
export SEARCH_MAX_LIMIT=50
export RAG_MAX_CONTEXT=20

# Logging Configuration
export LOG_LEVEL=INFO
export AI_LOG_LEVEL=WARN
export LOG_FILE=/var/log/movie-classification/application.log

# JVM Configuration
export JAVA_OPTS="-Xmx8g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Docker Environment File
**Location**: `.env`
```bash
# Docker Compose Environment Variables
COMPOSE_PROJECT_NAME=movie-classification

# Application
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=docker

# Services
REDIS_PORT=6379
GRAFANA_PORT=3000
PROMETHEUS_PORT=9090
LOKI_PORT=3100
TEMPO_PORT=3200

# Ollama (external service)
OLLAMA_BASE_URL=http://host.docker.internal:11434

# Volumes
DATA_VOLUME=./data
LOGS_VOLUME=./logs
CONFIG_VOLUME=./config
```

## ⚙️ Configuration Validation

### Configuration Validation Class
```java
@Component
@Validated
public class ConfigurationValidator {

    @Autowired
    private ApplicationProperties applicationProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        validateProcessingConfiguration();
        validateSearchConfiguration();
        validateOllamaConfiguration();
        validateRedisConfiguration();
    }

    private void validateProcessingConfiguration() {
        Processing processing = applicationProperties.getProcessing();

        if (processing.getBatchSize() <= 0) {
            throw new IllegalArgumentException("Processing batch size must be positive");
        }

        if (processing.getMaxParallelThreads() <= 0) {
            throw new IllegalArgumentException("Max parallel threads must be positive");
        }
    }

    private void validateSearchConfiguration() {
        Search search = applicationProperties.getSearch();

        if (search.getDefaultLimit() > search.getMaxLimit()) {
            throw new IllegalArgumentException("Default limit cannot exceed max limit");
        }

        if (search.getSimilarityThreshold() < 0 || search.getSimilarityThreshold() > 1) {
            throw new IllegalArgumentException("Similarity threshold must be between 0 and 1");
        }
    }
}
```

## 📊 Configuration Monitoring

### Configuration Info Endpoint
```java
@Component
public class ConfigurationInfoContributor implements InfoContributor {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("configuration", Map.of(
            "processing", Map.of(
                "batchSize", applicationProperties.getProcessing().getBatchSize(),
                "asyncEnabled", applicationProperties.getProcessing().isAsyncEnabled()
            ),
            "search", Map.of(
                "defaultLimit", applicationProperties.getSearch().getDefaultLimit(),
                "maxLimit", applicationProperties.getSearch().getMaxLimit()
            )
        ));
    }
}
```

### Configuration Health Check
```java
@Component
public class ConfigurationHealthIndicator implements HealthIndicator {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public Health health() {
        try {
            // Validate critical configuration
            validateCriticalSettings();

            return Health.up()
                .withDetail("batchSize", applicationProperties.getProcessing().getBatchSize())
                .withDetail("searchLimit", applicationProperties.getSearch().getMaxLimit())
                .build();

        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withException(e)
                .build();
        }
    }

    private void validateCriticalSettings() {
        if (applicationProperties.getProcessing().getBatchSize() <= 0) {
            throw new IllegalStateException("Invalid batch size configuration");
        }
    }
}
```

## 🔧 Runtime Configuration Changes

### Configuration Refresh
```java
@RestController
@RequestMapping("/api/admin/config")
public class ConfigurationController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshConfiguration() {
        // Refresh configuration from external sources
        // This would typically reload from config server
        return ResponseEntity.ok("Configuration refreshed");
    }

    @GetMapping("/current")
    public ResponseEntity<ApplicationProperties> getCurrentConfiguration() {
        return ResponseEntity.ok(applicationProperties);
    }
}
```

## 📋 Configuration Checklist

### Development Setup
- [ ] `application-dev.properties` configured
- [ ] Debug logging enabled
- [ ] Small batch sizes for fast feedback
- [ ] All actuator endpoints exposed
- [ ] Caching disabled or minimal TTL

### Testing Setup
- [ ] `application-test.properties` configured
- [ ] External dependencies mocked/disabled
- [ ] Fast execution settings
- [ ] Minimal logging
- [ ] Test-specific data files

### Production Setup
- [ ] Environment variables configured
- [ ] External configuration files secured
- [ ] Appropriate batch sizes and limits
- [ ] Production logging levels
- [ ] Security settings enabled
- [ ] Monitoring endpoints configured
- [ ] Resource limits set
- [ ] Connection pooling configured

---

**Next**: [🏗️ System Architecture](./05-architecture.md)
