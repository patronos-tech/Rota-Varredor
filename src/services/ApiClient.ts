import axios, { AxiosInstance, AxiosError, AxiosRequestConfig } from 'axios';
import { logger } from '@utils/logger';

export interface ApiClientConfig {
  baseURL: string;
  timeout?: number;
  retries?: number;
  retryDelay?: number;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  details?: any;
}

export class ApiClient {
  private client: AxiosInstance;
  private retries: number;
  private retryDelay: number;
  private requestQueue: AxiosRequestConfig[] = [];
  private isOnline: boolean = true;

  constructor(config: ApiClientConfig) {
    this.retries = config.retries || 3;
    this.retryDelay = config.retryDelay || 1000;

    this.client = axios.create({
      baseURL: config.baseURL,
      timeout: config.timeout || 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        logger.debug('API Request:', { url: config.url, method: config.method });
        return config;
      },
      (error) => {
        logger.error('Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response) => {
        logger.debug('API Response:', { url: response.config.url, status: response.status });
        return response;
      },
      (error) => this.handleError(error)
    );
  }

  private async handleError(error: AxiosError): Promise<never> {
    if (!this.isOnline && error.code !== 'ECONNABORTED') {
      this.requestQueue.push(error.config as AxiosRequestConfig);
      logger.warn('Request queued (offline)', { url: error.config?.url });
      return Promise.reject(error);
    }

    logger.error('API Error:', {
      url: error.config?.url,
      status: error.response?.status,
      message: error.message,
    });

    return Promise.reject(error);
  }

  async get<T>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<T> {
    return this.request<T>('GET', url, undefined, config);
  }

  async post<T>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> {
    return this.request<T>('POST', url, data, config);
  }

  async put<T>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> {
    return this.request<T>('PUT', url, data, config);
  }

  async patch<T>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> {
    return this.request<T>('PATCH', url, data, config);
  }

  async delete<T>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<T> {
    return this.request<T>('DELETE', url, undefined, config);
  }

  private async request<T>(
    method: string,
    url: string,
    data?: any,
    config?: AxiosRequestConfig,
    attempt: number = 1
  ): Promise<T> {
    try {
      const response = await this.client.request<T>({
        method,
        url,
        data,
        ...config,
      });
      return response.data;
    } catch (error) {
      const axiosError = error as AxiosError;

      // Retry logic
      if (attempt < this.retries && this.shouldRetry(axiosError)) {
        logger.warn(`Retrying request (${attempt}/${this.retries}):`, { url });
        await this.delay(this.retryDelay * attempt);
        return this.request<T>(method, url, data, config, attempt + 1);
      }

      throw error;
    }
  }

  private shouldRetry(error: AxiosError): boolean {
    // Retry on network errors or 5xx errors
    if (!error.response) return true;
    const status = error.response.status;
    return status >= 500 || status === 429;
  }

  private delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  setOnline(online: boolean): void {
    this.isOnline = online;
    if (online && this.requestQueue.length > 0) {
      this.syncQueuedRequests();
    }
  }

  private async syncQueuedRequests(): Promise<void> {
    logger.info('Syncing queued requests...', { count: this.requestQueue.length });
    const queue = [...this.requestQueue];
    this.requestQueue = [];

    for (const config of queue) {
      try {
        await this.client.request(config);
      } catch (error) {
        logger.error('Failed to sync queued request', { url: config.url, error });
        this.requestQueue.push(config);
      }
    }
  }
}
