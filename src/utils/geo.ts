// GPS and location utilities

import type { Coordinates, Location } from '@types/domain';
import type { DistanceCalculation, BearingCalculation } from '@types/services';

const EARTH_RADIUS_KM = 6371;
const EARTH_RADIUS_M = 6371000;

export function calculateDistance(from: Coordinates, to: Coordinates): DistanceCalculation {
  const dLat = toRad(to.latitude - from.latitude);
  const dLon = toRad(to.longitude - from.longitude);
  const lat1 = toRad(from.latitude);
  const lat2 = toRad(to.latitude);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distanceKm = EARTH_RADIUS_KM * c;
  const distanceMeters = distanceKm * 1000;

  return { distanceMeters, distanceKm };
}

export function calculateBearing(from: Coordinates, to: Coordinates): BearingCalculation {
  const dLon = toRad(to.longitude - from.longitude);
  const lat1 = toRad(from.latitude);
  const lat2 = toRad(to.latitude);

  const y = Math.sin(dLon) * Math.cos(lat2);
  const x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
  let bearing = Math.atan2(y, x);
  bearing = toDeg(bearing);
  bearing = (bearing + 360) % 360;

  const direction = getBearingDirection(bearing);

  return { bearing, direction };
}

function getBearingDirection(bearing: number): string {
  const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
  const index = Math.round(bearing / 45) % 8;
  return directions[index];
}

export function calculatePathDistance(path: Coordinates[]): DistanceCalculation {
  if (path.length < 2) {
    return { distanceMeters: 0, distanceKm: 0 };
  }

  let totalDistanceMeters = 0;

  for (let i = 0; i < path.length - 1; i++) {
    const { distanceMeters } = calculateDistance(path[i], path[i + 1]);
    totalDistanceMeters += distanceMeters;
  }

  return {
    distanceMeters: totalDistanceMeters,
    distanceKm: totalDistanceMeters / 1000,
  };
}

export function isPointNearPath(
  point: Coordinates,
  path: Coordinates[],
  toleranceMeters: number
): boolean {
  for (const pathPoint of path) {
    const { distanceMeters } = calculateDistance(point, pathPoint);
    if (distanceMeters <= toleranceMeters) {
      return true;
    }
  }
  return false;
}

export function findClosestPointOnPath(
  point: Coordinates,
  path: Coordinates[]
): { closestPoint: Coordinates; distance: number; index: number } {
  let minDistance = Infinity;
  let closestPoint = path[0];
  let closestIndex = 0;

  for (let i = 0; i < path.length; i++) {
    const { distanceMeters } = calculateDistance(point, path[i]);
    if (distanceMeters < minDistance) {
      minDistance = distanceMeters;
      closestPoint = path[i];
      closestIndex = i;
    }
  }

  return { closestPoint, distance: minDistance, index: closestIndex };
}

function toRad(degrees: number): number {
  return (degrees * Math.PI) / 180;
}

function toDeg(radians: number): number {
  return (radians * 180) / Math.PI;
}

export function formatDuration(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  if (hours > 0) {
    return `${hours}h ${minutes}m ${secs}s`;
  }
  if (minutes > 0) {
    return `${minutes}m ${secs}s`;
  }
  return `${secs}s`;
}

export function formatDistance(meters: number): string {
  if (meters >= 1000) {
    return `${(meters / 1000).toFixed(2)} km`;
  }
  return `${Math.round(meters)} m`;
}
