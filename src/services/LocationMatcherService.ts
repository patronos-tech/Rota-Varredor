import type { Coordinates, Location } from '@types/domain';
import type { LocationMatchResult } from '@types/services';
import { calculateDistance } from '@utils/geo';
import type { Street, Segment } from '@types/domain';

const DEFAULT_ACCURACY_THRESHOLD = 20; // meters

export class LocationMatcherService {
  private accuracyThreshold: number;

  constructor(accuracyThreshold: number = DEFAULT_ACCURACY_THRESHOLD) {
    this.accuracyThreshold = accuracyThreshold;
  }

  /**
   * Match current location to nearest street and segment
   */
  matchLocation(
    currentLocation: Coordinates,
    streets: Street[],
    currentAccuracy: number
  ): LocationMatchResult {
    let nearestStreet: Street | undefined;
    let nearestSegment: Segment | undefined;
    let minDistance = Infinity;

    for (const street of streets) {
      for (const segment of street.segments) {
        const distance = this.distanceToSegment(currentLocation, segment);

        if (distance < minDistance) {
          minDistance = distance;
          nearestStreet = street;
          nearestSegment = segment;
        }
      }
    }

    const withinTolerance = minDistance <= this.accuracyThreshold + currentAccuracy;
    const status = this.determineStatus(minDistance, currentAccuracy);

    return {
      matched: withinTolerance,
      nearestStreet,
      nearestSegment,
      distance: minDistance,
      withinTolerance,
      status,
    };
  }

  /**
   * Calculate distance from point to segment
   */
  private distanceToSegment(point: Coordinates, segment: Segment): number {
    let minDistance = Infinity;

    // Check distance to each point in segment geometry
    for (const segmentPoint of segment.geometry) {
      const { distanceMeters } = calculateDistance(point, segmentPoint);
      minDistance = Math.min(minDistance, distanceMeters);
    }

    return minDistance;
  }

  /**
   * Determine status based on distance
   */
  private determineStatus(
    distance: number,
    accuracy: number
  ): 'CORRECT' | 'APPROACHING' | 'INCORRECT' {
    const tolerance = this.accuracyThreshold + accuracy;
    const approachingDistance = tolerance * 2;

    if (distance <= tolerance) {
      return 'CORRECT';
    }
    if (distance <= approachingDistance) {
      return 'APPROACHING';
    }
    return 'INCORRECT';
  }
}
