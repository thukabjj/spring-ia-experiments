# ❓ Frequently Asked Questions (FAQ)

## 🚀 Getting Started

### Q: What is this project about?
**A:** The Movie Classification System is a Spring Boot RAG (Retrieval-Augmented Generation) application that processes Netflix movie data using AI models. It provides intelligent movie search, recommendations, and question-answering capabilities through semantic analysis and vector embeddings.

### Q: What does RAG mean?
**A:** RAG stands for **Retrieval-Augmented Generation**. It's an AI technique that combines information retrieval with text generation. The system first retrieves relevant movie information based on your query, then uses that context to generate intelligent responses.

### Q: Why use Ollama instead of OpenAI or other commercial APIs?
**A:** Ollama allows us to run AI models locally, providing:
- **Privacy**: Data stays on your machine
- **Cost**: No API fees for usage
- **Control**: Full control over model versions and configurations
- **Offline capability**: Works without internet connection
- **Learning**: Better understanding of AI model deployment

### Q: What is the Netflix Life Impact Dataset (NLID)?
**A:** NLID is a dataset from Kaggle containing comprehensive information about Netflix movies including user ratings, emotional impact scores, viewing statistics, and recommendation percentages. It provides rich metadata for testing AI-powered movie analysis.

## 🛠️ Installation & Setup

### Q: What are the minimum system requirements?
**A:**
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 10GB free space for models and data
- **CPU**: Multi-core processor (AI model processing is CPU-intensive)
- **OS**: macOS, Linux, or Windows with WSL2

### Q: Do I need a GPU for this project?
**A:** No, GPU is not required. Ollama works well with CPU-only processing. However, if you have a compatible GPU, it can significantly speed up embedding generation and model inference.

### Q: Why does the initial setup take so long?
**A:** The first-time setup downloads large AI models (several GB) and processes thousands of movie records to generate embeddings. Subsequent startups are much faster as everything is cached.

### Q: Can I use different AI models?
**A:** Yes! The system supports any Ollama-compatible model. You can change models in `application.properties`:
```properties
ollama.model.chat=mistral
ollama.model.embedding=nomic-embed-text
```

### Q: How do I verify everything is working correctly?
**A:** Run these quick checks:
```bash
# Check application health
curl http://localhost:8080/api/health

# Test movie search
curl "http://localhost:8080/api/movies/search?limit=3"

# Test RAG functionality
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What are good action movies?"}'
```

## 🏗️ Architecture & Design

### Q: Why use Clean Architecture?
**A:** Clean Architecture provides:
- **Maintainability**: Clear separation of concerns
- **Testability**: Easy to unit test business logic
- **Flexibility**: Easy to swap implementations (e.g., different databases)
- **Scalability**: Well-organized code that grows cleanly

### Q: What is the difference between Domain, Application, and Infrastructure layers?
**A:**
- **Domain**: Core business logic (Movie entities, business rules)
- **Application**: Use cases and application services (ProcessMoviesUseCase)
- **Infrastructure**: External systems (databases, APIs, file systems)

### Q: How does the vector search work?
**A:**
1. Movie content is converted to numerical vectors (embeddings) using AI models
2. When you search, your query is also converted to a vector
3. The system finds movies with similar vectors using cosine similarity
4. Results are ranked by semantic similarity, not just keyword matching

### Q: Why Redis for vector storage?
**A:** Redis is used for:
- **Performance**: Fast in-memory operations
- **Vector support**: Native vector similarity search capabilities
- **Caching**: Efficient caching of computed embeddings
- **Development**: Easy to set up and manage locally

## 🔍 Usage & Features

### Q: What types of questions can I ask the RAG system?
**A:** The system handles various query types:
- **Recommendations**: "What are good family movies?"
- **Comparisons**: "Compare action movies with comedy movies"
- **Specific queries**: "Movies with high emotional impact"
- **Analysis**: "What makes a movie highly rated?"

### Q: How accurate are the search results?
**A:** Accuracy depends on:
- **Model quality**: Better AI models = better results
- **Data quality**: Rich movie metadata improves relevance
- **Query specificity**: More specific queries get better results
- **Similarity threshold**: Configurable for precision vs. recall

### Q: Can I add my own movie data?
**A:** Currently, the system loads from the NLID CSV file. To add custom data:
1. Update the CSV file with your movies
2. Restart the application to reprocess embeddings
3. Or implement a custom data loader following the existing adapter pattern

### Q: Why are some search results unexpected?
**A:** AI-based search considers semantic meaning, not just keywords. A search for "space adventure" might return movies about exploration or journeys, even without the word "space." This is by design for more intelligent discovery.

## 🐛 Troubleshooting

### Q: The application starts but search returns no results. What's wrong?
**A:** Common causes:
1. **Movies not loaded**: Check health endpoint for movie count
2. **Embeddings not generated**: Wait for processing to complete
3. **Redis connection**: Verify Redis container is running
4. **Model issues**: Ensure Ollama models are downloaded

