import { create } from 'zustand';
import type { AppState, AppConfig } from '@types/app';

interface AppStore extends AppState {
  config: AppConfig;
  setConfig: (config: AppConfig) => void;
  setInitialized: (initialized: boolean) => void;
  setCurrentUser: (user: AppState['currentUser']) => void;
  setCurrentSector: (sectorId: string | undefined) => void;
  setCurrentExecution: (executionId: string | undefined) => void;
  setOnline: (online: boolean) => void;
  setLastSyncTime: (time: Date) => void;
  reset: () => void;
}

const defaultConfig: AppConfig = {
  mode: 'demo',
  backend: { url: 'http://localhost:8000', port: 8000 },
  gps: { enabled: true, updateInterval: 5000, accuracyThreshold: 20 },
  maps: { provider: 'demo' },
  demo: { sectorId: 'demo-itaquera-001', enableGpsSimulation: true },
};

export const useAppStore = create<AppStore>((set) => ({
  isInitialized: false,
  currentUser: undefined,
  currentSector: undefined,
  currentExecution: undefined,
  isOnline: true,
  lastSyncTime: undefined,
  config: defaultConfig,

  setConfig: (config) => set({ config }),
  setInitialized: (initialized) => set({ isInitialized: initialized }),
  setCurrentUser: (user) => set({ currentUser: user }),
  setCurrentSector: (sectorId) => set({ currentSector: sectorId }),
  setCurrentExecution: (executionId) => set({ currentExecution: executionId }),
  setOnline: (online) => set({ isOnline: online }),
  setLastSyncTime: (time) => set({ lastSyncTime: time }),

  reset: () =>
    set({
      isInitialized: false,
      currentUser: undefined,
      currentSector: undefined,
      currentExecution: undefined,
      isOnline: true,
      lastSyncTime: undefined,
    }),
}));
