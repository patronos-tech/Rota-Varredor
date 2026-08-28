import { create } from 'zustand';
import type { Execution, SegmentExecution } from '@types/domain';
import { getCurrentDate } from '@utils/helpers';

interface ExecutionStore {
  executions: Map<string, Execution>;
  currentExecution: Execution | null;
  isLoading: boolean;
  error: string | null;

  setExecutions: (executions: Execution[]) => void;
  setCurrentExecution: (execution: Execution) => void;
  addExecution: (execution: Execution) => void;
  updateExecution: (execution: Execution) => void;
  updateSegmentExecution: (executionId: string, segment: SegmentExecution) => void;
  setExecutionStatus: (executionId: string, status: 'STARTED' | 'PAUSED' | 'COMPLETED') => void;
  addLocationTracking: (
    executionId: string,
    segmentId: string,
    location: { latitude: number; longitude: number; accuracy: number }
  ) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clear: () => void;
}

export const useExecutionStore = create<ExecutionStore>((set) => ({
  executions: new Map(),
  currentExecution: null,
  isLoading: false,
  error: null,

  setExecutions: (executions) =>
    set(() => {
      const map = new Map();
      executions.forEach((e) => map.set(e.id, e));
      return { executions: map };
    }),

  setCurrentExecution: (execution) => set({ currentExecution: execution }),

  addExecution: (execution) =>
    set(({ executions }) => {
      const newExecutions = new Map(executions);
      newExecutions.set(execution.id, execution);
      return { executions: newExecutions, currentExecution: execution };
    }),

  updateExecution: (execution) =>
    set(({ executions, currentExecution }) => {
      const newExecutions = new Map(executions);
      newExecutions.set(execution.id, execution);
      return {
        executions: newExecutions,
        currentExecution: currentExecution?.id === execution.id ? execution : currentExecution,
      };
    }),

  updateSegmentExecution: (executionId, segment) =>
    set(({ executions, currentExecution }) => {
      const execution = executions.get(executionId);
      if (!execution) return {};

      const segmentIndex = execution.segments.findIndex((s) => s.id === segment.id);
      if (segmentIndex === -1) {
        execution.segments.push(segment);
      } else {
        execution.segments[segmentIndex] = segment;
      }

      const updated = { ...execution };
      const newExecutions = new Map(executions);
      newExecutions.set(executionId, updated);

      return {
        executions: newExecutions,
        currentExecution: currentExecution?.id === executionId ? updated : currentExecution,
      };
    }),

  setExecutionStatus: (executionId, status) =>
    set(({ executions, currentExecution }) => {
      const execution = executions.get(executionId);
      if (!execution) return {};

      const updated: Execution = {
        ...execution,
        status,
        ...(status === 'COMPLETED' && { completedAt: getCurrentDate() }),
      };

      const newExecutions = new Map(executions);
      newExecutions.set(executionId, updated);

      return {
        executions: newExecutions,
        currentExecution: currentExecution?.id === executionId ? updated : currentExecution,
      };
    }),

  addLocationTracking: (executionId, segmentId, location) =>
    set(({ executions, currentExecution }) => {
      const execution = executions.get(executionId);
      if (!execution) return {};

      const segment = execution.segments.find((s) => s.id === segmentId);
      if (segment) {
        segment.trackingPoints.push({
          id: Math.random().toString(36).substring(7),
          latitude: location.latitude,
          longitude: location.longitude,
          accuracy: location.accuracy,
          timestamp: getCurrentDate(),
        });
      }

      const newExecutions = new Map(executions);
      newExecutions.set(executionId, execution);

      return {
        executions: newExecutions,
        currentExecution: currentExecution?.id === executionId ? execution : currentExecution,
      };
    }),

  setLoading: (loading) => set({ isLoading: loading }),
  setError: (error) => set({ error }),

  clear: () =>
    set({
      executions: new Map(),
      currentExecution: null,
      isLoading: false,
      error: null,
    }),
}));
