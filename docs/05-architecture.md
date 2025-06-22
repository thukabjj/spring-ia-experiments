# 🏗️ System Architecture

## Overview

The Movie Classification System follows **Clean Architecture** principles with **Hexagonal Architecture** patterns, implementing a sophisticated **RAG (Retrieval-Augmented Generation)** system for intelligent movie analysis.

## 🏛️ High-Level Architecture

```mermaid
graph TB
    subgraph "External Systems"
        CSV[NLID Dataset CSV]
        OLLAMA[Ollama AI Service]
        REDIS[Redis Vector DB]
        LGTM[LGTM Stack]
    end

    subgraph "Application"
        subgraph "Presentation Layer"
            API[REST Controllers]
            DTO[DTOs & Responses]
        end

        subgraph "Application Layer"
            UC[Use Cases]
            SERV[Application Services]
        end

        subgraph "Domain Layer"
            ENT[Entities & Models]
            PORT[Ports/Interfaces]
            DOM_SERV[Domain Services]
        end

        subgraph "Infrastructure Layer"
            ADAPT[Adapters]
            CONFIG[Configuration]
            OBS[Observability]
        end
    end

    CSV --> ADAPT
    API --> UC
    UC --> DOM_SERV
    DOM_SERV --> PORT
    PORT --> ADAPT
    ADAPT --> OLLAMA
    ADAPT --> REDIS
    OBS --> LGTM
```

## 🎯 Clean Architecture Layers

### 1. Presentation Layer (`presentation/`)
**Responsibility**: Handle HTTP requests/responses and user interactions

#### Components:
- **Controllers**: REST endpoint handlers
- **DTOs**: Data transfer objects for API contracts
- **Exception Handlers**: Global error handling
- **Validators**: Input validation

#### Key Classes:
- `MovieController`: Movie search and retrieval endpoints
- `RAGController`: Question answering endpoints
- `ObservabilityController`: Health and metrics endpoints

```java
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final ProcessMoviesUseCase processMoviesUseCase;

    @GetMapping("/search")
    public ResponseEntity<MovieSearchResponse> searchMovies(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String genre,
        @RequestParam(defaultValue = "20") int limit
    ) {
        // Delegate to use case
    }
}
```

### 2. Application Layer (`application/`)
**Responsibility**: Orchestrate business operations and use cases

#### Components:
- **Use Cases**: Application-specific business rules
- **Services**: Application services for complex operations
- **Command/Query Objects**: Request/response models

#### Key Classes:
- `ProcessMoviesUseCase`: Main movie processing orchestrator
- `ChunkingService`: Text chunking for embeddings
- `QuestionGenerationService`: Generate questions from content

```java
@Service
public class ProcessMoviesUseCase {

    public void processMovies(List<Movie> movies) {
        // 1. Chunk movie content
        // 2. Generate embeddings
        // 3. Store in vector database
        // 4. Generate questions
    }
}
```

### 3. Domain Layer (`domain/`)
**Responsibility**: Core business logic and rules

#### Components:
- **Entities**: Core business objects
- **Value Objects**: Immutable domain concepts
- **Ports**: Interface definitions
- **Domain Services**: Business logic services

#### Key Classes:
- `Movie`: Core movie entity
- `MovieChunk`: Text chunk with embeddings
- `Question`: Generated questions about movies
- `MovieRepositoryPort`: Repository abstraction
- `LLMServicePort`: AI service abstraction

```java
public class Movie {
    private String title;
    private String genre;
    private Double userRating;
    private String reviewHighlights;

    // Domain methods
    public boolean isHighlyRated() {
        return userRating != null && userRating >= 8.0;
    }

    public boolean isSuitableForFamily() {
        return percentageSuggestedToFriendsFamily >= 70.0;
    }
}
```

### 4. Infrastructure Layer (`infrastructure/`)
**Responsibility**: External system integrations and technical concerns

#### Components:
- **Adapters**: External system implementations
- **Configuration**: Framework and library setup
- **Observability**: Monitoring and logging

#### Key Classes:
- `OllamaLLMService`: Ollama AI integration
- `InMemoryVectorDatabase`: Vector storage implementation
- `CsvMovieLoader`: Dataset loading adapter
- `MetricsService`: Performance monitoring

