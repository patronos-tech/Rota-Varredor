import type { Coordinates } from '@types/domain';
import type { RouteOptimizationInput, RouteOptimizationOutput } from '@types/services';
import type { Segment } from '@types/domain';
import { calculateDistance } from '@utils/geo';

export class RoutingService {
  /**
   * Optimize route based on current location and completed segments
   */
  optimizeRoute(input: RouteOptimizationInput): RouteOptimizationOutput {
    const pendingSegments = input.segments.filter(
      (s) => !input.completedSegmentIds.includes(s.id)
    );

    if (pendingSegments.length === 0) {
      throw new Error('No pending segments available');
    }

    const orderedSegments = this.orderSegmentsByProximity(
      input.currentLocation,
      pendingSegments
    );

    const nextSegment = orderedSegments[0];
    let totalDistance = 0;
    let estimatedDuration = 0;

    // Calculate total distance and duration
    let lastPoint = input.currentLocation;
    for (const segment of orderedSegments) {
      const { distanceMeters } = calculateDistance(lastPoint, segment.startPoint);
      totalDistance += distanceMeters;
      lastPoint = segment.endPoint;

      // Estimate segment distance
      const segmentDistance = this.calculateSegmentDistance(segment);
      totalDistance += segmentDistance;

      // Estimate duration (worker speed on foot ~1.4 m/s)
      estimatedDuration += (segmentDistance / (input.workerSpeed / 3.6)) || 0;
    }

    return {
      optimizedSegments: orderedSegments,
      totalDistance,
      estimatedDuration,
      nextSegment,
      confidence: this.calculateConfidence(orderedSegments),
    };
  }

  /**
   * Order segments by proximity to current location
   */
  private orderSegmentsByProximity(location: Coordinates, segments: Segment[]): Segment[] {
    const scored = segments.map((segment) => ({
      segment,
      score: this.calculateSegmentScore(location, segment),
    }));

    scored.sort((a, b) => a.score - b.score);
    return scored.map((item) => item.segment);
  }

  /**
   * Calculate score for segment based on distance and suggested order
   */
  private calculateSegmentScore(location: Coordinates, segment: Segment): number {
    const { distanceMeters } = calculateDistance(location, segment.startPoint);
    // Lower score = better (closer and better order)
    return distanceMeters * 0.7 + segment.plannedOrder * 100;
  }

  /**
   * Calculate total distance of a segment
   */
  private calculateSegmentDistance(segment: Segment): number {
    let totalDistance = 0;
    for (let i = 0; i < segment.geometry.length - 1; i++) {
      const { distanceMeters } = calculateDistance(
        segment.geometry[i],
        segment.geometry[i + 1]
      );
      totalDistance += distanceMeters;
    }
    return totalDistance;
  }

  /**
   * Calculate confidence in route optimization (0-1)
   */
  private calculateConfidence(segments: Segment[]): number {
    if (segments.length === 0) return 0;
    // Confidence based on how well ordered segments are
    const orderingScore = segments.reduce((acc, seg, idx) => {
      const expected = seg.plannedOrder;
      const actual = idx;
      return acc + (1 - Math.abs(expected - actual) / segments.length);
    }, 0) / segments.length;

    return Math.max(0, Math.min(1, orderingScore));
  }
}
