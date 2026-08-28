import { create } from 'zustand';
import type { Location } from '@types/domain';

interface LocationStore {
  currentLocation: Location | null;
  trackingHistory: Location[];
  isTracking: boolean;
  error: string | null;

  setCurrentLocation: (location: Location) => void;
  addTrackingPoint: (location: Location) => void;
  setTracking: (tracking: boolean) => void;
  clearTracking: () => void;
  setError: (error: string | null) => void;
}

export const useLocationStore = create<LocationStore>((set) => ({
  currentLocation: null,
  trackingHistory: [],
  isTracking: false,
  error: null,

  setCurrentLocation: (location) => set({ currentLocation: location }),

  addTrackingPoint: (location) =>
    set(({ trackingHistory }) => ({
      currentLocation: location,
      trackingHistory: [...trackingHistory, location],
    })),

  setTracking: (tracking) => set({ isTracking: tracking }),

  clearTracking: () =>
    set({
      currentLocation: null,
      trackingHistory: [],
      isTracking: false,
    }),

  setError: (error) => set({ error }),
}));
