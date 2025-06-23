# 🎬 Movie Classification System

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A sophisticated **Spring Boot RAG (Retrieval-Augmented Generation)** system that leverages AI models to provide intelligent movie analysis, semantic search, and personalized recommendations using the Netflix Life Impact Dataset.

## 🎯 What is This?

The Movie Classification System demonstrates the power of combining traditional data processing with modern AI capabilities:

- **🤖 AI-Powered**: Uses Ollama for embeddings generation and natural language processing
- **🔍 Semantic Search**: Goes beyond keyword matching to understand meaning and context
- **💬 Question Answering**: Ask natural language questions about movies and get intelligent responses
- **🏗️ Clean Architecture**: Domain-driven design with hexagonal architecture principles
- **📊 Observable**: Complete monitoring stack with LGTM (Loki, Grafana, Tempo, Metrics)

## 🚀 Quick Start

### Prerequisites
- **Java 21+** and **Maven 3.8+**
- **Docker & Docker Compose** for infrastructure
- **4GB+ RAM** (recommended for AI models)

### 5-Minute Setup

```bash
# 1. Clone and navigate
git clone git@github.com:thukabjj/spring-ia-experiments.git
cd movie-classification

# 2. Start infrastructure services
docker compose up -d

# 3. Install and setup Ollama
brew install ollama  # macOS
ollama serve
ollama pull llama3.1:8b
ollama pull nomic-embed-text

# 4. Build and run the application
mvn clean install
mvn spring-boot:run

# 5. Verify everything works
curl http://localhost:8585/api/movies/health
```

## 🎪 Try It Out

### Movie Discovery
```bash
# Load movies from CSV first
curl -X POST http://localhost:8585/api/movies/load

# Search for movies with vector similarity
curl "http://localhost:8585/api/movies/search?query=action%20adventure&limit=5"

# Get specific movie details
curl "http://localhost:8585/api/movies/The%20Matrix"

# Get all movies
curl http://localhost:8585/api/movies
```

### AI-Powered RAG Search
```bash
# Simple RAG query
curl "http://localhost:8585/api/rag/ask?q=What%20are%20the%20best%20family%20movies&limit=5"

# Advanced RAG query with full pipeline
curl -X POST http://localhost:8585/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"query": "recommend uplifting movies for difficult times", "limit": 5, "responseStyle": "detailed"}'
```

### System Monitoring
```bash
# Check system health
curl http://localhost:8585/api/observability/health

# Get detailed metrics
curl http://localhost:8585/api/observability/metrics

# View performance analytics
curl http://localhost:8585/api/observability/performance
```

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │   Application   │    │  Infrastructure │
│     Layer       │────│     Layer       │────│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                       │                       │
    Controllers            Use Cases              Adapters & Config
    DTOs & REST           Services               Database & External APIs
```

### Clean Architecture Benefits
- **🎯 Domain-Driven**: Business logic isolated from technical concerns
- **🔄 Testable**: Easy unit testing with clear boundaries
- **🔧 Flexible**: Swap implementations without changing business logic
- **📈 Scalable**: Well-organized code that grows maintainably

## 🛠️ Technology Stack

### Backend & Framework
- **Java 21** - Modern language features and performance
- **Spring Boot 3.x** - Production-ready application framework
- **Spring AI** - AI integration abstractions and tooling
- **Maven** - Dependency management and build automation

### AI & Machine Learning
- **Ollama** - Local AI model serving platform
- **Vector Embeddings** - Semantic search capabilities with nomic-embed-text
- **RAG Architecture** - Retrieval-augmented generation for intelligent responses

### Data & Storage
- **Redis** - Vector database for embeddings and caching
- **CSV Processing** - Netflix Life Impact Dataset ingestion
- **In-Memory Repository** - Fast development and testing

### Observability (LGTM Stack)
- **Loki** - Log aggregation and analysis
- **Grafana** - Visualization dashboards and alerting
- **Tempo** - Distributed tracing
- **Micrometer** - Application metrics and monitoring

### Infrastructure
- **Docker Compose** - Local development environment
- **Prometheus** - Metrics collection and storage

## 📊 Dataset: Netflix Life Impact Dataset (NLID)

Rich movie metadata including:
- **Basic Info**: Title, genre, runtime, release year
- **User Engagement**: View counts, completion rates, ratings
- **Content Analysis**: Review highlights, emotional impact scores
- **Social Metrics**: Friend/family recommendation percentages
- **82+ Movies** with comprehensive metadata for AI analysis

## 🎯 Key Features

### 🎬 For Movie Enthusiasts
- **Intelligent Discovery**: Find movies based on preferences and mood
- **Semantic Search**: Search by plot, themes, or emotional tone
- **Smart Recommendations**: AI-powered suggestions beyond simple ratings
- **Natural Questions**: "What are uplifting movies for a bad day?"

### 👩‍💻 For Developers
- **Reference Architecture**: Clean, well-documented hexagonal architecture
- **AI Integration**: Practical example of AI in enterprise applications
- **Comprehensive Testing**: Unit, integration, and end-to-end test strategies
- **Production Ready**: Observability, health checks, and metrics

### 🚀 For DevOps
- **Container Ready**: Docker and Docker Compose configurations
- **Monitoring**: Complete LGTM observability stack
- **Health Checks**: Comprehensive system health and diagnostics
- **Scalable Design**: Ready for horizontal scaling and cloud deployment

## 📋 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/movies` | GET | Get all movies |
| `/api/movies/{title}` | GET | Get specific movie details |
| `/api/movies/load` | POST | Load movies from CSV file |
| `/api/movies/search` | GET | Vector similarity search |
| `/api/movies/process` | POST | Process movies with chunking |
| `/api/movies/health` | GET | Movie service health check |
| `/api/movies/stats` | GET | Movie statistics |
| `/api/rag/ask` | GET | Simple RAG query |
| `/api/rag/query` | POST | Advanced RAG with full pipeline |
| `/api/rag/capabilities` | GET | RAG system capabilities |
| `/api/observability/health` | GET | System health check |
| `/api/observability/metrics` | GET | Detailed system metrics |
| `/api/observability/performance` | GET | Performance analytics |

