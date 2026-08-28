import type { Sector, Street, Segment, Execution, Location } from './domain';

export interface SectorCardProps {
  sector: Sector;
  onPress: (sectorId: string) => void;
  isLoading?: boolean;
}

export interface StreetListItemProps {
  street: Street;
  index: number;
  onPress?: () => void;
  isCompleted?: boolean;
}

export interface SegmentListItemProps {
  segment: Segment;
  index: number;
  status: string;
  onPress?: () => void;
}

export interface MapDisplayProps {
  sector: Sector;
  currentLocation?: Location;
  currentSegment?: Segment;
  plannedRoute?: Segment[];
  onLocationUpdate?: (location: Location) => void;
}

export interface ExecutionProgressProps {
  execution: Execution;
  completedCount: number;
  totalCount: number;
}

export interface LocationStatusIndicatorProps {
  status: 'correct' | 'approaching' | 'incorrect';
  street?: Street;
  distance?: number;
}

export interface SegmentStartFormProps {
  onSubmit: (startNumber: number) => void;
  isLoading?: boolean;
}

export interface SegmentEndFormProps {
  onSubmit: (endNumber: number) => void;
  isLoading?: boolean;
}
