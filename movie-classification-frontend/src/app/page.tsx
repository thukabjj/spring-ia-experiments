'use client';

import { Alert, AlertDescription } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useMetrics, useSystemHealth, useTestConnection } from '@/lib/hooks/useApi';
import {
    Activity,
    AlertCircle,
    BarChart3,
    Brain,
    CheckCircle,
    Database,
    Loader2,
    MessageSquare,
    Search,
    Settings,
    TrendingUp,
    Zap
} from 'lucide-react';
import { useState } from 'react';

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState('overview');

  const { data: health, isLoading: healthLoading, error: healthError } = useSystemHealth();
  const { data: metrics, isLoading: metricsLoading } = useMetrics();
  const { data: connection } = useTestConnection();

  const isSystemOnline = health?.status === 'UP' && connection?.connected;

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="text-center space-y-4">
        <div className="flex items-center justify-center space-x-2">
          <Brain className="h-8 w-8 text-blue-600" />
          <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
            Movie Classification System
          </h1>
        </div>
        <p className="text-lg text-gray-600 max-w-2xl mx-auto">
          AI-powered movie analysis with RAG capabilities, semantic search, and real-time performance monitoring
        </p>

        {/* Connection Status */}
        <div className="flex items-center justify-center space-x-2">
          {healthLoading ? (
            <Badge variant="secondary" className="flex items-center space-x-1">
              <Loader2 className="h-3 w-3 animate-spin" />
              <span>Connecting...</span>
            </Badge>
          ) : isSystemOnline ? (
            <Badge variant="default" className="bg-green-500 flex items-center space-x-1">
              <CheckCircle className="h-3 w-3" />
              <span>System Online</span>
            </Badge>
          ) : (
            <Badge variant="destructive" className="flex items-center space-x-1">
              <AlertCircle className="h-3 w-3" />
              <span>System Offline</span>
            </Badge>
          )}

          {connection?.latency && (
            <Badge variant="outline">
              Latency: {connection.latency}ms
            </Badge>
          )}
        </div>
      </div>

      {/* Error Alert */}
      {healthError && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            Unable to connect to the backend service. Please ensure the Spring Boot application is running on port 8585.
          </AlertDescription>
        </Alert>
      )}

      {/* Main Dashboard */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="overview" className="flex items-center space-x-2">
            <BarChart3 className="h-4 w-4" />
            <span>Overview</span>
          </TabsTrigger>
          <TabsTrigger value="movies" className="flex items-center space-x-2">
            <Database className="h-4 w-4" />
            <span>Movies</span>
          </TabsTrigger>
          <TabsTrigger value="rag" className="flex items-center space-x-2">
            <MessageSquare className="h-4 w-4" />
            <span>RAG Chat</span>
          </TabsTrigger>
          <TabsTrigger value="performance" className="flex items-center space-x-2">
            <Zap className="h-4 w-4" />
            <span>Performance</span>
          </TabsTrigger>
        </TabsList>

        {/* Overview Tab */}
        <TabsContent value="overview" className="space-y-6">
          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Movies Loaded</CardTitle>
                <Database className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {health?.movieCount ?? 0}
                </div>
                <p className="text-xs text-muted-foreground">
                  Netflix Life Impact Dataset
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Embeddings</CardTitle>
                <Brain className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {health?.embeddingCount ?? 0}
                </div>
                <p className="text-xs text-muted-foreground">
                  Vector representations
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Cache Hit Rate</CardTitle>
                <TrendingUp className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {health?.performance?.cacheHitRate ?? 'N/A'}
                </div>
                <p className="text-xs text-muted-foreground">
                  Performance optimization
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">LLM Status</CardTitle>
                <Activity className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {health?.llmAvailable ? (
                    <Badge variant="default" className="bg-green-500">Online</Badge>
                  ) : (
                    <Badge variant="destructive">Offline</Badge>
                  )}
                </div>
                <p className="text-xs text-muted-foreground">
                  Ollama AI Service
                </p>
              </CardContent>
            </Card>
          </div>

          {/* System Information */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>System Health</CardTitle>
                <CardDescription>Current system status and performance</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {health?.llmStatus && (
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <span className="text-sm font-medium">Embedding Model:</span>
                      <Badge variant="outline">{health.llmStatus.embeddingModel}</Badge>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-sm font-medium">Chat Model:</span>
                      <Badge variant="outline">{health.llmStatus.chatModel}</Badge>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-sm font-medium">Optimization Level:</span>
                      <Badge variant="secondary">{health.llmStatus.optimizationLevel}</Badge>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Quick Actions</CardTitle>
                <CardDescription>Common system operations</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <Button variant="outline" className="w-full">
                    <Search className="h-4 w-4 mr-2" />
                    Search Movies
                  </Button>
                  <Button variant="outline" className="w-full">
                    <MessageSquare className="h-4 w-4 mr-2" />
                    Ask RAG
                  </Button>
                  <Button variant="outline" className="w-full">
                    <Database className="h-4 w-4 mr-2" />
                    Load Data
                  </Button>
                  <Button variant="outline" className="w-full">
                    <Settings className="h-4 w-4 mr-2" />
                    Settings
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* Movies Tab */}
        <TabsContent value="movies">
          <Card>
            <CardHeader>
              <CardTitle>Movie Management</CardTitle>
              <CardDescription>
                Search, browse, and manage your movie dataset
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-center py-8">
                <Database className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">Movie management interface coming soon...</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* RAG Tab */}
        <TabsContent value="rag">
          <Card>
            <CardHeader>
              <CardTitle>RAG Chat Interface</CardTitle>
              <CardDescription>
                Ask natural language questions about movies
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-center py-8">
                <MessageSquare className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">RAG chat interface coming soon...</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Performance Tab */}
        <TabsContent value="performance">
          <Card>
            <CardHeader>
              <CardTitle>Performance Analytics</CardTitle>
              <CardDescription>
                Real-time system performance and optimization metrics
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-center py-8">
                <Zap className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">Performance dashboard coming soon...</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
