# 🔍 Troubleshooting Guide

## 🚨 Common Issues and Solutions

### 1. Application Startup Issues

#### Issue: Application fails to start with "Port 8080 already in use"
**Symptoms:**
```
***************************
APPLICATION FAILED TO START
***************************

Description:
Web server failed to start. Port 8080 was already in use.
```

**Solutions:**
```bash
# Option 1: Find and kill process using port 8080
lsof -ti:8080 | xargs kill -9

# Option 2: Change application port
echo "server.port=8081" >> src/main/resources/application.properties

# Option 3: Use random port for testing
echo "server.port=0" >> src/main/resources/application-test.properties
```

#### Issue: Cannot connect to Redis
**Symptoms:**
```
org.springframework.data.redis.RedisConnectionFailureException:
Unable to connect to Redis; nested exception is io.lettuce.core.RedisConnectionException
```

**Solutions:**
```bash
# Check if Redis container is running
docker ps | grep redis

# Start Redis if not running
docker compose up -d redis

# Check Redis connectivity
redis-cli -h localhost -p 6379 ping

# Reset Redis container
docker compose restart redis
```

#### Issue: Ollama service unavailable
**Symptoms:**
```
OllamaServiceException: Failed to connect to Ollama service at http://localhost:11434
```

**Solutions:**
```bash
# Check Ollama status
curl http://localhost:11434/api/version

# Start Ollama service
ollama serve

# Install required models
ollama pull llama2
ollama pull nomic-embed-text

# Check available models
ollama list
```

### 2. Memory and Performance Issues

#### Issue: OutOfMemoryError during movie processing
**Symptoms:**
```
java.lang.OutOfMemoryError: Java heap space
    at com.techisthoughts.ia.movieclassification.application.service.ChunkingService.chunkMovie
```

**Solutions:**
```bash
# Increase JVM heap size
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn spring-boot:run

# Or configure in application.properties
echo "app.processing.batch-size=25" >> application.properties
echo "app.processing.parallel-processing=false" >> application.properties
```

**Configuration Tuning:**
```properties
# Reduce memory consumption
app.processing.batch-size=25
app.processing.enable-parallel=false
app.search.max-results=50

# JVM tuning
-Xmx4g
-Xms2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
```

#### Issue: Slow embedding generation
**Symptoms:**
- Long response times (>30 seconds)
- High CPU usage
- Application appears frozen

**Solutions:**
```properties
# Optimize embedding settings
ollama.model.embedding=nomic-embed-text  # Faster than other models
app.processing.batch-size=10
app.processing.embedding-timeout=30s

# Enable async processing
app.processing.async-enabled=true
app.processing.thread-pool-size=4
```

### 3. Data Processing Issues

#### Issue: CSV file not found or corrupted
**Symptoms:**
```
FileNotFoundException: NLID.csv (No such file or directory)
```

**Solutions:**
```bash
# Check file exists
ls -la src/main/resources/NLID.csv

# Verify file permissions
chmod 644 src/main/resources/NLID.csv

# Download fresh dataset if corrupted
curl -o src/main/resources/NLID.csv "https://kaggle.com/datasets/towhid121/netflix-life-impact-dataset-nlid"
```

#### Issue: Movie processing fails with encoding errors
**Symptoms:**
```
java.nio.charset.MalformedInputException: Input length = 1
```

**Solutions:**
```properties
# Force UTF-8 encoding
app.csv.encoding=UTF-8
app.csv.ignore-malformed=true
app.csv.skip-invalid-lines=true
```

### 4. API and Search Issues

#### Issue: Search returns no results
**Symptoms:**
- API returns empty results
- Search functionality not working

**Diagnostic Steps:**
```bash
# Check if movies are loaded
curl http://localhost:8080/api/health

# Test basic search
curl "http://localhost:8080/api/movies/search?limit=5"

# Check Redis for data
redis-cli -h localhost -p 6379 keys "*movie*"
```

**Solutions:**
```bash
# Force reload movies
curl -X POST http://localhost:8080/api/system/reload-movies

# Check embeddings are generated
curl http://localhost:8080/api/metrics | grep embedding
```

#### Issue: RAG responses are irrelevant or empty
**Symptoms:**
- Empty responses from `/api/rag/ask`
- Poor quality answers

**Solutions:**
```properties
# Adjust similarity threshold
app.rag.similarity-threshold=0.6
app.rag.max-context-movies=10
app.rag.include-metadata=true

# Switch to better model
ollama.model.chat=llama2:13b
ollama.model.embedding=all-minilm
```

### 5. Docker and Infrastructure Issues

#### Issue: Docker Compose services fail to start
**Symptoms:**
```
ERROR: for grafana  Cannot start service grafana: driver failed programming external connectivity
```

**Solutions:**
```bash
# Stop all containers and restart
docker compose down
docker system prune -f
docker compose up -d

# Check port conflicts
netstat -tulpn | grep :3000

# Use different ports if needed
echo "GRAFANA_PORT=3001" > .env
docker compose up -d
```

#### Issue: Grafana dashboards not loading
**Symptoms:**
- Grafana UI loads but no data
- Dashboard panels show "No data"

**Solutions:**
```bash
# Check Prometheus connectivity
curl http://localhost:9090/api/v1/query?query=up

# Verify Grafana datasource configuration
docker compose logs grafana | grep -i error

# Reset Grafana data
docker compose down
docker volume rm movie-classification_grafana-data
docker compose up -d
```

### 6. AI Model and Embedding Issues

#### Issue: Embedding generation extremely slow
**Symptoms:**
- Processing takes hours for small datasets
- High CPU usage from Ollama

