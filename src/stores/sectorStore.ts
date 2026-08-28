import { create } from 'zustand';
import type { Sector, Street, Segment, Execution, Location, ExecutionMetrics } from '@types/domain';
import { generateId, getCurrentDate } from '@utils/helpers';

interface SectorStore {
  sectors: Map<string, Sector>;
  currentSector: Sector | null;
  isLoading: boolean;
  error: string | null;

  setSectors: (sectors: Sector[]) => void;
  setCurrentSector: (sector: Sector) => void;
  addSector: (sector: Sector) => void;
  updateSector: (sector: Sector) => void;
  setSectorStatus: (sectorId: string, status: 'DRAFT' | 'READY' | 'IN_EXECUTION' | 'COMPLETED') => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clear: () => void;
}

export const useSectorStore = create<SectorStore>((set) => ({
  sectors: new Map(),
  currentSector: null,
  isLoading: false,
  error: null,

  setSectors: (sectors) =>
    set(() => {
      const map = new Map();
      sectors.forEach((s) => map.set(s.id, s));
      return { sectors: map };
    }),

  setCurrentSector: (sector) => set({ currentSector: sector }),

  addSector: (sector) =>
    set(({ sectors }) => {
      const newSectors = new Map(sectors);
      newSectors.set(sector.id, sector);
      return { sectors: newSectors };
    }),

  updateSector: (sector) =>
    set(({ sectors, currentSector }) => {
      const newSectors = new Map(sectors);
      newSectors.set(sector.id, sector);
      return {
        sectors: newSectors,
        currentSector: currentSector?.id === sector.id ? sector : currentSector,
      };
    }),

  setSectorStatus: (sectorId, status) =>
    set(({ sectors, currentSector }) => {
      const sector = sectors.get(sectorId);
      if (!sector) return {};

      const updated = { ...sector, status, updatedAt: getCurrentDate() };
      const newSectors = new Map(sectors);
      newSectors.set(sectorId, updated);

      return {
        sectors: newSectors,
        currentSector: currentSector?.id === sectorId ? updated : currentSector,
      };
    }),

  setLoading: (loading) => set({ isLoading: loading }),
  setError: (error) => set({ error }),

  clear: () =>
    set({
      sectors: new Map(),
      currentSector: null,
      isLoading: false,
      error: null,
    }),
}));
