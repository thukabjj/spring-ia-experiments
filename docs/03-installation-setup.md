# ⚙️ Installation & Setup

## 🎯 Prerequisites

### System Requirements

| Component | Minimum | Recommended | Notes |
|-----------|---------|-------------|-------|
| **RAM** | 8GB | 16GB+ | AI models are memory-intensive |
| **Storage** | 10GB free | 20GB+ | For models, data, and logs |
| **CPU** | 4 cores | 8+ cores | Multi-core helps with parallel processing |
| **OS** | macOS 10.15+, Ubuntu 18.04+, Windows 10+ | Latest versions | Windows requires WSL2 |

### Required Software

#### Java Development Kit (JDK) 21+
```bash
# Option 1: SDKMAN (Recommended)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.1-tem
sdk use java 21.0.1-tem

# Option 2: Direct download
# Download from https://openjdk.java.net/

# Verify installation
java -version
# Should show: openjdk version "21.0.1" or similar
```

#### Apache Maven 3.8+
```bash
# With SDKMAN
sdk install maven 3.9.6

# Or download from https://maven.apache.org/download.cgi

# Verify installation
mvn -version
# Should show: Apache Maven 3.9.6 or similar
```

#### Docker & Docker Compose
```bash
# macOS (using Homebrew)
brew install docker docker-compose

# Or download Docker Desktop from https://www.docker.com/products/docker-desktop

# Linux (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install docker.io docker-compose

# Verify installation
docker --version
docker-compose --version
```

#### Git Version Control
```bash
# macOS
brew install git

# Linux
sudo apt-get install git

# Windows
# Download from https://git-scm.com/download/win

# Verify installation
git --version
```

## 🚀 Quick Installation

### Step 1: Clone Repository
```bash
# Clone the project
git clone <repository-url>
cd movie-classification

# Check project structure
ls -la
```

### Step 2: Start Infrastructure Services
```bash
# Start all required services in background
docker-compose up -d

# Verify services are running
docker-compose ps

# Expected output:
# NAME                    COMMAND                  SERVICE   STATUS
# movie-classification-redis-1     "docker-entrypoint.s…"   redis     Up
# movie-classification-grafana-1   "/run.sh"                 grafana   Up
# movie-classification-prometheus-1 "/bin/prometheus --c…"   prometheus Up
# movie-classification-loki-1      "/usr/bin/loki -conf…"    loki      Up
# movie-classification-tempo-1     "/tempo -config.file…"    tempo     Up
```

### Step 3: Install and Configure Ollama

#### macOS Installation
```bash
# Install via Homebrew
brew install ollama

# Or download from https://ollama.ai
```

#### Linux Installation
```bash
# Install script
curl -fsSL https://ollama.ai/install.sh | sh
```

#### Windows Installation
```bash
# Download installer from https://ollama.ai
# Run in WSL2 for best compatibility
```

#### Start Ollama Service
```bash
# Start Ollama service (runs in background)
ollama serve

# In another terminal, pull required models
ollama pull llama2
ollama pull nomic-embed-text

# Verify models are downloaded
ollama list
# Expected output:
# NAME                    ID              SIZE      MODIFIED
# llama2:latest           78e26419b446    3.8 GB    2 hours ago
# nomic-embed-text:latest 0a109f422b47    274 MB    2 hours ago
```

### Step 4: Build and Run Application
```bash
# Build the project (downloads dependencies)
mvn clean install

# This may take several minutes on first run
# Expected output should end with: BUILD SUCCESS

# Run the application
mvn spring-boot:run

# Application will start on port 8080
# Watch for "Started MovieClassificationApplication" message
```

### Step 5: Verify Installation
```bash
# Check application health
curl http://localhost:8080/api/health

# Expected response:
# {"status":"UP","timestamp":"2024-12-23T10:30:00Z"}

# Test movie search
curl "http://localhost:8080/api/movies/search?limit=3"

# Should return movie data
```

## 🔧 Detailed Configuration

### Environment Variables
Create a `.env` file in the project root:

```bash
# .env file
# Java settings
JAVA_OPTS=-Xmx4g -Xms2g

# Application settings
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Ollama settings
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_CHAT_MODEL=llama2
OLLAMA_EMBEDDING_MODEL=nomic-embed-text

# Redis settings
REDIS_HOST=localhost
REDIS_PORT=6379

# Observability settings
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics,info,prometheus
```

### Application Properties
Update `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=${SERVER_PORT:8080}
server.servlet.context-path=/

# Spring Profiles
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# Ollama Configuration
spring.ai.ollama.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
spring.ai.ollama.chat.model=${OLLAMA_CHAT_MODEL:llama2}
spring.ai.ollama.embedding.model=${OLLAMA_EMBEDDING_MODEL:nomic-embed-text}

# Redis Configuration
spring.redis.host=${REDIS_HOST:localhost}
spring.redis.port=${REDIS_PORT:6379}
spring.redis.timeout=2000ms

# Application-specific settings
app.csv.file-path=classpath:NLID.csv
app.processing.batch-size=50
app.search.default-limit=20
app.search.max-limit=100

# Logging Configuration
logging.level.com.techisthoughts.ia.movieclassification=INFO
logging.level.org.springframework.ai=INFO
logging.file.name=logs/application.log

# Actuator Configuration
management.endpoints.web.exposure.include=${MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE:health,metrics,info}
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

## 🐳 Docker Setup (Alternative)

### Option 1: Full Docker Setup
```bash
# Build application image
docker build -t movie-classification:latest .

