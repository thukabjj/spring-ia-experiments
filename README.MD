# Spring AI with Ollama

This project is an experimentation of Spring AI (Artificial Intelligence) with Ollama, using the [Netflix Life Impact Dataset](https://www.kaggle.com/datasets/towhid121/netflix-life-impact-dataset-nlid/data) from Kaggle.

The application demonstrates how to use Spring Boot, Spring AI, Ollama, and Redis to perform intelligent movie data processing, embedding, and querying. It exposes a REST API for interacting with processed movie data and includes observability tooling for monitoring.

---

## Features

- REST API with Spring Boot to expose endpoints for movie data and chat-based AI.
- Reads and parses movie data from a CSV file (Netflix Life Impact Dataset).
- Uses Spring AI and Ollama for movie classification and chat interaction.
- Embeds movie data as vectors using Ollama's embedding model and stores in Redis (vector store).
- Logging and monitoring of processed movie records and service health.
- Pre-built endpoints for:
  - Fetching all movies: `/movies`
  - Chatting with an AI assistant: `/chat?message=your-query`

---

## Requirements

- Java 21+
- Maven 3.8+
- [Ollama](https://ollama.com/) (for AI model integration)
- Redis (required if using vector store features)
- Docker (for running containers such as Redis, Grafana, Prometheus, etc.)

---

## Setup

1. **Run containers** (Redis, Grafana, Prometheus, etc.)

   ```bash
   docker compose up
   ```

2. **Build the project:**

   ```bash
   mvn clean install
   ```

3. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

---

## Example Usage

- After starting the application, it will automatically load and process the `NLID.csv` dataset.
- Access movie data via:
  ```
  GET http://localhost:8080/movies
  ```
- Chat with the AI assistant via:
  ```
  GET http://localhost:8080/chat?message=Tell me about a movie that changed lives.
  ```

---

## Access the Services

- **Grafana:** [http://localhost:3000](http://localhost:3000) (admin/admin)
- **Prometheus:** [http://localhost:9090](http://localhost:9090)
- **Loki:** [http://localhost:3100](http://localhost:3100)
- **Tempo:** [http://localhost:3200](http://localhost:3200)
- **Redis:** [http://localhost:6379](http://localhost:6379)
- **RedisInsight (bundled):** [http://localhost:8001](http://localhost:8001)

---

## Project Structure

- `src/main/java/com/techisthoughts/ia/movieclassification/`
  - `controller/` - REST endpoints for movies and chat
  - `service/` - Business logic for data loading, embedding, and querying
  - `repository/` - Repository layer using Redis as a vector store
  - `llm/` - Large Language Model configuration

---

## Dataset

- The [Netflix Life Impact Dataset](https://www.kaggle.com/datasets/towhid121/netflix-life-impact-dataset-nlid/data) (`NLID.csv`) is required in the classpath for movie data loading.
- The application reads the CSV file, creates vector embeddings, and saves them in Redis for semantic search and querying.

---

## License

This project is for experimentation and educational purposes. Please refer to the Kaggle dataset license for dataset usage terms.

---