**Solutions:**
```bash
# Use smaller, faster models
ollama pull nomic-embed-text:latest
ollama pull llama2:7b

# Configure in application
echo "ollama.model.embedding=nomic-embed-text" >> application.properties
echo "ollama.model.chat=llama2:7b" >> application.properties
```

#### Issue: Inconsistent search results
**Symptoms:**
- Same query returns different results
- Poor search relevance

**Solutions:**
```properties
# Normalize embeddings
app.embedding.normalize=true
app.search.similarity-threshold=0.75

# Use consistent chunking
app.chunking.strategy=fixed-size
app.chunking.size=200
app.chunking.overlap=50
```

## 🔧 Diagnostic Tools

### Health Check Endpoints
```bash
# Basic health check
curl http://localhost:8080/api/health

# Detailed system status
curl http://localhost:8080/api/system/status

# Metrics and performance
curl http://localhost:8080/api/metrics
```

### Log Analysis
```bash
# Real-time application logs
tail -f logs/application.log

# Search for errors
grep -i error logs/application.log

# Filter by component
grep "ChunkingService" logs/application.log | tail -20

# Check startup logs
head -50 logs/application.log
```

### Database Inspection
```bash
# Connect to Redis
redis-cli -h localhost -p 6379

# Check stored data
redis-cli -h localhost -p 6379 keys "*"
redis-cli -h localhost -p 6379 get "movie:The Matrix"

# Check memory usage
redis-cli -h localhost -p 6379 info memory
```

### Performance Monitoring
```bash
# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Application metrics
curl http://localhost:8080/actuator/metrics/movie.processing.time

# HTTP metrics
curl http://localhost:8080/actuator/metrics/http.server.requests
```

## 🐛 Debug Mode Configuration

### Enable Debug Logging
```properties
# application-debug.properties
logging.level.com.techisthoughts.ia.movieclassification=DEBUG
logging.level.org.springframework.ai=DEBUG
logging.level.org.springframework.data.redis=DEBUG

# HTTP request/response logging
logging.level.org.springframework.web.client.RestTemplate=DEBUG
logging.level.org.apache.http.wire=DEBUG

# SQL logging (if using JPA)
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### JVM Debugging
```bash
# Enable remote debugging
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
mvn spring-boot:run

# Memory analysis
export MAVEN_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof"

# GC logging
export MAVEN_OPTS="-Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
```

## 📊 Performance Tuning

### Ollama Optimization
```bash
# Set Ollama environment variables
export OLLAMA_NUM_PARALLEL=4
export OLLAMA_MAX_LOADED_MODELS=2
export OLLAMA_FLASH_ATTENTION=1

# Use GPU if available
export OLLAMA_GPU_LAYERS=35
```

### Application Tuning
```properties
# Connection pooling
spring.redis.lettuce.pool.max-active=20
spring.redis.lettuce.pool.max-idle=10
spring.redis.lettuce.pool.min-idle=2

# HTTP client tuning
spring.ai.ollama.timeout=60s
spring.ai.ollama.connection-timeout=30s
spring.ai.ollama.read-timeout=60s

# Async processing
app.processing.thread-pool-size=8
app.processing.queue-capacity=100
```

### Memory Optimization
```properties
# Reduce memory footprint
app.processing.batch-size=25
app.chunking.max-chunk-size=500
app.search.max-results=100

# Enable garbage collection
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=32m
```

## 📋 Error Codes and Solutions

| Error Code | Description | Solution |
|------------|-------------|----------|
| `MOVIE_NOT_FOUND` | Movie title not in dataset | Check spelling, use search API |
| `EMBEDDING_FAILED` | AI model unavailable | Restart Ollama, check models |
| `PROCESSING_TIMEOUT` | Operation took too long | Increase timeout, reduce batch size |
| `REDIS_CONNECTION_FAILED` | Cannot connect to Redis | Check Docker container, restart |
| `INVALID_QUERY` | Search query malformed | Validate input parameters |
| `MEMORY_EXCEEDED` | Out of memory error | Increase heap size, reduce batch size |

## 🔄 Recovery Procedures

### Complete System Reset
```bash
# Stop everything
docker compose down
pkill -f "spring-boot:run"
pkill -f "ollama"

# Clean volumes and networks
docker system prune -a -f
docker volume prune -f

# Restart infrastructure
docker compose up -d

# Restart Ollama
ollama serve &

# Restart application
mvn spring-boot:run
```

### Data Recovery
```bash
# Backup current data
redis-cli -h localhost -p 6379 --rdb backup.rdb

# Clear corrupted data
redis-cli -h localhost -p 6379 flushall

# Reload movies
curl -X POST http://localhost:8080/api/system/reload-movies
```

### Configuration Reset
```bash
# Reset to default configuration
git checkout -- src/main/resources/application.properties

# Remove debug configurations
rm -f src/main/resources/application-debug.properties

# Restart with clean config
mvn spring-boot:run
```

## 📞 Getting Help

### Log Analysis Commands
```bash
# Most recent errors
grep -i "error\|exception" logs/application.log | tail -10

# Performance issues
grep -i "timeout\|slow\|memory" logs/application.log

# AI-related issues
grep -i "ollama\|embedding\|rag" logs/application.log
```

### Support Information
When reporting issues, include:

1. **System Information:**
   ```bash
   java -version
   mvn -version
   docker --version
   ollama --version
   ```

2. **Configuration:**
   ```bash
   cat src/main/resources/application.properties
   ```

3. **Error Logs:**
   ```bash
   tail -50 logs/application.log
   ```

4. **System Status:**
   ```bash
   curl http://localhost:8080/api/health
   ```

---

**Next**: [🛠️ Maintenance](./16-maintenance.md)
