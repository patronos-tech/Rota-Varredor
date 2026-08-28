import type { Sector, Street, Segment, Coordinates } from '@types/domain';
import { generateId, getCurrentDate } from '@utils/helpers';

/**
 * Generate demo sector with streets and segments for Itaquera, São Paulo
 */
export function generateDemoSector(): Sector {
  const sectorId = 'demo-itaquera-001';
  const pickup: Coordinates = { latitude: -23.5505, longitude: -46.4704 };
  const dropoff: Coordinates = { latitude: -23.5515, longitude: -46.4714 };

  // Define demo streets
  const rua1: Street = {
    id: `street-${generateId()}`,
    sectorId,
    name: 'Rua A - Itaquera',
    geometry: [
      { latitude: -23.5500, longitude: -46.4700 },
      { latitude: -23.5505, longitude: -46.4705 },
      { latitude: -23.5510, longitude: -46.4710 },
    ],
    startPoint: { latitude: -23.5500, longitude: -46.4700 },
    endPoint: { latitude: -23.5510, longitude: -46.4710 },
    direction: 45,
    status: 'PENDING',
    suggestedOrder: 1,
    segments: [],
  };

  const rua2: Street = {
    id: `street-${generateId()}`,
    sectorId,
    name: 'Rua B - Itaquera',
    geometry: [
      { latitude: -23.5510, longitude: -46.4700 },
      { latitude: -23.5515, longitude: -46.4705 },
      { latitude: -23.5520, longitude: -46.4710 },
    ],
    startPoint: { latitude: -23.5510, longitude: -46.4700 },
    endPoint: { latitude: -23.5520, longitude: -46.4710 },
    direction: 45,
    status: 'PENDING',
    suggestedOrder: 2,
    segments: [],
  };

  const rua3: Street = {
    id: `street-${generateId()}`,
    sectorId,
    name: 'Rua C - Itaquera',
    geometry: [
      { latitude: -23.5500, longitude: -46.4710 },
      { latitude: -23.5505, longitude: -46.4715 },
      { latitude: -23.5510, longitude: -46.4720 },
    ],
    startPoint: { latitude: -23.5500, longitude: -46.4710 },
    endPoint: { latitude: -23.5510, longitude: -46.4720 },
    direction: 45,
    status: 'PENDING',
    suggestedOrder: 3,
    segments: [],
  };

  // Create segments for each street
  rua1.segments = [
    {
      id: `segment-${generateId()}`,
      streetId: rua1.id,
      sectorId,
      startPoint: rua1.startPoint,
      endPoint: { latitude: -23.5505, longitude: -46.4705 },
      geometry: rua1.geometry.slice(0, 2),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 1,
    },
    {
      id: `segment-${generateId()}`,
      streetId: rua1.id,
      sectorId,
      startPoint: { latitude: -23.5505, longitude: -46.4705 },
      endPoint: rua1.endPoint,
      geometry: rua1.geometry.slice(1),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 2,
    },
  ];

  rua2.segments = [
    {
      id: `segment-${generateId()}`,
      streetId: rua2.id,
      sectorId,
      startPoint: rua2.startPoint,
      endPoint: { latitude: -23.5515, longitude: -46.4705 },
      geometry: rua2.geometry.slice(0, 2),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 3,
    },
    {
      id: `segment-${generateId()}`,
      streetId: rua2.id,
      sectorId,
      startPoint: { latitude: -23.5515, longitude: -46.4705 },
      endPoint: rua2.endPoint,
      geometry: rua2.geometry.slice(1),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 4,
    },
  ];

  rua3.segments = [
    {
      id: `segment-${generateId()}`,
      streetId: rua3.id,
      sectorId,
      startPoint: rua3.startPoint,
      endPoint: { latitude: -23.5505, longitude: -46.4715 },
      geometry: rua3.geometry.slice(0, 2),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 5,
    },
    {
      id: `segment-${generateId()}`,
      streetId: rua3.id,
      sectorId,
      startPoint: { latitude: -23.5505, longitude: -46.4715 },
      endPoint: rua3.endPoint,
      geometry: rua3.geometry.slice(1),
      direction: 45,
      status: 'PENDING',
      plannedOrder: 6,
    },
  ];

  const sector: Sector = {
    id: sectorId,
    name: 'Setor Demo - Itaquera',
    code: 'DEMO-ITA-001',
    streets: [rua1, rua2, rua3],
    pickupPoint: pickup,
    dropoffPoint: dropoff,
    status: 'READY',
    createdAt: getCurrentDate(),
    updatedAt: getCurrentDate(),
  };

  return sector;
}