# Run with Docker Compose
docker-compose -f docker-compose.full.yml up -d

# This includes the application in Docker
```

### Option 2: Infrastructure Only (Recommended for Development)
```bash
# Run only infrastructure services
docker-compose up -d

# Run application locally for easier development
mvn spring-boot:run
```

## 🏗️ Development Environment Setup

### IDE Configuration

#### IntelliJ IDEA
1. **Import Project**: File → Open → Select `pom.xml`
2. **Java SDK**: File → Project Structure → Project → SDK → Java 21
3. **Enable Annotation Processing**: Settings → Build → Compiler → Annotation Processors → Enable
4. **Install Plugins**: Spring Boot, Docker, YAML/Ansible Support

#### Visual Studio Code
1. **Install Extensions**:
   ```bash
   code --install-extension vscjava.vscode-java-pack
   code --install-extension pivotal.vscode-spring-boot
   code --install-extension ms-azuretools.vscode-docker
   ```

2. **Configure Settings** (`.vscode/settings.json`):
   ```json
   {
     "java.home": "/path/to/java-21",
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-21",
         "path": "/path/to/java-21"
       }
     ],
     "spring-boot.ls.java.home": "/path/to/java-21"
   }
   ```

### Useful Aliases
Add to your shell profile (`.bashrc`, `.zshrc`, etc.):

```bash
# Movie Classification aliases
alias mc-start="docker-compose up -d && mvn spring-boot:run"
alias mc-stop="docker-compose down && pkill -f spring-boot:run"
alias mc-logs="tail -f logs/application.log"
alias mc-health="curl http://localhost:8080/api/health"
alias mc-test="curl 'http://localhost:8080/api/movies/search?limit=3'"
```

## 🧪 Verification Steps

### Infrastructure Health Checks
```bash
# Redis
redis-cli -h localhost -p 6379 ping
# Expected: PONG

# Grafana
curl -I http://localhost:3000
# Expected: HTTP/1.1 200 OK

# Prometheus
curl -I http://localhost:9090
# Expected: HTTP/1.1 200 OK

# Ollama
curl http://localhost:11434/api/version
# Expected: {"version":"0.1.15"}
```

### Application Health Checks
```bash
# Basic health
curl http://localhost:8080/api/health

# System metrics
curl http://localhost:8080/api/metrics

# Movie count
curl http://localhost:8080/api/health | jq '.details.moviesLoaded'

# Test search functionality
curl "http://localhost:8080/api/movies/search?genre=Action&limit=3"

# Test RAG functionality
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What are good comedy movies?"}'
```

## 🚨 Troubleshooting Installation

### Common Issues

#### Port Conflicts
```bash
# Check what's using port 8080
lsof -ti:8080

# Kill process if needed
lsof -ti:8080 | xargs kill -9

# Use different port
echo "server.port=8081" >> src/main/resources/application.properties
```

#### Memory Issues
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx4g -Xms2g"

# Or set in your shell profile
echo 'export MAVEN_OPTS="-Xmx4g -Xms2g"' >> ~/.bashrc
```

#### Docker Issues
```bash
# Reset Docker
docker system prune -a -f
docker volume prune -f

# Restart Docker service (Linux)
sudo systemctl restart docker

# Check Docker resources (ensure enough memory allocated)
docker system df
```

#### Ollama Model Issues
```bash
# Check available models
ollama list

# Re-download if needed
ollama pull llama2
ollama pull nomic-embed-text

# Check Ollama logs
ollama logs
```

### Performance Optimization
```bash
# For development speed
echo "app.processing.batch-size=25" >> application-dev.properties
echo "logging.level.org.springframework.ai=WARN" >> application-dev.properties

# For production-like testing
echo "app.processing.batch-size=100" >> application-prod.properties
echo "app.processing.async-enabled=true" >> application-prod.properties
```

## ✅ Installation Checklist

After setup, verify these items:

- [ ] Java 21+ installed and configured
- [ ] Maven 3.8+ working
- [ ] Docker containers running (redis, grafana, prometheus, loki, tempo)
- [ ] Ollama service running
- [ ] Required AI models downloaded (llama2, nomic-embed-text)
- [ ] Application builds successfully (`mvn clean install`)
- [ ] Application starts without errors
- [ ] Health endpoint returns UP status
- [ ] Movies are loaded from CSV
- [ ] Vector embeddings are generated
- [ ] Search API returns results
- [ ] RAG API responds to questions
- [ ] Monitoring dashboards accessible

---

**Time Estimate**: 30-60 minutes for complete setup
**Next**: [🔧 Configuration Guide](./04-configuration.md)
