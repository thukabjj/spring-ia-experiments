import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../api';
import {
    ProcessingRequest,
    RAGQuery
} from '../types';

// Query Keys
export const QUERY_KEYS = {
  MOVIES: 'movies',
  MOVIE_BY_TITLE: 'movieByTitle',
  MOVIE_SEARCH: 'movieSearch',
  MOVIE_STATS: 'movieStats',
  MOVIE_HEALTH: 'movieHealth',
  SYSTEM_HEALTH: 'systemHealth',
  METRICS: 'metrics',
  TOKEN_METRICS: 'tokenMetrics',
  PERFORMANCE: 'performance',
  CACHE_ANALYTICS: 'cacheAnalytics',
  ERROR_ANALYTICS: 'errorAnalytics',
  DASHBOARD_DATA: 'dashboardData',
  FEATURE_TOGGLES: 'featureToggles',
  RAG_CAPABILITIES: 'ragCapabilities',
  OPTIMIZED_HEALTH: 'optimizedHealth',
  ULTRA_FAST_HEALTH: 'ultraFastHealth',
  ULTRA_FAST_METRICS: 'ultraFastMetrics',
  PERFORMANCE_COMPARISON: 'performanceComparison',
} as const;

// Movie Hooks
export const useMovies = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.MOVIES],
    queryFn: () => apiClient.getMovies(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 2,
  });
};

export const useMovieByTitle = (title: string, enabled = true) => {
  return useQuery({
    queryKey: [QUERY_KEYS.MOVIE_BY_TITLE, title],
    queryFn: () => apiClient.getMovieByTitle(title),
    enabled: enabled && !!title,
    staleTime: 10 * 60 * 1000, // 10 minutes
    retry: 2,
  });
};

export const useMovieSearch = (query: string, limit = 10, enabled = true) => {
  return useQuery({
    queryKey: [QUERY_KEYS.MOVIE_SEARCH, query, limit],
    queryFn: () => apiClient.searchMovies(query, limit),
    enabled: enabled && !!query,
    staleTime: 2 * 60 * 1000, // 2 minutes
    retry: 2,
  });
};

export const useMovieStats = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.MOVIE_STATS],
    queryFn: () => apiClient.getMovieStats(),
    staleTime: 1 * 60 * 1000, // 1 minute
    retry: 2,
  });
};

export const useMovieHealth = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.MOVIE_HEALTH],
    queryFn: () => apiClient.getMovieHealth(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
    refetchInterval: 30 * 1000, // Auto-refresh every 30 seconds
  });
};

// System Health & Metrics Hooks
export const useSystemHealth = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.SYSTEM_HEALTH],
    queryFn: () => apiClient.getSystemHealth(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
    refetchInterval: 30 * 1000, // Auto-refresh every 30 seconds
  });
};

export const useMetrics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.METRICS],
    queryFn: () => apiClient.getMetrics(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
    refetchInterval: 60 * 1000, // Auto-refresh every minute
  });
};

export const useTokenMetrics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TOKEN_METRICS],
    queryFn: () => apiClient.getTokenMetrics(),
    staleTime: 1 * 60 * 1000, // 1 minute
    retry: 2,
  });
};

export const usePerformanceAnalytics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.PERFORMANCE],
    queryFn: () => apiClient.getPerformanceAnalytics(),
    staleTime: 2 * 60 * 1000, // 2 minutes
    retry: 2,
  });
};

export const useCacheAnalytics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.CACHE_ANALYTICS],
    queryFn: () => apiClient.getCacheAnalytics(),
    staleTime: 1 * 60 * 1000, // 1 minute
    retry: 2,
  });
};

export const useErrorAnalytics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.ERROR_ANALYTICS],
    queryFn: () => apiClient.getErrorAnalytics(),
    staleTime: 1 * 60 * 1000, // 1 minute
    retry: 2,
  });
};

export const useDashboardData = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD_DATA],
    queryFn: () => apiClient.getDashboardData(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
    refetchInterval: 60 * 1000, // Auto-refresh every minute
  });
};

export const useFeatureToggles = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.FEATURE_TOGGLES],
    queryFn: () => apiClient.getFeatureToggles(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 2,
  });
};

// RAG Hooks
export const useRAGCapabilities = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.RAG_CAPABILITIES],
    queryFn: () => apiClient.getRAGCapabilities(),
    staleTime: 10 * 60 * 1000, // 10 minutes
    retry: 2,
  });
};

// Optimized Performance Hooks
export const useOptimizedHealth = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.OPTIMIZED_HEALTH],
    queryFn: () => apiClient.getOptimizedHealth(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
  });
};

export const useUltraFastHealth = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.ULTRA_FAST_HEALTH],
    queryFn: () => apiClient.getUltraFastHealth(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 2,
  });
};

export const useUltraFastMetrics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.ULTRA_FAST_METRICS],
    queryFn: () => apiClient.getUltraFastMetrics(),
    staleTime: 1 * 60 * 1000, // 1 minute
    retry: 2,
  });
};

export const usePerformanceComparison = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.PERFORMANCE_COMPARISON],
    queryFn: () => apiClient.getPerformanceComparison(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 2,
  });
};

// Mutation Hooks
export const useLoadMovies = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => apiClient.loadMovies(),
    onSuccess: () => {
      // Invalidate related queries
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.MOVIES] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.MOVIE_STATS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.SYSTEM_HEALTH] });
    },
  });
};

export const useProcessMovies = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ProcessingRequest) => apiClient.processMovies(request),
    onSuccess: () => {
      // Invalidate related queries
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.METRICS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.SYSTEM_HEALTH] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DASHBOARD_DATA] });
    },
  });
};

export const useProcessMoviesFast = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ strategy = 'FIXED_SIZE', questionsPerChunk = 2 }: {
      strategy?: string;
      questionsPerChunk?: number
    }) => apiClient.processMoviesFast(strategy, questionsPerChunk),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.METRICS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.OPTIMIZED_HEALTH] });
    },
  });
};

export const useProcessMoviesLightning = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      strategy = 'FIXED_SIZE',
      questionsPerChunk = 2,
      maxChunks = 20
    }: {
      strategy?: string;
      questionsPerChunk?: number;
      maxChunks?: number
    }) => apiClient.processMoviesLightning(strategy, questionsPerChunk, maxChunks),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.ULTRA_FAST_METRICS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.ULTRA_FAST_HEALTH] });
    },
  });
};

export const useAskRAG = () => {
  return useMutation({
    mutationFn: ({
      query,
      limit = 5,
      style = 'detailed'
    }: {
      query: string;
      limit?: number;
      style?: 'concise' | 'detailed' | 'casual' | 'analytical'
    }) => apiClient.askRAG(query, limit, style),
  });
};

export const useQueryRAG = () => {
  return useMutation({
    mutationFn: (request: RAGQuery) => apiClient.queryRAG(request),
  });
};

export const useClearAllData = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => apiClient.clearAllData(),
    onSuccess: () => {
      // Invalidate all queries since data has been cleared
      queryClient.invalidateQueries();
    },
  });
};

// Connection Test Hook
export const useTestConnection = () => {
  return useQuery({
    queryKey: ['connection-test'],
    queryFn: () => apiClient.testConnection(),
    staleTime: 30 * 1000, // 30 seconds
    retry: 1,
    refetchInterval: 60 * 1000, // Check connection every minute
  });
};
