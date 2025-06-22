# 🔌 API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API does not require authentication. See [Security Guide](./12-security.md) for implementation plans.

## Response Format

All API responses follow this structure:

### Success Response
```json
{
  "success": true,
  "data": {...},
  "metadata": {
    "timestamp": "2024-12-23T10:30:00Z",
    "total": 42,
    "limit": 20,
    "offset": 0
  }
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "MOVIE_NOT_FOUND",
    "message": "Movie with title 'Unknown Movie' not found",
    "details": "Check the movie title spelling or search for similar movies"
  }
}
```

## 🎬 Movie Endpoints

### GET /api/movies/search
Search movies with various filters.

**Query Parameters:**
- `query` (string, optional): Search term for movie content
- `genre` (string, optional): Filter by genre
- `minRating` (number, optional): Minimum user rating (0-10)
- `maxRating` (number, optional): Maximum user rating (0-10)
- `limit` (integer, optional, default: 20): Number of results
- `offset` (integer, optional, default: 0): Pagination offset

**Example Request:**
```bash
curl "http://localhost:8080/api/movies/search?genre=Action&minRating=7.5&limit=10"
```

**Example Response:**
```json
{
  "success": true,
  "data": [
    {
      "title": "The Matrix",
      "genre": "Action",
      "runtime": 136,
      "userRating": 8.7,
      "releaseYear": 1999,
      "reviewHighlights": "Groundbreaking visual effects...",
      "emotionalImpact": 9.2,
      "percentageSuggestedToFriendsFamily": 85.5
    }
  ],
  "metadata": {
    "total": 15,
    "limit": 10,
    "offset": 0
  }
}
```

### GET /api/movies/{title}
Get a specific movie by title.

**Path Parameters:**
- `title` (string, required): Movie title (URL encoded)

**Example Request:**
```bash
curl "http://localhost:8080/api/movies/The%20Dark%20Knight"
```

**Example Response:**
```json
{
  "success": true,
  "data": {
    "title": "The Dark Knight",
    "genre": "Action",
    "runtime": 152,
    "userRating": 9.0,
    "releaseYear": 2008,
    "reviewHighlights": "Heath Ledger's iconic Joker performance...",
    "emotionalImpact": 8.8,
    "percentageSuggestedToFriendsFamily": 92.3,
    "totalViews": 1500000,
    "averageViewingTime": 147
  }
}
```

### POST /api/movies/search/embedding
Semantic search using vector embeddings.

**Request Body:**
```json
{
  "query": "space adventure with heroes fighting evil empire",
  "limit": 5,
  "threshold": 0.7
}
```

**Parameters:**
- `query` (string, required): Natural language search query
- `limit` (integer, optional, default: 10): Number of results
- `threshold` (number, optional, default: 0.0): Similarity threshold (0-1)

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/movies/search/embedding \
  -H "Content-Type: application/json" \
  -d '{
    "query": "romantic comedy with happy ending",
    "limit": 5,
    "threshold": 0.6
  }'
```

**Example Response:**
```json
{
  "success": true,
  "data": [
    {
      "movie": {
        "title": "When Harry Met Sally",
        "genre": "Romance",
        "userRating": 7.6
      },
      "similarity": 0.89,
      "relevanceScore": 0.92
    }
  ],
  "metadata": {
    "searchType": "embedding",
    "queryProcessingTime": "245ms"
  }
}
```

## 🤖 RAG (Retrieval-Augmented Generation) Endpoints

### POST /api/rag/ask
Ask natural language questions about movies.

**Request Body:**
```json
{
  "question": "What are the best family-friendly movies with high emotional impact?",
  "maxResults": 10,
  "includeContext": true
}
```

**Parameters:**
- `question` (string, required): Natural language question
- `maxResults` (integer, optional, default: 5): Max movies to consider
- `includeContext` (boolean, optional, default: false): Include retrieved context

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Recommend sci-fi movies with great visual effects",
    "maxResults": 3,
    "includeContext": true
  }'
```

**Example Response:**
```json
{
  "success": true,
  "data": {
    "answer": "Based on the dataset, I recommend these sci-fi movies with exceptional visual effects:\n\n1. **Avatar** - Revolutionary 3D technology and stunning Pandora world-building\n2. **Blade Runner 2049** - Masterful cinematography and futuristic visuals\n3. **Interstellar** - Scientifically accurate space visuals and practical effects\n\nAll three have high user ratings and are frequently recommended to friends and family.",
    "sources": [
      {
        "title": "Avatar",
        "relevance": 0.94,
        "snippet": "Groundbreaking 3D technology..."
      }
    ],
    "processingTime": "1.2s"
  },
  "metadata": {
    "questionType": "recommendation",
    "contextMovies": 3,
    "modelUsed": "llama2"
  }
}
```

