import React, { useEffect, useState, useRef } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import { useExecutionStore, useLocationStore, useSectorStore, useAppStore } from '@stores';
import { Button, LocationStatusIndicator, ProgressIndicator, SegmentList } from '@components';
import { GPSService } from '@services/GPSService';
import { LocationMatcherService } from '@services/LocationMatcherService';
import { RoutingService } from '@services/RoutingService';
import type { Execution, ExecutionStatus } from '@types/domain';
import { generateId, getCurrentDate } from '@utils/helpers';
import { logger } from '@utils/logger';

export function ExecutionScreen({ route, navigation }: any) {
  const { sectorId } = route.params;
  const sector = useSectorStore((s) => s.sectors.get(sectorId));
  const [execution, setExecution] = useState<Execution | null>(null);
  const [executionStatus, setExecutionStatus] = useState<ExecutionStatus>('STARTED');
  const [locationStatus, setLocationStatus] = useState<'correct' | 'approaching' | 'incorrect'>('incorrect');
  const [currentDistance, setCurrentDistance] = useState<number>(0);
  const [completedCount, setCompletedCount] = useState(0);

  const gpsServiceRef = useRef<GPSService | null>(null);
  const locationMatcherRef = useRef<LocationMatcherService | null>(null);
  const routingServiceRef = useRef<RoutingService | null>(null);

  const { setCurrentLocation, currentLocation } = useLocationStore();
  const { config } = useAppStore();

  useEffect(() => {
    if (sector) {
      initializeExecution();
      initializeServices();
    }
    return () => {
      if (gpsServiceRef.current) {
        gpsServiceRef.current.stopTracking();
      }
    };
  }, [sector]);

  const initializeServices = () => {
    locationMatcherRef.current = new LocationMatcherService(config.gps.accuracyThreshold);
    routingServiceRef.current = new RoutingService();
    gpsServiceRef.current = new GPSService(
      config.gps.updateInterval,
      config.demo.enableGpsSimulation
    );
  };

  const initializeExecution = async () => {
    if (!sector) return;

    try {
      // Get all segments from sector
      const allSegments = sector.streets.flatMap((street) => street.segments);

      const newExecution: Execution = {
        id: generateId(),
        sectorId: sector.id,
        workerId: 'demo-worker',
        status: 'STARTED',
        startedAt: getCurrentDate(),
        segments: allSegments.map((segment) => ({
          id: segment.id,
          executionId: '',
          segmentId: segment.id,
          streetId: segment.streetId,
          sectorId: segment.sectorId,
          status: 'PENDING' as const,
          trackingPoints: [],
          duration: 0,
          distance: 0,
        })),
        plannedRoute: {
          id: generateId(),
          executionId: '',
          type: 'PLANNED',
          segments: allSegments,
          totalDistance: 1000,
          estimatedDuration: 3600,
          createdAt: getCurrentDate(),
        },
        realRoute: {
          id: generateId(),
          executionId: '',
          type: 'REAL',
          segments: [],
          totalDistance: 0,
          estimatedDuration: 0,
          createdAt: getCurrentDate(),
        },
        totalDistance: 0,
        totalDuration: 0,
        pauses: [],
        issues: [],
      };

      setExecution(newExecution);
      startGPSTracking(newExecution, allSegments);
    } catch (error) {
      logger.error('Failed to initialize execution', error);
      Alert.alert('Erro', 'Falha ao inicializar execução');
    }
  };

  const startGPSTracking = async (exec: Execution, segments: any[]) => {
    if (!gpsServiceRef.current) return;

    try {
      await gpsServiceRef.current.startTracking(
        (location) => {
          setCurrentLocation({
            id: generateId(),
            latitude: location.latitude,
            longitude: location.longitude,
            accuracy: location.accuracy,
            altitude: location.altitude,
            speed: location.speed,
            heading: location.heading,
            timestamp: location.timestamp,
          });

          // Match location to street
          if (locationMatcherRef.current && sector) {
            const result = locationMatcherRef.current.matchLocation(
              { latitude: location.latitude, longitude: location.longitude },
              sector.streets,
              location.accuracy
            );

            setLocationStatus(result.status === 'CORRECT' ? 'correct' : result.status === 'APPROACHING' ? 'approaching' : 'incorrect');
            setCurrentDistance(result.distance);
          }
        },
        config.demo.enableGpsSimulation
      );

      logger.info('GPS tracking started');
    } catch (error) {
      logger.error('Failed to start GPS tracking', error);
    }
  };

  const handleCompleteSegment = (segmentId: string) => {
    if (!execution) return;

    const updatedSegments = execution.segments.map((seg) =>
      seg.segmentId === segmentId ? { ...seg, status: 'COMPLETED' as const } : seg
    );

    const completed = updatedSegments.filter((s) => s.status === 'COMPLETED').length;
    setCompletedCount(completed);

    setExecution({
      ...execution,
      segments: updatedSegments,
    });
  };

  const handleCompleteExecution = async () => {
    if (!execution) return;

    const allCompleted = execution.segments.every((s) => s.status === 'COMPLETED');

    if (!allCompleted) {
      Alert.alert('Atenção', 'Nem todos os trechos foram completados. Deseja finalizar mesmo assim?', [
        { text: 'Cancelar', onPress: () => {} },
        {
          text: 'Finalizar',
          onPress: () => {
            setExecutionStatus('COMPLETED');
            navigation.navigate('History', { executionId: execution.id });
          },
        },
      ]);
    } else {
      setExecutionStatus('COMPLETED');
      navigation.navigate('History', { executionId: execution.id });
    }
  };

  if (!sector || !execution) {
    return (
      <View style={styles.centerContainer}>
        <Text>Carregando execução...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Executando</Text>
        <Text style={styles.subtitle}>{sector.name}</Text>
      </View>

      <View style={styles.content}>
        <LocationStatusIndicator
          status={locationStatus}
          distance={currentDistance}
          street={currentLocation ? `${currentLocation.latitude.toFixed(4)}, ${currentLocation.longitude.toFixed(4)}` : undefined}
        />

        <ProgressIndicator
          completed={completedCount}
          total={execution.segments.length}
        />

        <Text style={styles.sectionTitle}>Trechos</Text>
        <SegmentList
          segments={execution.segments.map((seg, idx) => ({
            id: seg.segmentId,
            streetName: sector.streets.find((s) => s.id === seg.streetId)?.name || `Trecho ${idx + 1}`,
            status: seg.status,
            index: idx,
          }))}
          currentSegmentId={execution.segments.find((s) => s.status === 'PENDING')?.segmentId}
        />

        <View style={styles.actions}>
          <Button
            title="Pausar"
            variant="secondary"
            onPress={() => setExecutionStatus('PAUSED')}
            disabled={executionStatus !== 'STARTED'}
          />
          <Button
            title="Finalizar Execução"
            variant="primary"
            onPress={handleCompleteExecution}
            disabled={executionStatus === 'COMPLETED'}
          />
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f6fa',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    backgroundColor: '#3498db',
    paddingTop: 16,
    paddingBottom: 16,
    paddingHorizontal: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  subtitle: {
    fontSize: 14,
    color: '#ecf0f1',
    marginTop: 4,
  },
  content: {
    paddingHorizontal: 16,
    paddingVertical: 16,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 12,
    marginTop: 12,
  },
  actions: {
    gap: 12,
    marginTop: 24,
  },
});