## 🔄 Data Flow Architecture

### 1. Movie Processing Flow
```mermaid
sequenceDiagram
    participant CSV as CSV Dataset
    participant Loader as CsvMovieLoader
    participant UseCase as ProcessMoviesUseCase
    participant Chunking as ChunkingService
    participant LLM as OllamaLLMService
    participant VectorDB as VectorDatabase

    CSV->>Loader: Load NLID.csv
    Loader->>UseCase: List<Movie>
    UseCase->>Chunking: Chunk content
    Chunking->>LLM: Generate embeddings
    LLM->>VectorDB: Store vectors
    UseCase->>VectorDB: Store movie data
```

### 2. Search & Retrieval Flow
```mermaid
sequenceDiagram
    participant Client as API Client
    participant Controller as MovieController
    participant Repository as MovieRepository
    participant VectorDB as VectorDatabase
    participant LLM as LLMService

    Client->>Controller: Search request
    Controller->>Repository: Find movies
    Repository->>VectorDB: Vector search
    VectorDB->>LLM: Generate query embedding
    LLM->>VectorDB: Similarity search
    VectorDB->>Repository: Matching movies
    Repository->>Controller: Search results
    Controller->>Client: Response
```

### 3. RAG Question Answering Flow
```mermaid
sequenceDiagram
    participant Client as Client
    participant RAGController as RAGController
    participant VectorDB as VectorDatabase
    participant LLM as LLMService

    Client->>RAGController: Ask question
    RAGController->>VectorDB: Retrieve context
    VectorDB->>LLM: Generate query embedding
    LLM->>VectorDB: Find relevant movies
    VectorDB->>RAGController: Context movies
    RAGController->>LLM: Generate answer
    LLM->>RAGController: Response
    RAGController->>Client: Final answer
```

## 🧩 Component Architecture

### Core Components Interaction

```mermaid
graph LR
    subgraph "Domain Core"
        Movie[Movie Entity]
        MovieChunk[Movie Chunk]
        Question[Question Entity]
    end

    subgraph "Ports"
        MoviePort[Movie Repository Port]
        LLMPort[LLM Service Port]
        VectorPort[Vector DB Port]
    end

    subgraph "Adapters"
        CsvAdapter[CSV Loader]
        OllamaAdapter[Ollama Service]
        RedisAdapter[Redis Vector DB]
    end

    Movie --> MoviePort
    MovieChunk --> VectorPort
    Question --> MoviePort

    MoviePort --> CsvAdapter
    LLMPort --> OllamaAdapter
    VectorPort --> RedisAdapter
```

## 🚀 Deployment Architecture

### Local Development Environment
```mermaid
graph TB
    subgraph "Docker Compose Stack"
        App[Spring Boot App<br/>:8080]
        Redis[Redis<br/>:6379]
        Grafana[Grafana<br/>:3000]
        Prometheus[Prometheus<br/>:9090]
        Loki[Loki<br/>:3100]
        Tempo[Tempo<br/>:3200]
    end

    subgraph "External Services"
        Ollama[Ollama<br/>:11434]
    end

    App --> Redis
    App --> Ollama
    App --> Prometheus
    App --> Loki
    App --> Tempo
    Grafana --> Prometheus
    Grafana --> Loki
    Grafana --> Tempo
```

### Production Architecture (Planned)
```mermaid
graph TB
    subgraph "Load Balancer"
        LB[Load Balancer]
    end

    subgraph "Application Tier"
        App1[App Instance 1]
        App2[App Instance 2]
        App3[App Instance 3]
    end

    subgraph "Data Tier"
        Redis1[Redis Primary]
        Redis2[Redis Replica]
        VectorDB[Vector Database]
    end

    subgraph "AI Tier"
        Ollama1[Ollama Instance 1]
        Ollama2[Ollama Instance 2]
    end

    subgraph "Observability"
        Prometheus[Prometheus]
        Grafana[Grafana]
        Loki[Loki]
    end

    LB --> App1
    LB --> App2
    LB --> App3

    App1 --> Redis1
    App2 --> Redis1
    App3 --> Redis1

    Redis1 --> Redis2

    App1 --> Ollama1
    App2 --> Ollama2
    App3 --> Ollama1
```

