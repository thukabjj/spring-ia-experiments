# Spring AI with Ollama

This project is an experimentation of Spring IA with Ollama using the [Netflix Life Impact Dataset](https://www.kaggle.com/datasets/towhid121/netflix-life-impact-dataset-nlid/data) dataset from Kaggle .

## Features

- REST API with Spring Boot
- Reads and parses movie data from a CSV file
- Uses Spring AI and Ollama for classification and chat
- Logging of processed movie records

## Requirements

- Java 21+
- Maven 3.8+
- Ollama (for AI model integration)
- Redis (if using vector store)
- Docker

## Setup

1. Run containers

```bash
 docker compose up
```

2. Build the project:

```bash
    mvn clean install
```

3. Run the application:

```bash
    mvn spring-boot:run
```

## Access the services

Grafana: <http://localhost:3000> (admin/admin)
Prometheus: <http://localhost:9090>
Loki: <http://localhost:3100>
Tempo: <http://localhost:3200>
Redis: <http://localhost:6379>
RedisInsight (bundled): <http://localhost:8001>