### POST /api/rag/chat
Multi-turn conversation about movies.

**Request Body:**
```json
{
  "message": "Tell me about action movies",
  "conversationId": "optional-session-id",
  "context": {
    "preserveHistory": true,
    "maxTurns": 10
  }
}
```

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What makes a good thriller movie?",
    "conversationId": "user-123-session-1"
  }'
```

## 📊 System Endpoints

### GET /api/health
System health check.

**Example Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-12-23T10:30:00Z",
  "components": {
    "database": "UP",
    "ollama": "UP",
    "redis": "UP"
  },
  "details": {
    "moviesLoaded": 8807,
    "embeddingsGenerated": 8807,
    "uptime": "2h 15m 30s"
  }
}
```

### GET /api/metrics
Application metrics and statistics.

**Example Response:**
```json
{
  "success": true,
  "data": {
    "movies": {
      "total": 8807,
      "byGenre": {
        "Action": 1250,
        "Comedy": 980,
        "Drama": 1500
      }
    },
    "performance": {
      "averageSearchTime": "150ms",
      "averageEmbeddingTime": "45ms",
      "cacheHitRate": 0.75
    },
    "usage": {
      "totalSearches": 15420,
      "totalQuestions": 3280,
      "dailyActiveUsers": 125
    }
  }
}
```

### GET /api/system/status
Detailed system status information.

**Example Response:**
```json
{
  "success": true,
  "data": {
    "application": {
      "version": "1.0.0",
      "buildTime": "2024-12-20T15:30:00Z",
      "environment": "development"
    },
    "dependencies": {
      "ollama": {
        "status": "connected",
        "models": ["llama2", "nomic-embed-text"],
        "version": "0.1.15"
      },
      "redis": {
        "status": "connected",
        "keys": 8807,
        "memory": "45MB"
      }
    },
    "performance": {
      "jvmMemory": {
        "used": "512MB",
        "max": "2GB"
      },
      "threads": {
        "active": 15,
        "total": 25
      }
    }
  }
}
```

## 🔍 Error Handling

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `MOVIE_NOT_FOUND` | 404 | Specified movie not found |
| `INVALID_QUERY` | 400 | Search query is invalid or empty |
| `LIMIT_EXCEEDED` | 400 | Request limit exceeds maximum allowed |
| `OLLAMA_UNAVAILABLE` | 503 | AI service is not available |
| `PROCESSING_ERROR` | 500 | Internal processing error |

### Error Response Examples

**Movie Not Found (404):**
```json
{
  "success": false,
  "error": {
    "code": "MOVIE_NOT_FOUND",
    "message": "Movie 'Unknown Title' not found",
    "suggestions": ["The Matrix", "The Dark Knight", "Inception"]
  }
}
```

**Invalid Query (400):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_QUERY",
    "message": "Query parameter cannot be empty",
    "field": "query"
  }
}
```

## 📝 Request/Response Examples

### Complex Search Query
```bash
# Multi-parameter search
curl -G "http://localhost:8080/api/movies/search" \
  -d "query=superhero" \
  -d "genre=Action" \
  -d "minRating=8.0" \
  -d "limit=5" \
  -d "offset=0"
```

### Batch Operations
```bash
# Multiple movie lookup (not yet implemented)
curl -X POST http://localhost:8080/api/movies/batch \
  -H "Content-Type: application/json" \
  -d '{
    "titles": ["The Matrix", "Inception", "The Dark Knight"],
    "includeDetails": true
  }'
```

## 🚀 Rate Limiting

Currently not implemented. Planned limits:
- **Search endpoints**: 100 requests/minute
- **RAG endpoints**: 20 requests/minute
- **Health endpoints**: 1000 requests/minute

## 📋 API Changelog

### v1.0.0 (Current)
- Initial API release
- Basic movie search and retrieval
- RAG question answering
- Health and metrics endpoints

### Planned Features
- Authentication and authorization
- Rate limiting
- Batch operations
- Advanced filtering options
- Real-time updates

---

**Testing**: Use the provided [test scripts](../test-rag-capabilities.sh) for comprehensive API testing.
**Next**: [🗄️ Database Schema](./07-database-schema.md)
