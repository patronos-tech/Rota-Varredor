import type { Coordinates, Location } from '@types/domain';
import type { GPSSimulationConfig } from '@types/services';
import * as Location_Module from 'expo-location';
import { logger } from '@utils/logger';

export interface GPSUpdate {
  latitude: number;
  longitude: number;
  accuracy: number;
  altitude?: number;
  speed?: number;
  heading?: number;
  timestamp: Date;
}

export class GPSService {
  private locationSubscription: any = null;
  private updateInterval: number = 5000; // 5 seconds
  private simulationMode: boolean = false;
  private simulationConfig: GPSSimulationConfig = {
    enabled: false,
    speed: 1.4, // m/s (walking speed)
    updateInterval: 5000,
    accuracy: 5,
  };
  private currentSimulatedLocation: Coordinates | null = null;
  private onLocationUpdate: ((location: GPSUpdate) => void) | null = null;

  constructor(updateInterval: number = 5000, simulationMode: boolean = false) {
    this.updateInterval = updateInterval;
    this.simulationMode = simulationMode;
  }

  /**
   * Request location permissions
   */
  async requestPermissions(): Promise<boolean> {
    try {
      const { status } = await Location_Module.requestForegroundPermissionsAsync();
      logger.info(`Location permission status: ${status}`);
      return status === 'granted';
    } catch (error) {
      logger.error('Failed to request location permissions', error);
      return false;
    }
  }

  /**
   * Start listening to GPS updates
   */
  async startTracking(
    onUpdate: (location: GPSUpdate) => void,
    enableSimulation: boolean = false
  ): Promise<void> {
    this.onLocationUpdate = onUpdate;

    if (enableSimulation || this.simulationMode) {
      this.startSimulation();
    } else {
      this.startRealGPS();
    }
  }

  /**
   * Start real GPS tracking
   */
  private async startRealGPS(): Promise<void> {
    try {
      const hasPermission = await this.requestPermissions();
      if (!hasPermission) {
        logger.warn('Location permission not granted, switching to simulation');
        this.startSimulation();
        return;
      }

      this.locationSubscription = await Location_Module.watchPositionAsync(
        {
          accuracy: Location_Module.Accuracy.BestForNavigation,
          timeInterval: this.updateInterval,
          distanceInterval: 1,
        },
        (location: any) => {
          if (this.onLocationUpdate) {
            this.onLocationUpdate({
              latitude: location.coords.latitude,
              longitude: location.coords.longitude,
              accuracy: location.coords.accuracy || 10,
              altitude: location.coords.altitude,
              speed: location.coords.speed,
              heading: location.coords.heading,
              timestamp: new Date(location.timestamp),
            });
          }
        }
      );

      logger.info('GPS tracking started');
    } catch (error) {
      logger.error('Failed to start GPS tracking', error);
      this.startSimulation();
    }
  }

  /**
   * Start GPS simulation for testing
   */
  private startSimulation(): void {
    logger.info('Starting GPS simulation mode');
    // Initial location (demo sector in Itaquera, São Paulo)
    this.currentSimulatedLocation = {
      latitude: -23.5505,
      longitude: -46.4704,
    };

    const simulationInterval = setInterval(() => {
      if (this.currentSimulatedLocation && this.onLocationUpdate) {
        // Random walk simulation
        const deltaLat = (Math.random() - 0.5) * 0.0001;
        const deltaLon = (Math.random() - 0.5) * 0.0001;

        this.currentSimulatedLocation.latitude += deltaLat;
        this.currentSimulatedLocation.longitude += deltaLon;

        this.onLocationUpdate({
          latitude: this.currentSimulatedLocation.latitude,
          longitude: this.currentSimulatedLocation.longitude,
          accuracy: this.simulationConfig.accuracy,
          timestamp: new Date(),
        });
      }
    }, this.simulationConfig.updateInterval);

    (this as any).simulationInterval = simulationInterval;
  }

  /**
   * Move simulated location to a specific point
   */
  moveSimulationTo(coordinates: Coordinates): void {
    if (this.simulationMode || !this.locationSubscription) {
      this.currentSimulatedLocation = coordinates;
      logger.info(`Simulated location moved to ${coordinates.latitude}, ${coordinates.longitude}`);
    }
  }

  /**
   * Stop GPS tracking
   */
  stopTracking(): void {
    if (this.locationSubscription) {
      this.locationSubscription.remove();
      this.locationSubscription = null;
    }
    if ((this as any).simulationInterval) {
      clearInterval((this as any).simulationInterval);
      (this as any).simulationInterval = null;
    }
    this.onLocationUpdate = null;
    logger.info('GPS tracking stopped');
  }

  /**
   * Get current location (one-time)
   */
  async getCurrentLocation(): Promise<GPSUpdate> {
    try {
      const location = await Location_Module.getCurrentPositionAsync({
        accuracy: Location_Module.Accuracy.BestForNavigation,
      });

      return {
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
        accuracy: location.coords.accuracy || 10,
        altitude: location.coords.altitude,
        speed: location.coords.speed,
        heading: location.coords.heading,
        timestamp: new Date(location.timestamp),
      };
    } catch (error) {
      logger.error('Failed to get current location', error);
      throw error;
    }
  }
}
