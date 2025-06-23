import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
    API_ENDPOINTS,
    ApiResponse,
    FeatureToggles,
    HealthStatus,
    MetricsData,
    MovieDto,
    PerformanceComparison,
    ProcessingRequest,
    ProcessingResult,
    RAGQuery,
    RAGResponse,
    SearchResult
} from './types';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_ENDPOINTS.BASE_URL,
      timeout: 30000, // 30 seconds timeout
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`);
        return config;
      },
      (error) => {
        console.error('❌ Request Error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response) => {
        console.log(`✅ API Response: ${response.status} ${response.config.url}`);
        return response;
      },
      (error) => {
        console.error('❌ Response Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

  // Generic request method with proper error handling
  private async request<T>(
    method: 'GET' | 'POST' | 'PUT' | 'DELETE',
    url: string,
    data?: unknown,
    params?: Record<string, unknown>
  ): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.client.request({
        method,
        url,
        data,
        params,
      });
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          error.response?.data?.message ||
          error.response?.data?.error ||
          error.message ||
          'An unexpected error occurred'
        );
      }
      throw error;
    }
  }

  // Movie API Methods
  async getMovies(): Promise<MovieDto[]> {
    return this.request<MovieDto[]>('GET', API_ENDPOINTS.MOVIES.BASE);
  }

  async getMovieByTitle(title: string): Promise<MovieDto> {
    return this.request<MovieDto>('GET', API_ENDPOINTS.MOVIES.BY_TITLE(title));
  }

  async searchMovies(query: string, limit = 10): Promise<SearchResult[]> {
    return this.request<SearchResult[]>('GET', API_ENDPOINTS.MOVIES.SEARCH, undefined, {
      query,
      limit,
    });
  }

  async loadMovies(): Promise<ApiResponse<{ moviesLoaded: number; message: string }>> {
    return this.request<ApiResponse<{ moviesLoaded: number; message: string }>>(
      'POST',
      API_ENDPOINTS.MOVIES.LOAD
    );
  }

  async processMovies(request: ProcessingRequest): Promise<ProcessingResult> {
    return this.request<ProcessingResult>('POST', API_ENDPOINTS.MOVIES.PROCESS, undefined, {
      strategy: request.strategy,
      questionsPerChunk: request.questionsPerChunk,
    });
  }

  async getMovieStats(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.MOVIES.STATS);
  }

  async clearAllData(): Promise<ApiResponse<{ message: string }>> {
    return this.request<ApiResponse<{ message: string }>>('DELETE', API_ENDPOINTS.MOVIES.CLEAR);
  }

  async getMovieHealth(): Promise<HealthStatus> {
    return this.request<HealthStatus>('GET', API_ENDPOINTS.MOVIES.HEALTH);
  }

  // RAG API Methods
  async askRAG(
    query: string,
    limit = 5,
    style: 'concise' | 'detailed' | 'casual' | 'analytical' = 'detailed'
  ): Promise<RAGResponse> {
    return this.request<RAGResponse>('GET', API_ENDPOINTS.RAG.ASK, undefined, {
      q: query,
      limit,
      style,
    });
  }

  async queryRAG(request: RAGQuery): Promise<RAGResponse> {
    return this.request<RAGResponse>('POST', API_ENDPOINTS.RAG.QUERY, request);
  }

  async getRAGCapabilities(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.RAG.CAPABILITIES);
  }

  // Observability API Methods
  async getSystemHealth(): Promise<HealthStatus> {
    return this.request<HealthStatus>('GET', API_ENDPOINTS.OBSERVABILITY.HEALTH);
  }

  async getMetrics(): Promise<MetricsData> {
    return this.request<MetricsData>('GET', API_ENDPOINTS.OBSERVABILITY.METRICS);
  }

  async getTokenMetrics(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.OBSERVABILITY.TOKENS);
  }

  async getPerformanceAnalytics(): Promise<PerformanceComparison> {
    return this.request<PerformanceComparison>('GET', API_ENDPOINTS.OBSERVABILITY.PERFORMANCE);
  }

  async getCacheAnalytics(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.OBSERVABILITY.CACHE);
  }

  async getErrorAnalytics(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.OBSERVABILITY.ERRORS);
  }

  async getDashboardData(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.OBSERVABILITY.DASHBOARD);
  }

  async getFeatureToggles(): Promise<FeatureToggles> {
    return this.request<FeatureToggles>('GET', API_ENDPOINTS.OBSERVABILITY.FEATURES);
  }

  // Optimized API Methods
  async getOptimizedHealth(): Promise<HealthStatus> {
    return this.request<HealthStatus>('GET', API_ENDPOINTS.OPTIMIZED.HEALTH);
  }

  async processMoviesFast(
    strategy = 'FIXED_SIZE',
    questionsPerChunk = 2
  ): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>(
      'POST',
      API_ENDPOINTS.OPTIMIZED.PROCESS,
      undefined,
      { strategy, questionsPerChunk }
    );
  }

  async getPerformanceComparison(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.OPTIMIZED.COMPARISON);
  }

  // Ultra-Fast API Methods
  async getUltraFastHealth(): Promise<HealthStatus> {
    return this.request<HealthStatus>('GET', API_ENDPOINTS.ULTRA_FAST.HEALTH);
  }

  async processMoviesLightning(
    strategy = 'FIXED_SIZE',
    questionsPerChunk = 2,
    maxChunks = 20
  ): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>(
      'POST',
      API_ENDPOINTS.ULTRA_FAST.LIGHTNING,
      undefined,
      { strategy, questionsPerChunk, maxChunks }
    );
  }

  async getUltraFastMetrics(): Promise<Record<string, unknown>> {
    return this.request<Record<string, unknown>>('GET', API_ENDPOINTS.ULTRA_FAST.METRICS);
  }

  // Utility Methods
  async ping(): Promise<boolean> {
    try {
      await this.getSystemHealth();
      return true;
    } catch {
      return false;
    }
  }

  // Test connection method
  async testConnection(): Promise<{
    connected: boolean;
    latency?: number;
    error?: string
  }> {
    const start = Date.now();
    try {
      await this.getSystemHealth();
      const latency = Date.now() - start;
      return { connected: true, latency };
    } catch (error) {
      return {
        connected: false,
        error: error instanceof Error ? error.message : 'Unknown error'
      };
    }
  }
}

// Create and export a singleton instance
export const apiClient = new ApiClient();

// Export the class for testing purposes
export { ApiClient };
