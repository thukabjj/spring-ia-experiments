# Movie Classification AI Demo

This project is a Spring Boot application that uses Spring AI and Ollama to classify and process movie records from a CSV file. It exposes REST endpoints for chat and movie classification.

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

## Endpoints

- `GET /chat`  
  Simple chat endpoint with a system prompt.

- `POST /classify`  
  Reads `NLID.csv` from resources, parses movie records, and classifies them using the AI model.

## Setup

1. Clone the repository.
2. Place your `NLID.csv` file in `src/main/resources/`.
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Configuration

Dependencies and versions are managed in `pom.xml`.  
Spring AI and Ollama integration is handled via the provided starters.

