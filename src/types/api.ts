// API Response/Request Types

import type { Sector, Street, Segment, Execution, ExecutionMetrics, RouteLearning } from './domain';

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  timestamp: Date;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, any>;
}

// Sector Endpoints
export interface CreateSectorRequest {
  name: string;
  code: string;
  mapImage?: string;
  pickupPoint: { latitude: number; longitude: number };
  dropoffPoint: { latitude: number; longitude: number };
}

export interface CreateSectorResponse extends ApiResponse<Sector> {}

export interface GetSectorsResponse extends ApiResponse<Sector[]> {}

// Street Endpoints
export interface CreateStreetRequest {
  sectorId: string;
  name: string;
  geometry: { latitude: number; longitude: number }[];
  startPoint: { latitude: number; longitude: number };
  endPoint: { latitude: number; longitude: number };
  direction: number;
}

export interface CreateStreetResponse extends ApiResponse<Street> {}

// Segment Endpoints
export interface CreateSegmentRequest {
  sectorId: string;
  streetId: string;
  startPoint: { latitude: number; longitude: number };
  endPoint: { latitude: number; longitude: number };
  geometry: { latitude: number; longitude: number }[];
  direction: number;
}

export interface CreateSegmentResponse extends ApiResponse<Segment> {}

// Execution Endpoints
export interface StartExecutionRequest {
  sectorId: string;
  workerId: string;
}

export interface StartExecutionResponse extends ApiResponse<Execution> {}

export interface StartSegmentRequest {
  executionId: string;
  segmentId: string;
  startNumber?: number;
}

export interface CompleteSegmentRequest {
  executionId: string;
  segmentId: string;
  endNumber?: number;
}

export interface CompleteSegmentResponse extends ApiResponse<Execution> {}

export interface PauseExecutionRequest {
  executionId: string;
  reason?: string;
}

export interface ResumeExecutionRequest {
  executionId: string;
}

export interface CompleteExecutionRequest {
  executionId: string;
}

export interface CompleteExecutionResponse extends ApiResponse<Execution> {}

// Metrics Endpoints
export interface GetMetricsResponse extends ApiResponse<ExecutionMetrics> {}

// Learning Endpoints
export interface GetRouteLearningResponse extends ApiResponse<RouteLearning> {}

// Location Tracking
export interface TrackLocationRequest {
  executionId: string;
  latitude: number;
  longitude: number;
  accuracy: number;
  altitude?: number;
  speed?: number;
  heading?: number;
}

export interface TrackLocationResponse extends ApiResponse<{ success: boolean; currentSegment?: Segment }> {}
