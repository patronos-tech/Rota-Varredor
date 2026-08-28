// Core Domain Types

export type SectorStatus = 'DRAFT' | 'READY' | 'IN_EXECUTION' | 'COMPLETED';
export type StreetStatus = 'PENDING' | 'IN_EXECUTION' | 'COMPLETED';
export type SegmentStatus = 'PENDING' | 'IN_EXECUTION' | 'COMPLETED';
export type ExecutionStatus = 'STARTED' | 'PAUSED' | 'COMPLETED';
export type RouteStatus = 'PLANNED' | 'REAL';

export interface Coordinates {
  latitude: number;
  longitude: number;
}

export interface Sector {
  id: string;
  name: string;
  code: string;
  mapImage?: string; // Base64 or URI
  streets: Street[];
  pickupPoint: Coordinates;
  dropoffPoint: Coordinates;
  status: SectorStatus;
  createdAt: Date;
  updatedAt: Date;
}

export interface Street {
  id: string;
  sectorId: string;
  name: string;
  geometry: Coordinates[]; // Array of coordinates defining the street
  startPoint: Coordinates;
  endPoint: Coordinates;
  direction: number; // bearing in degrees
  status: StreetStatus;
  segments: Segment[];
  suggestedOrder: number;
}

export interface Segment {
  id: string;
  streetId: string;
  sectorId: string;
  startPoint: Coordinates;
  endPoint: Coordinates;
  geometry: Coordinates[];
  direction: number;
  status: SegmentStatus;
  plannedOrder: number;
}

export interface Location {
  id: string;
  latitude: number;
  longitude: number;
  timestamp: Date;
  accuracy: number;
  altitude?: number;
  speed?: number;
  heading?: number;
}

export interface SegmentExecution {
  id: string;
  executionId: string;
  segmentId: string;
  streetId: string;
  sectorId: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  startNumber?: number; // Initial street number (e.g., 100)
  endNumber?: number; // Final street number (e.g., 350)
  startedAt?: Date;
  completedAt?: Date;
  startLocation?: Location;
  endLocation?: Location;
  trackingPoints: Location[];
  duration: number; // in seconds
  distance: number; // in meters
  notes?: string;
}

export interface Execution {
  id: string;
  sectorId: string;
  workerId: string;
  status: ExecutionStatus;
  startedAt: Date;
  completedAt?: Date;
  segments: SegmentExecution[];
  plannedRoute: Route;
  realRoute: Route;
  totalDistance: number; // meters
  totalDuration: number; // seconds
  pauses: Pause[];
  issues: Issue[];
}

export interface Route {
  id: string;
  executionId: string;
  type: RouteStatus;
  segments: Segment[];
  totalDistance: number;
  estimatedDuration: number;
  createdAt: Date;
}

export interface Pause {
  id: string;
  executionId: string;
  startedAt: Date;
  resumedAt?: Date;
  reason?: string;
  duration: number; // seconds
}

export interface Issue {
  id: string;
  executionId: string;
  segmentId: string;
  type: 'BLOCKED_STREET' | 'CONSTRUCTION' | 'NO_ACCESS' | 'RAIN' | 'RISK' | 'OTHER';
  description?: string;
  reportedAt: Date;
  resolved: boolean;
}

export interface ExecutionMetrics {
  executionId: string;
  totalSegments: number;
  completedSegments: number;
  completionPercentage: number;
  plannedDistance: number;
  realDistance: number;
  distanceEfficiency: number; // real / planned
  plannedDuration: number;
  realDuration: number;
  timeEfficiency: number; // real / planned
  averageSpeed: number; // km/h
  deviations: number;
  pauseCount: number;
  totalPauseTime: number;
}

export interface ExecutionHistory {
  id: string;
  sectorId: string;
  workerId: string;
  executedAt: Date;
  summary: string;
  metrics: ExecutionMetrics;
  segments: SegmentExecution[];
}

export interface StreetPattern {
  segmentId: string;
  streetId: string;
  frequency: number; // how many times has this order appeared
  startNumber?: number; // most common starting number
  endNumber?: number; // most common ending number
  averageDuration: number; // seconds
  averageDistance: number; // meters
}

export interface RouteLearning {
  sectorId: string;
  patterns: StreetPattern[];
  lastUpdated: Date;
  executionCount: number;
  suggestedOrder: string[]; // segment IDs in suggested order
  confidence: number; // 0-1
}
