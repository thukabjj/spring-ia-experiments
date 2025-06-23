'use client';

import { Toaster } from '@/components/ui/sonner';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { Inter } from 'next/font/google';
import { useState } from 'react';
import './globals.css';

const inter = Inter({ subsets: ['latin'] });

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [queryClient] = useState(() => new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: 60 * 1000, // 1 minute
        retry: 2,
        refetchOnWindowFocus: false,
      },
      mutations: {
        retry: 1,
      },
    },
  }));

  return (
    <html lang="en">
      <head>
        <title>Movie Classification System</title>
        <meta name="description" content="AI-powered movie classification and analysis system" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </head>
      <body className={inter.className}>
        <QueryClientProvider client={queryClient}>
          <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
            <main className="container mx-auto px-4 py-8">
              {children}
            </main>
          </div>
          <Toaster />
          <ReactQueryDevtools initialIsOpen={false} />
        </QueryClientProvider>
      </body>
    </html>
  );
}
