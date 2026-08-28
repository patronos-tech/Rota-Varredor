import type { Segment, Coordinates, Street } from './domain';

export interface RouteOptimizationInput {
  currentLocation: Coordinates;
  segments: Segment[];
  completedSegmentIds: string[];
  workerSpeed: number;
}

export interface RouteOptimizationOutput {
  optimizedSegments: Segment[];
  totalDistance: number;
  estimatedDuration: number;
  nextSegment: Segment;
  confidence: number;
}

export interface LocationMatchResult {
  matched: boolean;
  nearestStreet?: Street;
  nearestSegment?: Segment;
  distance: number;
  withinTolerance: boolean;
  status: 'CORRECT' | 'APPROACHING' | 'INCORRECT';
}

export interface GPSSimulationConfig {
  enabled: boolean;
  speed: number;
  updateInterval: number;
  accuracy: number;
}

export interface SyncQueueItem {
  id: string;
  type: 'execution' | 'location' | 'segment';
  data: any;
  timestamp: Date;
  synced: boolean;
  attempts: number;
}

export interface OfflineDataStore {
  sectors: Map<string, any>;
  executions: Map<string, any>;
  locations: Location[];
  syncQueue: SyncQueueItem[];
}

export interface AIServiceConfig {
  ocrEnabled: boolean;
  visionEnabled: boolean;
  routeOptimizationEnabled: boolean;
  learningEnabled: boolean;
}

export interface Location {
  latitude: number;
  longitude: number;
  timestamp: Date;
  accuracy: number;
  altitude?: number;
  speed?: number;
  heading?: number;
}

export interface DistanceCalculation {
  distanceMeters: number;
  distanceKm: number;
}

export interface BearingCalculation {
  bearing: number;
  direction: string;
}
