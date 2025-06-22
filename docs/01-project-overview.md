# 📋 Project Overview

## What is Movie Classification System?

The Movie Classification System is a sophisticated Spring Boot application that implements a **Retrieval-Augmented Generation (RAG)** architecture for intelligent movie analysis and recommendations. It processes the Netflix Life Impact Dataset (NLID) using advanced AI models to provide semantic search, movie recommendations, and insightful analysis.

## 🎯 Core Purpose

This system demonstrates the power of combining traditional data processing with modern AI capabilities:

- **Data Processing**: Ingests and processes Netflix movie data from CSV files
- **AI Integration**: Uses Ollama for embeddings generation and natural language processing
- **Semantic Search**: Provides intelligent movie discovery through vector-based search
- **RAG Capabilities**: Answers questions about movies using retrieved context
- **Observability**: Comprehensive monitoring and metrics collection

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │   Application   │    │  Infrastructure │
│     Layer       │────│     Layer       │────│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                       │                       │
        │                       │                       │
    Controllers            Use Cases              Adapters & Config
    DTOs & REST           Services               Database & External APIs
```

### Clean Architecture Principles

- **Domain-Driven Design**: Clear separation of business logic
- **Hexagonal Architecture**: Ports and adapters pattern
- **SOLID Principles**: Maintainable and extensible code
- **Dependency Inversion**: Interface-based abstractions

## 🎬 Dataset: Netflix Life Impact Dataset (NLID)

The system processes comprehensive movie data including:

- **Basic Information**: Title, genre, runtime, release year
- **User Engagement**: View counts, completion rates, user ratings
- **Content Analysis**: Review highlights, emotional impact metrics
- **Recommendations**: Friend/family suggestion percentages
- **Metadata**: Production details, cast information

## 🤖 AI/ML Capabilities

### Embedding Generation
- **Vector Representations**: Creates semantic embeddings for movies
- **Similarity Search**: Finds related movies based on content
- **Chunking Strategy**: Optimizes text processing for better embeddings

### Question Answering
- **Context Retrieval**: Finds relevant movies based on queries
- **Natural Language**: Supports conversational interactions
- **Semantic Understanding**: Goes beyond keyword matching

### Models Supported
- **Ollama Integration**: Multiple model support (llama2, mistral, etc.)
- **Configurable**: Switch between different AI models
- **Optimized**: Performance-tuned for different use cases

## 🛠️ Technology Stack

### Backend
- **Java 21**: Modern language features and performance
- **Spring Boot 3.x**: Latest framework capabilities
- **Spring AI**: AI integration and abstractions
- **Maven**: Dependency management and build tool

### Data & Storage
- **Redis**: Vector database for embeddings
- **CSV Processing**: Efficient file parsing and processing
- **In-Memory Storage**: Fast development and testing

### AI & ML
- **Ollama**: Local AI model serving
- **Spring AI**: Unified AI framework
- **Vector Embeddings**: Semantic search capabilities

### Observability (LGTM Stack)
- **Loki**: Log aggregation and analysis
- **Grafana**: Visualization and dashboards
- **Tempo**: Distributed tracing
- **Micrometer**: Application metrics

### Infrastructure
- **Docker**: Containerization and deployment
- **Docker Compose**: Local development environment
- **Prometheus**: Metrics collection

## 🚀 Key Features

### For End Users
- **Movie Discovery**: Find movies based on preferences
- **Intelligent Search**: Semantic search beyond keywords
- **Recommendations**: AI-powered movie suggestions
- **Question Answering**: Ask questions about movies naturally

### For Developers
- **Clean Architecture**: Easy to understand and extend
- **Comprehensive Testing**: Automated testing strategy
- **API Documentation**: Well-documented REST endpoints
- **Observability**: Built-in monitoring and logging

### For Operations
- **Scalable Design**: Ready for horizontal scaling
- **Monitoring**: Full observability stack
- **Health Checks**: System health and status endpoints
- **Performance Metrics**: Detailed performance insights

## 🎯 Use Cases

### Primary Use Cases
1. **Movie Discovery**: "Find movies similar to The Matrix"
2. **Content-Based Search**: "Show me sci-fi movies with high emotional impact"
3. **Question Answering**: "What are the best family-friendly comedies?"
4. **Recommendation Engine**: Personalized movie suggestions

### Technical Use Cases
1. **RAG System Demonstration**: Showcase retrieval-augmented generation
2. **AI Integration**: Example of AI/ML in enterprise applications
3. **Clean Architecture**: Reference implementation of hexagonal architecture
4. **Observability**: Complete monitoring and metrics solution

## 📊 Performance Characteristics

- **Embedding Generation**: Configurable batch processing
- **Search Response**: Sub-second query responses
- **Scalability**: Designed for horizontal scaling
- **Memory Efficiency**: Optimized for large datasets

## 🔮 Future Roadmap

### Short Term
- Enhanced security implementation
- Performance optimizations
- Extended API capabilities
- Comprehensive test coverage

### Long Term
- Multi-modal AI integration
- Real-time recommendation updates
- Advanced analytics dashboard
- Production deployment guides

---

**Next**: [🚀 Quick Start Guide](./02-quick-start.md)
