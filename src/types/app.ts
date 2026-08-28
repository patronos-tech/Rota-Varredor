export interface AppConfig {
  mode: 'demo' | 'production';
  backend: {
    url: string;
    port: number;
  };
  gps: {
    enabled: boolean;
    updateInterval: number;
    accuracyThreshold: number;
  };
  maps: {
    provider: 'demo' | 'google' | 'mapbox';
    apiKey?: string;
  };
  demo: {
    sectorId: string;
    enableGpsSimulation: boolean;
  };
}

export interface AppState {
  isInitialized: boolean;
  currentUser?: {
    id: string;
    name: string;
    role: 'worker' | 'supervisor' | 'admin';
  };
  currentSector?: string;
  currentExecution?: string;
  isOnline: boolean;
  lastSyncTime?: Date;
}

export interface ValidationResult {
  isValid: boolean;
  errors: ValidationError[];
}

export interface ValidationError {
  field: string;
  message: string;
  code: string;
}