## 📊 Monitoring Dashboards

Access your monitoring stack:

| Service | URL | Purpose |
|---------|-----|---------|
| **Application** | http://localhost:8585 | Main API endpoints |
| **Grafana** | http://localhost:3000 | Dashboards & visualization |
| **Prometheus** | http://localhost:9090 | Metrics collection |
| **Redis Insight** | http://localhost:8001 | Database management |

*Default Grafana credentials: admin/admin*

## 🧪 Testing & Quality

```bash
# Run all tests
mvn test

# Integration tests with test scripts
./test-rag-capabilities.sh

# Performance testing
./test-scenarios.sh

# Check test results
ls test-results/
```

## 📚 Documentation

Comprehensive documentation available in `/docs`:

- **[📋 Project Overview](./docs/01-project-overview.md)** - System purpose and capabilities
- **[🚀 Quick Start](./docs/02-quick-start.md)** - Get running in 10 minutes
- **[🔌 API Documentation](./docs/06-api-documentation.md)** - Complete API reference
- **[🏗️ Architecture](./docs/05-architecture.md)** - System design and patterns
- **[💻 Development Guide](./docs/09-development-guide.md)** - For contributors and developers
- **[🔍 Troubleshooting](./docs/15-troubleshooting.md)** - Common issues and solutions
- **[❓ FAQ](./docs/20-faq.md)** - Frequently asked questions

## 🔧 Configuration

Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8585
spring.application.name=movie-classification

# AI Model Configuration
spring.ai.ollama.chat.model=llama3.1:8b
spring.ai.ollama.embedding.model=nomic-embed-text
spring.ai.ollama.base-url=http://localhost:11434

# Processing Settings
app.embedding.batch-size=20
app.search.default-limit=50
app.search.max-limit=200
app.chunking.default-chunk-size=5

# Data Configuration
app.movie.data.file=NLID.csv

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## 🚨 Common Issues & Solutions

### Ollama Connection Issues
```bash
# Check Ollama status
curl http://localhost:11434/api/version

# Restart if needed
ollama serve
```

### Memory Issues
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn spring-boot:run
```

### Port Conflicts
```bash
# Use different port
echo "server.port=8586" >> application.properties
```

### Dataset Loading Issues
```bash
# Verify CSV file exists
ls -la src/main/resources/NLID.csv

# Load movies manually
curl -X POST http://localhost:8585/api/movies/load
```

More solutions in [Troubleshooting Guide](./docs/15-troubleshooting.md).

## 🔮 Roadmap

### Phase 1: Foundation ✅
- Clean architecture implementation
- Basic RAG functionality
- Observability stack
- Core API endpoints

### Phase 2: Enhancement (In Progress)
- 🔐 Security implementation (authentication, authorization)
- ⚡ Performance optimization (caching, async processing)
- 🧪 Comprehensive testing (unit, integration, e2e)
- 📖 API documentation (OpenAPI/Swagger)

### Phase 3: Production Ready (Planned)
- 🚀 Cloud deployment guides
- 🔄 CI/CD pipeline setup
- 📊 Advanced analytics dashboard
- 🌐 Multi-tenant support

## 🤝 Contributing

We welcome contributions! Areas where you can help:

- **🔐 Security**: Implement authentication and authorization
- **⚡ Performance**: Optimize search and processing speeds
- **🧪 Testing**: Add more comprehensive test coverage
- **📖 Documentation**: Improve guides and examples
- **🎨 UI**: Create a web interface for the API
- **🤖 AI**: Integrate additional AI models and capabilities

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring AI Team** - For excellent AI integration framework
- **Ollama Project** - For making local AI accessible
- **Netflix** - For inspiring the movie classification use case
- **Clean Architecture Community** - For architectural guidance

---

## 🆘 Need Help?

- 📖 **Documentation**: Check the comprehensive [docs](./docs) directory
- 🐛 **Issues**: Review [Troubleshooting Guide](./docs/15-troubleshooting.md)
- ❓ **Questions**: See [FAQ](./docs/20-faq.md)
- 💬 **Discussion**: Create an issue for questions or improvements

**Happy Movie Discovering! 🍿**