### Q: Embedding generation is extremely slow. How can I speed it up?
**A:** Try these optimizations:
- Use faster models (e.g., `nomic-embed-text`)
- Reduce batch size in configuration
- Enable GPU acceleration if available
- Increase JVM memory allocation

### Q: I'm getting "Out of Memory" errors. What should I do?
**A:**
1. Increase JVM heap size: `export MAVEN_OPTS="-Xmx4g"`
2. Reduce processing batch size in `application.properties`
3. Close other applications to free RAM
4. Consider using a smaller AI model

### Q: Docker containers won't start. What's the issue?
**A:** Common solutions:
- Check for port conflicts: `netstat -tulpn | grep :8080`
- Clean Docker: `docker system prune -f`
- Restart Docker service
- Check available disk space

## 📊 Performance & Scaling

### Q: How many movies can the system handle?
**A:** The current architecture can handle tens of thousands of movies. Performance depends on:
- Available RAM for embeddings storage
- AI model size and speed
- Hardware specifications

### Q: Can this scale to production usage?
**A:** The clean architecture is designed for scalability. For production:
- Move to persistent vector database (Weaviate, Pinecone)
- Implement proper caching strategies
- Add load balancing and horizontal scaling
- Use production-grade AI model serving

### Q: How can I improve search performance?
**A:** Performance optimizations:
- Enable caching for frequent queries
- Use smaller, faster AI models
- Implement result pagination
- Add database indexing
- Configure connection pooling

## 🔐 Security & Configuration

### Q: Is this system secure for production use?
**A:** **No**, the current version lacks production security features:
- No authentication or authorization
- No rate limiting
- No input validation/sanitization
- Open API endpoints

See the [Security Guide](./12-security.md) for planned improvements.

### Q: How do I change configuration settings?
**A:** Configuration can be modified in:
- `application.properties` for default settings
- Environment variables for deployment-specific values
- `ApplicationProperties` class for type-safe configuration

### Q: Can I use this with different databases?
**A:** Yes! The clean architecture uses ports/adapters pattern. You can implement:
- Different vector databases (Weaviate, Chroma, Pinecone)
- Traditional databases (PostgreSQL, MongoDB)
- Cloud services (AWS, Azure, GCP)

## 🤖 AI & ML

### Q: What AI models are supported?
**A:** Any Ollama-compatible model:
- **Chat models**: llama2, mistral, codellama, vicuna
- **Embedding models**: nomic-embed-text, all-minilm, bge-large

### Q: How do I choose the best model for my use case?
**A:** Consider:
- **Speed**: Smaller models (7B parameters) are faster
- **Quality**: Larger models (13B+) give better results
- **Memory**: Larger models need more RAM
- **Purpose**: Specialized models for specific tasks

### Q: Can I fine-tune models for better movie recommendations?
**A:** Currently not supported, but possible future enhancements:
- Custom model training on movie data
- Fine-tuning for specific domains
- Integration with cloud ML services

## 🚀 Development & Contribution

### Q: How can I contribute to this project?
**A:** Contributions welcome! Areas for improvement:
- Security implementation
- Performance optimizations
- Additional AI model integrations
- UI/frontend development
- Documentation improvements

### Q: Can I use this as a learning project?
**A:** Absolutely! This project demonstrates:
- Clean Architecture principles
- AI/ML integration in Java
- Spring Boot best practices
- Observability and monitoring
- RAG system implementation

### Q: How do I add new API endpoints?
**A:** Follow the existing pattern:
1. Create controller in `presentation/controller/`
2. Implement use case in `application/usecase/`
3. Add domain logic if needed
4. Write tests for all layers

### Q: Can I deploy this to the cloud?
**A:** Yes, with modifications:
- Use cloud vector databases
- Implement proper authentication
- Add load balancing
- Configure for container orchestration (Kubernetes)

## 📚 Learning & Resources

### Q: Where can I learn more about RAG systems?
**A:** Recommended resources:
- [RAG Paper](https://arxiv.org/abs/2005.11401) - Original research
- [LangChain Documentation](https://langchain.com) - RAG frameworks
- [Ollama Documentation](https://ollama.ai) - Local AI models

### Q: What concepts should I understand to work with this project?
**A:** Key concepts:
- **Vector embeddings**: Numerical representation of text
- **Semantic search**: Meaning-based rather than keyword-based
- **Clean Architecture**: Domain-driven design principles
- **Spring Boot**: Java web framework
- **Docker**: Containerization technology

### Q: Are there similar projects I can study?
**A:** Related projects:
- LangChain examples
- Spring AI documentation
- Vector database tutorials
- Open source RAG implementations

---

**Have a question not covered here?**
- Check the [Troubleshooting Guide](./15-troubleshooting.md)
- Review the [Development Guide](./09-development-guide.md)
- Create an issue in the project repository