## 🔧 Configuration Architecture

### Property Management
```yaml
# Hierarchical configuration
application.properties (default)
├── application-dev.properties
├── application-test.properties
├── application-prod.properties
└── application-local.properties
```

### Configuration Classes
```java
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Processing processing = new Processing();
    private Search search = new Search();
    private Ollama ollama = new Ollama();

    @Data
    public static class Processing {
        private int batchSize = 50;
        private int parallelism = 4;
    }

    @Data
    public static class Search {
        private int defaultLimit = 20;
        private int maxLimit = 100;
        private double similarityThreshold = 0.7;
    }
}
```

## 📊 Performance Architecture

### Caching Strategy
```mermaid
graph LR
    Client --> AppCache[Application Cache<br/>L1 - In Memory]
    AppCache --> RedisCache[Redis Cache<br/>L2 - Distributed]
    RedisCache --> VectorDB[Vector Database<br/>L3 - Persistent]

    subgraph "Cache Levels"
        AppCache
        RedisCache
        VectorDB
    end
```

### Async Processing
```java
@Async("taskExecutor")
public CompletableFuture<List<MovieChunk>> processMovieChunks(Movie movie) {
    // Parallel chunk processing
    return CompletableFuture.completedFuture(chunks);
}
```

## 🔒 Security Architecture

### Current State
- **No Authentication**: Open API access
- **No Authorization**: All endpoints accessible
- **No Rate Limiting**: Unlimited requests

### Planned Security Architecture
```mermaid
graph TB
    subgraph "Security Layer"
        AuthFilter[Authentication Filter]
        AuthzFilter[Authorization Filter]
        RateLimit[Rate Limiting]
    end

    subgraph "Identity Provider"
        JWT[JWT Tokens]
        OAuth[OAuth 2.0]
    end

    Client --> AuthFilter
    AuthFilter --> JWT
    AuthFilter --> AuthzFilter
    AuthzFilter --> RateLimit
    RateLimit --> Controller
```

## 📈 Monitoring Architecture

### Observability Stack (LGTM)
```mermaid
graph TB
    subgraph "Application"
        App[Spring Boot App]
        Metrics[Micrometer Metrics]
        Logs[Logback Logs]
        Traces[Spring Cloud Sleuth]
    end

    subgraph "Collection"
        Prometheus[Prometheus<br/>Metrics Collection]
        Loki[Loki<br/>Log Aggregation]
        Tempo[Tempo<br/>Trace Collection]
    end

    subgraph "Visualization"
        Grafana[Grafana<br/>Dashboards & Alerts]
    end

    Metrics --> Prometheus
    Logs --> Loki
    Traces --> Tempo

    Prometheus --> Grafana
    Loki --> Grafana
    Tempo --> Grafana
```

## 🧪 Testing Architecture

### Test Strategy
```mermaid
graph TB
    subgraph "Unit Tests"
        UnitDomain[Domain Tests]
        UnitService[Service Tests]
        UnitController[Controller Tests]
    end

    subgraph "Integration Tests"
        IntegAPI[API Tests]
        IntegDB[Database Tests]
        IntegAI[AI Service Tests]
    end

    subgraph "End-to-End Tests"
        E2EFlow[Complete Flow Tests]
        E2EPerf[Performance Tests]
    end

    UnitDomain --> IntegAPI
    UnitService --> IntegDB
    UnitController --> IntegAI
    IntegAPI --> E2EFlow
    IntegDB --> E2EPerf
```

## 🔄 Evolution Strategy

### Phase 1: Foundation (Current)
- ✅ Clean Architecture implementation
- ✅ RAG system basics
- ✅ Basic observability

### Phase 2: Enhancement (Planned)
- 🔄 Security implementation
- 🔄 Performance optimization
- 🔄 Advanced caching

### Phase 3: Scale (Future)
- 📋 Microservices decomposition
- 📋 Event-driven architecture
- 📋 Multi-tenant support

---

**Next**: [🔌 API Documentation](./06-api-documentation.md)
