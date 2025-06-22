# 💻 Development Guide

## 🎯 Development Environment Setup

### Prerequisites
- **Java 21+**: Use SDKMAN for easy version management
- **Maven 3.8+**: For dependency management
- **Docker & Docker Compose**: For local infrastructure
- **Git**: Version control
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse
- **Ollama**: AI model serving platform

### SDKMAN Setup (Recommended)
```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Install Java 21
sdk install java 21.0.1-tem
sdk use java 21.0.1-tem

# Install Maven
sdk install maven 3.9.6
```

### IDE Configuration

#### IntelliJ IDEA
```xml
<!-- .idea/modules.xml settings -->
<module fileurl="file://$PROJECT_DIR$/movie-classification.iml" filepath="$PROJECT_DIR$/movie-classification.iml" />
```

#### VS Code Extensions
```json
{
  "recommendations": [
    "vscode-java-pack",
    "spring-boot-extension-pack",
    "docker",
    "redhat.vscode-yaml"
  ]
}
```

## 🏗️ Project Structure Deep Dive

### Clean Architecture Organization
```
src/main/java/com/techisthoughts/ia/movieclassification/
├── 📁 presentation/           # Controllers, DTOs, REST Layer
│   ├── controller/            # REST Controllers
│   └── dto/                  # Data Transfer Objects
├── 📁 application/           # Use Cases, Application Services
│   ├── service/              # Application Services
│   └── usecase/              # Business Use Cases
├── 📁 domain/               # Core Business Logic
│   ├── model/               # Domain Entities
│   ├── port/                # Interface Definitions
│   └── service/             # Domain Services (if any)
├── 📁 infrastructure/       # External Concerns
│   ├── adapter/             # External System Adapters
│   ├── config/              # Spring Configuration
│   └── observability/       # Monitoring & Metrics
├── 📁 config/               # Application Configuration
└── 📁 llm/                  # AI/ML Configuration
```

### Key Design Principles

#### 1. Dependency Rule
```java
// ✅ Good: Domain doesn't depend on infrastructure
public class Movie {
    // Pure domain logic, no external dependencies
}

// ❌ Bad: Domain depending on framework
public class Movie extends JpaEntity {
    // Violates dependency rule
}
```

#### 2. Interface Segregation
```java
// ✅ Good: Focused interfaces
public interface MovieRepositoryPort {
    List<Movie> findByGenre(String genre);
    Optional<Movie> findByTitle(String title);
}

public interface VectorDatabasePort {
    void storeEmbedding(String id, float[] embedding);
    List<SimilarityResult> findSimilar(float[] query, int limit);
}
```

#### 3. Single Responsibility
```java
// ✅ Good: Single responsibility
@Service
public class ChunkingService {
    public List<MovieChunk> chunkMovie(Movie movie) {
        // Only handles text chunking
    }
}

@Service
public class EmbeddingService {
    public float[] generateEmbedding(String text) {
        // Only handles embedding generation
    }
}
```

## 🔧 Development Workflow

### 1. Feature Development Process

#### Branching Strategy
```bash
# Create feature branch
git checkout -b feature/new-search-algorithm

# Work on feature
git add .
git commit -m "feat: implement semantic search algorithm"

# Push and create PR
git push origin feature/new-search-algorithm
```

#### Commit Message Convention
```bash
# Format: type(scope): description

feat(search): add semantic similarity search
fix(api): resolve null pointer in movie controller
docs(readme): update installation instructions
test(unit): add tests for chunking service
refactor(domain): extract movie validation logic
```

### 2. Local Development Setup

#### Start Infrastructure
```bash
# Start all required services
docker compose up -d

# Check services are running
docker compose ps
```

#### Start Application
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using IDE
# Run MovieClassificationApplication.main()

# Option 3: With custom profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Development Properties
```properties
# src/main/resources/application-dev.properties

# Fast feedback for development
spring.jpa.show-sql=true
logging.level.com.techisthoughts=DEBUG
logging.level.org.springframework.ai=DEBUG

# Reduced batch sizes for faster testing
app.processing.batch-size=10
app.search.default-limit=5

# Development-specific settings
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

### 3. Testing Strategy

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class ChunkingServiceTest {

    @Mock
    private ApplicationProperties properties;

    @InjectMocks
    private ChunkingService chunkingService;

    @Test
    void shouldChunkMovieContentCorrectly() {
        // Given
        Movie movie = Movie.builder()
            .title("Test Movie")
            .reviewHighlights("Long review content...")
            .build();

        when(properties.getProcessing().getChunkSize()).thenReturn(100);

        // When
        List<MovieChunk> chunks = chunkingService.chunkMovie(movie);

        // Then
        assertThat(chunks).isNotEmpty();
        assertThat(chunks.get(0).getContent()).hasSize(100);
    }
}
```

#### Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MovieControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldSearchMoviesByGenre() {
        // When
        ResponseEntity<MovieSearchResponse> response = restTemplate.getForEntity(
            "/api/movies/search?genre=Action",
            MovieSearchResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
    }
}
```

#### Test Containers for External Dependencies
```java
@Testcontainers
class MovieRepositoryIntegrationTest {

    @Container
    static RedisContainer redis = new RedisContainer("redis:7-alpine")
            .withExposedPorts(6379);

