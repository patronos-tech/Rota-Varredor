from app.repositories import RepositoryFactory
from app.schemas.schemas import SectorCreate, Sector
from app.core.exceptions import NotFoundException
from typing import List

class SectorService:
    def __init__(self):
        self.repo = RepositoryFactory.get_sector_repository()

    async def create_sector(self, sector: SectorCreate) -> Sector:
        return await self.repo.create(sector)

    async def get_sector(self, sector_id: str) -> Sector:
        sector = await self.repo.get_by_id(sector_id)
        if not sector:
            raise NotFoundException("Sector", sector_id)
        return sector

    async def list_sectors(self) -> List[Sector]:
        return await self.repo.list_all()

    async def update_sector(self, sector_id: str, sector: SectorCreate) -> Sector:
        existing = await self.repo.get_by_id(sector_id)
        if not existing:
            raise NotFoundException("Sector", sector_id)
        return await self.repo.update(sector_id, sector)

    async def delete_sector(self, sector_id: str) -> bool:
        existing = await self.repo.get_by_id(sector_id)
        if not existing:
            raise NotFoundException("Sector", sector_id)
        return await self.repo.delete(sector_id)
