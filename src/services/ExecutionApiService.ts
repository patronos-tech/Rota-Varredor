import type { ApiClient } from './ApiClient';
import type {
  StartExecutionRequest,
  StartExecutionResponse,
  CompleteSegmentRequest,
  CompleteSegmentResponse,
  TrackLocationRequest,
  TrackLocationResponse,
} from '@types/api';
import type { Execution, SegmentExecution } from '@types/domain';

export class ExecutionApiService {
  constructor(private apiClient: ApiClient) {}

  async startExecution(request: StartExecutionRequest): Promise<Execution> {
    const response = await this.apiClient.post<StartExecutionResponse>(
      '/api/executions/start',
      request
    );
    if (!response.success || !response.data) {
      throw new Error(response.error?.message || 'Failed to start execution');
    }
    return response.data;
  }

  async completeSegment(request: CompleteSegmentRequest): Promise<Execution> {
    const response = await this.apiClient.post<CompleteSegmentResponse>(
      '/api/executions/segments/complete',
      request
    );
    if (!response.success || !response.data) {
      throw new Error(response.error?.message || 'Failed to complete segment');
    }
    return response.data;
  }

  async trackLocation(request: TrackLocationRequest): Promise<SegmentExecution> {
    const response = await this.apiClient.post<TrackLocationResponse>(
      '/api/executions/track-location',
      request
    );
    if (!response.success) {
      throw new Error(response.error?.message || 'Failed to track location');
    }
    return response.data as any;
  }

  async pauseExecution(executionId: string, reason?: string): Promise<void> {
    await this.apiClient.post(`/api/executions/${executionId}/pause`, {
      reason,
    });
  }

  async resumeExecution(executionId: string): Promise<Execution> {
    const response = await this.apiClient.post<{ data: Execution }>(
      `/api/executions/${executionId}/resume`
    );
    return response.data;
  }

  async completeExecution(executionId: string): Promise<Execution> {
    const response = await this.apiClient.post<{ data: Execution }>(
      `/api/executions/${executionId}/complete`
    );
    return response.data;
  }

  async getExecution(executionId: string): Promise<Execution> {
    const response = await this.apiClient.get<{ data: Execution }>(
      `/api/executions/${executionId}`
    );
    return response.data;
  }
}