    @Container
    static GenericContainer<?> ollama = new GenericContainer<>("ollama/ollama:latest")
            .withExposedPorts(11434);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("ollama.base-url", () ->
            "http://" + ollama.getHost() + ":" + ollama.getFirstMappedPort());
    }
}
```

## 🐛 Debugging Guide

### Common Issues and Solutions

#### 1. Ollama Connection Issues
```java
// Add connection health check
@Component
public class OllamaHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Test Ollama connection
            ResponseEntity<String> response = restTemplate.getForEntity(
                ollamaProperties.getBaseUrl() + "/api/version",
                String.class
            );

            return response.getStatusCode().is2xxSuccessful()
                ? Health.up().withDetail("version", response.getBody()).build()
                : Health.down().withDetail("status", response.getStatusCode()).build();

        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

#### 2. Memory Issues During Processing
```java
// Add memory monitoring
@EventListener
public void handleMovieProcessingEvent(MovieProcessingEvent event) {
    Runtime runtime = Runtime.getRuntime();
    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
    long maxMemory = runtime.maxMemory();

    double memoryUsage = (double) usedMemory / maxMemory;

    if (memoryUsage > 0.8) {
        log.warn("High memory usage detected: {}%", memoryUsage * 100);
        // Trigger garbage collection or reduce batch size
    }
}
```

#### 3. Slow Embedding Generation
```java
// Add timing metrics
@Timed(name = "embedding.generation", description = "Time taken to generate embeddings")
public CompletableFuture<float[]> generateEmbeddingAsync(String text) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            return ollamaLLMService.embed(text);
        } catch (Exception e) {
            log.error("Failed to generate embedding for text: {}", text, e);
            throw new EmbeddingGenerationException("Failed to generate embedding", e);
        }
    }, embeddingExecutor);
}
```

### Debug Configuration
```properties
# application-debug.properties

# Enable debug logging
logging.level.com.techisthoughts.ia.movieclassification=DEBUG
logging.level.org.springframework.ai=DEBUG
logging.level.org.springframework.web=DEBUG

# Enable Spring Boot actuator endpoints
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true

# Enable SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Enable request/response logging
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```

## 🧪 Testing Best Practices

### Test Organization
```
src/test/java/
├── unit/                     # Fast, isolated tests
│   ├── domain/              # Domain logic tests
│   ├── application/         # Service tests
│   └── infrastructure/      # Adapter tests
├── integration/             # Integration tests
│   ├── api/                # API endpoint tests
│   ├── database/           # Database integration
│   └── external/           # External service tests
└── e2e/                    # End-to-end tests
    ├── scenarios/          # User journey tests
    └── performance/        # Performance tests
```

### Test Data Management
```java
// Test data builder pattern
public class MovieTestDataBuilder {

    public static Movie.MovieBuilder aMovie() {
        return Movie.builder()
            .title("Test Movie")
            .genre("Action")
            .userRating(8.5)
            .releaseYear(2024)
            .reviewHighlights("Great movie for testing");
    }

    public static Movie actionMovie() {
        return aMovie()
            .genre("Action")
            .userRating(8.0)
            .build();
    }

    public static Movie highlyRatedMovie() {
        return aMovie()
            .userRating(9.5)
            .percentageSuggestedToFriendsFamily(95.0)
            .build();
    }
}
```

### Performance Testing
```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void shouldProcessLargeMovieListQuickly() {
    // Given
    List<Movie> movies = generateTestMovies(1000);

    // When
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    processMoviesUseCase.processMovies(movies);

    stopWatch.stop();

    // Then
    assertThat(stopWatch.getTotalTimeSeconds()).isLessThan(5.0);
}
```

## 📊 Performance Optimization

### Profiling Setup
```java
// Add method-level timing
@Timed(name = "movie.search", description = "Movie search performance")
public List<Movie> searchMovies(SearchCriteria criteria) {
    return movieRepository.findByCriteria(criteria);
}

// Add custom metrics
@Component
public class MovieMetrics {

    private final Counter searchCounter;
    private final Timer searchTimer;

    public MovieMetrics(MeterRegistry meterRegistry) {
        this.searchCounter = Counter.builder("movie.search.count")
            .description("Number of movie searches")
            .register(meterRegistry);

        this.searchTimer = Timer.builder("movie.search.duration")
            .description("Movie search duration")
            .register(meterRegistry);
    }
}
```

### Async Processing
```java
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean("embeddingExecutor")
    public Executor embeddingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("embedding-");
        executor.initialize();
        return executor;
    }
}
```

### Caching Strategy
```java
@Service
public class CachedMovieService {

    @Cacheable(value = "movies", key = "#title")
    public Optional<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    @Cacheable(value = "search-results", key = "#criteria.hashCode()")
    public List<Movie> searchMovies(SearchCriteria criteria) {
        return movieRepository.findByCriteria(criteria);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public void clearMovieCache() {
        // Cache cleared
    }
}
```

## 🔄 Code Quality

### Static Analysis Tools
```xml
<!-- pom.xml -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
</plugin>

<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

### Code Style Configuration
```xml
<!-- checkstyle.xml -->
<module name="Checker">
    <module name="TreeWalker">
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>
    </module>
</module>
```

### Pre-commit Hooks
```bash
#!/bin/sh
# .git/hooks/pre-commit

# Run tests
mvn test

# Check code style
mvn checkstyle:check

# Run static analysis
mvn spotbugs:check
```

## 🚀 Build and Deployment

### Maven Profiles
```xml
<!-- pom.xml -->
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>

    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
        <build>
            <plugins>
                <plugin>
                    <groupId>com.google.cloud.tools</groupId>
                    <artifactId>jib-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### Docker Build
```dockerfile
# Dockerfile.dev
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/*.jar app.jar

# Development optimizations
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

---

**Next**: [🧪 Testing Guide](./10-testing-guide.md)
