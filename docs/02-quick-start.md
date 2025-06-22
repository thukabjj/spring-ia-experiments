# 🚀 Quick Start Guide

## Prerequisites Checklist

Before starting, ensure you have:

- ✅ **Java 21+** installed and configured
- ✅ **Maven 3.8+** for dependency management
- ✅ **Docker & Docker Compose** for infrastructure
- ✅ **Git** for version control
- ✅ **8GB+ RAM** recommended for AI models

## 🏃‍♂️ 5-Minute Setup

### Step 1: Clone and Navigate
```bash
git clone <repository-url>
cd movie-classification
```

### Step 2: Start Infrastructure
```bash
docker compose up -d
```

This starts:
- 🔴 Redis (Vector Database)
- 📊 Grafana (Dashboards)
- 📈 Prometheus (Metrics)
- 📝 Loki (Logs)
- 🔍 Tempo (Tracing)

### Step 3: Install Ollama and Models
```bash
# Install Ollama (macOS)
brew install ollama

# Or download from https://ollama.ai

# Start Ollama service
ollama serve

# In another terminal, pull required models
ollama pull llama2
ollama pull nomic-embed-text
```

### Step 4: Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Step 5: Verify Installation
```bash
# Check application health
curl http://localhost:8080/api/health

# Expected response:
# {"status":"UP","timestamp":"2024-..."}
```

## 🧪 Quick Test

### Load Sample Data
```bash
# The application automatically loads NLID.csv on startup
# Check logs for confirmation:
tail -f logs/application.log | grep "movies processed"
```

### Test Movie Search
```bash
# Search for action movies
curl "http://localhost:8080/api/movies/search?query=action&limit=5"

# Get specific movie
curl "http://localhost:8080/api/movies/The%20Matrix"

# Test RAG capabilities
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What are the best sci-fi movies?"}'
```

## 📊 Access Monitoring Dashboards

| Service | URL | Credentials |
|---------|-----|-------------|
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Redis Insight** | http://localhost:8001 | - |
| **Application** | http://localhost:8080 | - |

## 🎯 Quick API Examples

### 1. Movie Discovery
```bash
# Find movies by genre
curl "http://localhost:8080/api/movies/search?genre=Comedy&limit=10"
```

### 2. Semantic Search
```bash
# Vector-based similarity search
curl -X POST http://localhost:8080/api/movies/search/embedding \
  -H "Content-Type: application/json" \
  -d '{"query": "space adventure with heroes", "limit": 5}'
```

### 3. RAG Question Answering
```bash
# Ask natural language questions
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "Recommend family-friendly movies with high ratings"}'
```

## 🔧 Configuration Quick Tweaks

### Change AI Model
```properties
# In application.properties
ollama.model.chat=mistral
ollama.model.embedding=nomic-embed-text
```

### Adjust Processing Limits
```properties
# Batch sizes and limits
app.processing.batch-size=50
app.search.default-limit=20
app.search.max-limit=100
```

### Enable Debug Logging
```properties
# For development debugging
logging.level.com.techisthoughts.ia.movieclassification=DEBUG
logging.level.org.springframework.ai=DEBUG
```

## 🚨 Common Quick Fixes

### Issue: Ollama Connection Failed
```bash
# Check if Ollama is running
curl http://localhost:11434/api/version

# If not running:
ollama serve
```

### Issue: Redis Connection Error
```bash
# Check Redis container
docker ps | grep redis

# Restart if needed
docker compose restart redis
```

### Issue: Out of Memory
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn spring-boot:run
```

### Issue: Port Already in Use
```bash
# Change application port
echo "server.port=8081" >> src/main/resources/application.properties
```

## 📋 Verification Checklist

After setup, verify:

- [ ] Application starts without errors
- [ ] Health endpoint responds (200 OK)
- [ ] Movies are loaded from CSV
- [ ] Vector embeddings are generated
- [ ] Ollama models are accessible
- [ ] Redis contains movie data
- [ ] Grafana dashboards load
- [ ] API endpoints respond correctly

## 🎓 Next Steps

### For Development
1. Read [Development Guide](./09-development-guide.md)
2. Explore [API Documentation](./06-api-documentation.md)
3. Check [Testing Guide](./10-testing-guide.md)

### For Production
1. Review [Security Guide](./12-security.md)
2. Study [Deployment Guide](./13-deployment.md)
3. Configure [Performance Optimization](./14-performance.md)

### For Customization
1. Understand [Architecture](./05-architecture.md)
2. Modify [Configuration](./04-configuration.md)
3. Extend [AI Components](./08-ai-ml-components.md)

## 🆘 Need Help?

- 🐛 **Issues**: Check [Troubleshooting Guide](./15-troubleshooting.md)
- ❓ **Questions**: See [FAQ](./20-faq.md)
- 📖 **Deep Dive**: Read [Project Overview](./01-project-overview.md)

---

**Time to complete**: ~10 minutes
**Next**: [⚙️ Installation & Setup](./03-installation-setup.md)
