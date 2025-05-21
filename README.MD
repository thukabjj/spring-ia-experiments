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

