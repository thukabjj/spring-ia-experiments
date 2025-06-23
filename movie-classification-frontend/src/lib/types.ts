// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

// Movie Types
export interface Movie {
  movieTitle: string;
  genre: string;
  releaseYear: string;
  averageRating: string;
  numberOfReviews: string;
  reviewHighlights: string[];
  minuteOfLifeChangingInsight: string;
  howDiscovered: string;
  meaningfulAdviceTaken: string;
  isSuggestedToFriendsFamily: string;
  percentageSuggestedToFriendsFamily: string;
}

export type MovieDto = Movie;

// Search Types
export interface SearchResult {
  id: string;
  similarity: number;
  metadata: Record<string, unknown>;
}

// RAG Types
export interface RAGQuery {
  query: string;
  limit?: number;
  responseStyle?: 'concise' | 'detailed' | 'casual' | 'analytical';
  enableEnrichment?: boolean;
  enableCuration?: boolean;
}

export interface RAGResponse {
  success: boolean;
  query: string;
  enrichedQuery?: string;
  response: string;
  sources: Array<{
    title?: string;
    similarity?: number;
    metadata?: Record<string, unknown>;
  }>;
  timestamp: string;
  chainOfThought?: string[];
}

// Processing Types
export interface ProcessingRequest {
  strategy?: 'FIXED_SIZE' | 'SEMANTIC' | 'ADAPTIVE';
  questionsPerChunk?: number;
}

export interface ProcessingResult {
  success: boolean;
  chunksCreated: number;
  questionsGenerated: number;
  embeddingsStored: number;
  message: string;
}

// Health & Metrics Types
export interface HealthStatus {
  status: string;
  timestamp: string;
  movieCount: number;
  embeddingCount: number;
  llmAvailable: boolean;
  dataStatus?: {
    moviesLoaded: number;
    dataSource: string;
    lastUpdated: string;
  };
  llmStatus?: {
    available: boolean;
    embeddingModel: string;
    chatModel: string;
    optimizationLevel: string;
  };
  performance?: {
    cacheHitRate: string;
    averageResponseTime: string;
    totalRequests: number;
    optimization: string;
  };
}

export interface MetricsData {
  tokens: {
    input: number;
    output: number;
    total: number;
    estimatedCost: number;
  };
  performance: {
    averageResponseTime: number;
    throughput: number;
    cacheHitRate: number;
  };
  business: {
    moviesProcessed: number;
    chunksCreated: number;
    questionsGenerated: number;
    embeddingsCreated: number;
  };
  errors: {
    total: number;
    circuitBreakerTrips: number;
    retryAttempts: number;
  };
}

// Performance Analytics Types
export interface PerformanceComparison {
  blocking: string;
  optimized: string;
  ultraFast: string;
  lightning: string;
}

export interface StrategyMetrics {
  usage: number;
  averageTime: number;
  successRate: number;
}

// Feature Toggle Types
export interface FeatureToggles {
  strategies: Record<string, {
    enabled: boolean;
    description: string;
  }>;
  features: Record<string, {
    enabled: boolean;
    [key: string]: unknown;
  }>;
  monitoring: Record<string, {
    enabled: boolean;
    [key: string]: unknown;
  }>;
}

// Dashboard Data Types
export interface DashboardData {
  health: HealthStatus;
  metrics: MetricsData;
  performance: PerformanceComparison;
  features: FeatureToggles;
}

// API Endpoints Configuration
export const API_ENDPOINTS = {
  BASE_URL: 'http://localhost:8585/api',
  MOVIES: {
    BASE: '/movies',
    HEALTH: '/movies/health',
    LOAD: '/movies/load',
    SEARCH: '/movies/search',
    PROCESS: '/movies/process',
    STATS: '/movies/stats',
    CLEAR: '/movies/clear',
    BY_TITLE: (title: string) => `/movies/${encodeURIComponent(title)}`,
  },
  RAG: {
    BASE: '/rag',
    ASK: '/rag/ask',
    QUERY: '/rag/query',
    CAPABILITIES: '/rag/capabilities',
  },
  OBSERVABILITY: {
    BASE: '/observability',
    HEALTH: '/observability/health',
    METRICS: '/observability/metrics',
    TOKENS: '/observability/tokens',
    PERFORMANCE: '/observability/performance',
    CACHE: '/observability/cache',
    ERRORS: '/observability/errors',
    DASHBOARD: '/observability/dashboard',
    FEATURES: '/observability/features',
  },
  OPTIMIZED: {
    BASE: '/optimized/movies',
    HEALTH: '/optimized/movies/health',
    PROCESS: '/optimized/movies/process-fast',
    COMPARISON: '/optimized/movies/performance-comparison',
  },
  ULTRA_FAST: {
    BASE: '/ultra-fast/movies',
    HEALTH: '/ultra-fast/movies/health',
    STREAMING: '/ultra-fast/movies/process-streaming',
    LIGHTNING: '/ultra-fast/movies/process-lightning',
    METRICS: '/ultra-fast/movies/performance-metrics',
  },
} as const;
