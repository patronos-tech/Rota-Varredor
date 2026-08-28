from app.repositories import RepositoryFactory
from app.schemas.schemas import ExecutionCreate
from app.core.exceptions import NotFoundException
from typing import List

class ExecutionService:
    def __init__(self):
        self.repo = RepositoryFactory.get_execution_repository()
        self.sector_repo = RepositoryFactory.get_sector_repository()

    async def start_execution(self, execution: ExecutionCreate):
        # Verify sector exists
        sector = await self.sector_repo.get_by_id(execution.sector_id)
        if not sector:
            raise NotFoundException("Sector", execution.sector_id)
        
        return await self.repo.create(execution)

    async def get_execution(self, execution_id: str):
        execution = await self.repo.get_by_id(execution_id)
        if not execution:
            raise NotFoundException("Execution", execution_id)
        return execution

    async def list_executions(self, sector_id: str) -> List[dict]:
        return await self.repo.list_by_sector(sector_id)

    async def complete_segment(self, execution_id: str, segment_id: str):
        execution = await self.repo.get_by_id(execution_id)
        if not execution:
            raise NotFoundException("Execution", execution_id)
        
        # Update segment status
        for segment in execution.get("segments", []):
            if segment["id"] == segment_id:
                segment["status"] = "COMPLETED"
                break
        
        return await self.repo.update(execution_id, execution)

    async def pause_execution(self, execution_id: str, reason: str = None):
        execution = await self.repo.get_by_id(execution_id)
        if not execution:
            raise NotFoundException("Execution", execution_id)
        
        return await self.repo.update(execution_id, {"status": "PAUSED"})

    async def resume_execution(self, execution_id: str):
        execution = await self.repo.get_by_id(execution_id)
        if not execution:
            raise NotFoundException("Execution", execution_id)
        
        return await self.repo.update(execution_id, {"status": "STARTED"})

    async def complete_execution(self, execution_id: str):
        execution = await self.repo.get_by_id(execution_id)
        if not execution:
            raise NotFoundException("Execution", execution_id)
        
        return await self.repo.update(execution_id, {"status": "COMPLETED"})
