# 🎬 Movie Classification Frontend

A modern, responsive Next.js frontend for the Movie Classification System with AI-powered movie analysis, RAG capabilities, and real-time performance monitoring.

## 🚀 Features

- **🎨 Modern UI**: Built with Next.js 14, TypeScript, and Tailwind CSS
- **📊 Real-time Dashboard**: Live system health and performance metrics
- **🤖 RAG Chat Interface**: Natural language movie queries
- **🔍 Advanced Search**: Semantic movie search with vector embeddings
- **📈 Performance Analytics**: Multiple processing strategies comparison
- **🎯 Type Safety**: Full TypeScript support with comprehensive type definitions
- **⚡ Optimized Performance**: React Query for efficient data fetching and caching
- **🎭 Beautiful Components**: Shadcn/ui components with modern design

## 🛠️ Tech Stack

- **Framework**: Next.js 14 with App Router
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Shadcn/ui
- **Data Fetching**: TanStack React Query (React Query v5)
- **HTTP Client**: Axios
- **State Management**: Zustand
- **Form Handling**: React Hook Form with Zod validation
- **Icons**: Lucide React
- **Charts**: Recharts
- **Notifications**: Sonner

## 📁 Project Structure

```
movie-classification-frontend/
├── src/
│   ├── app/                    # Next.js 14 App Router
│   │   ├── layout.tsx         # Root layout with providers
│   │   └── page.tsx           # Main dashboard page
│   ├── components/            # Reusable components
│   │   └── ui/               # Shadcn/ui components
│   └── lib/                  # Utilities and configurations
│       ├── api.ts            # API client with Axios
│       ├── types.ts          # TypeScript type definitions
│       ├── hooks/            # Custom React hooks
│       │   └── useApi.ts     # React Query hooks
│       └── utils.ts          # Utility functions
├── public/                   # Static assets
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── next.config.ts
```

## 🚀 Quick Start

### Prerequisites

- Node.js 18+
- npm or yarn
- Backend Spring Boot application running on port 8585

### Installation

1. **Navigate to the frontend directory**:
   ```bash
   cd movie-classification-frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. **Open your browser**:
   Navigate to [http://localhost:3000](http://localhost:3000)

## 📡 API Integration

The frontend connects to the Spring Boot backend running on `http://localhost:8585/api`.

### Available Endpoints

- **Movies API**: `/api/movies/*` - CRUD operations, search, processing
- **RAG API**: `/api/rag/*` - Natural language queries and chat
- **Observability API**: `/api/observability/*` - Health, metrics, performance
- **Optimized APIs**: `/api/optimized/*`, `/api/ultra-fast/*` - Performance strategies

### Connection Status

The dashboard automatically detects backend connectivity and displays:
- ✅ **System Online** - Backend is reachable
- ❌ **System Offline** - Backend is not reachable
- **Latency** - Response time to backend

## 🎯 Key Features

### 📊 Dashboard Overview

- **System Health**: Real-time status of all components
- **Quick Stats**: Movies loaded, embeddings created, cache hit rate
- **Performance Metrics**: LLM status, optimization levels
- **Quick Actions**: Common operations with one click

### 🎬 Movie Management

- **Search & Browse**: Find movies by title, genre, or content
- **Vector Search**: Semantic search using AI embeddings
- **Data Loading**: Load Netflix Life Impact Dataset
- **Processing**: Create chunks and generate questions

### 🤖 RAG Chat Interface

- **Natural Language Queries**: Ask questions about movies
- **Multiple Response Styles**: Concise, detailed, casual, analytical
- **Source Attribution**: See which movies influenced the response
- **Chain of Thought**: Understand the AI reasoning process

### ⚡ Performance Analytics

- **Strategy Comparison**: Blocking vs Optimized vs Ultra-Fast vs Lightning
- **Real-time Metrics**: Response times, throughput, cache efficiency
- **Token Tracking**: Monitor AI model usage and costs
- **Error Analytics**: System reliability monitoring

