import type { ApiClient } from './ApiClient';
import type { GetSectorsResponse } from '@types/api';
import type { Sector } from '@types/domain';

export class SectorApiService {
  constructor(private apiClient: ApiClient) {}

  async getSectors(): Promise<Sector[]> {
    const response = await this.apiClient.get<GetSectorsResponse>('/api/sectors');
    if (!response.success || !response.data) {
      throw new Error(response.error?.message || 'Failed to fetch sectors');
    }
    return response.data;
  }

  async getSector(sectorId: string): Promise<Sector> {
    const response = await this.apiClient.get<{ data: Sector }>(
      `/api/sectors/${sectorId}`
    );
    return response.data;
  }

  async createSector(name: string, code: string, coordinates: any): Promise<Sector> {
    const response = await this.apiClient.post<{ data: Sector }>(
      '/api/sectors',
      {
        name,
        code,
        ...coordinates,
      }
    );
    return response.data;
  }
}