## 🔧 Development

### Available Scripts

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Start production server
npm start

# Run linting
npm run lint

# Type checking
npm run type-check
```

### Code Quality

- **TypeScript**: Full type safety with strict mode
- **ESLint**: Code linting with Next.js recommended rules
- **Prettier**: Code formatting (configured in IDE)
- **Tailwind CSS**: Utility-first styling with design system

### Adding New Features

1. **Create API types** in `src/lib/types.ts`
2. **Add API methods** in `src/lib/api.ts`
3. **Create React Query hooks** in `src/lib/hooks/useApi.ts`
4. **Build UI components** using Shadcn/ui
5. **Add pages/routes** in the `src/app` directory

## 🎨 UI Components

### Shadcn/ui Components Used

- **Card**: Information containers
- **Button**: Interactive elements
- **Badge**: Status indicators
- **Tabs**: Navigation between sections
- **Alert**: Error and info messages
- **Dialog**: Modal interactions
- **Table**: Data display
- **Input**: Form inputs

### Adding New Components

```bash
# Add a new Shadcn/ui component
npx shadcn@latest add [component-name]
```

## 📱 Responsive Design

The application is fully responsive and optimized for:
- **Desktop**: Full dashboard experience
- **Tablet**: Adapted layout with collapsible sections
- **Mobile**: Touch-friendly interface with drawer navigation

## 🔄 State Management

### React Query (TanStack Query)

- **Caching**: Intelligent data caching with stale-while-revalidate
- **Background Updates**: Automatic data refresh
- **Error Handling**: Retry logic and error boundaries
- **Loading States**: Built-in loading and error states

### Query Configuration

```typescript
// Example query with caching
const { data, isLoading, error } = useSystemHealth();

// Auto-refresh every 30 seconds
refetchInterval: 30 * 1000
```

## 🚨 Error Handling

- **API Errors**: Graceful handling with user-friendly messages
- **Network Issues**: Offline detection and retry mechanisms
- **Loading States**: Skeleton loaders and spinners
- **Error Boundaries**: Prevent app crashes

## 🎯 Performance Optimizations

- **Code Splitting**: Automatic route-based splitting
- **Image Optimization**: Next.js built-in image optimization
- **Caching**: React Query aggressive caching strategy
- **Bundle Analysis**: Webpack bundle analyzer integration

## 🔧 Configuration

### Environment Variables

Create a `.env.local` file:

```env
# Backend API URL
NEXT_PUBLIC_API_BASE_URL=http://localhost:8585/api

# Enable React Query Devtools in production
NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS=false
```

### Tailwind Configuration

Custom theme and design tokens are configured in `tailwind.config.js`.

## 🚀 Deployment

### Vercel (Recommended)

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel
```

### Docker

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

### Static Export

```bash
# Build static export
npm run build && npm run export
```

## 🧪 Testing

### Setup Testing (Future)

```bash
# Install testing dependencies
npm install --save-dev @testing-library/react @testing-library/jest-dom jest-environment-jsdom
```

## 📈 Monitoring

- **React Query Devtools**: Development-time query inspection
- **Performance Monitoring**: Built-in Next.js analytics
- **Error Tracking**: Integration ready for Sentry
- **User Analytics**: Ready for Google Analytics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Troubleshooting

### Common Issues

1. **Backend Connection Failed**
   - Ensure Spring Boot app is running on port 8585
   - Check CORS configuration in backend

2. **Build Errors**
   - Run `npm install` to ensure all dependencies are installed
   - Check TypeScript errors with `npm run type-check`

3. **Styling Issues**
   - Ensure Tailwind CSS is properly configured
   - Check for conflicting CSS classes

### Getting Help

- Check the [Issues](https://github.com/thukabjj/spring-ia-experiments/issues) page
- Review the backend API documentation
- Ensure all environment variables are set correctly

---

**Happy coding! 🚀**
