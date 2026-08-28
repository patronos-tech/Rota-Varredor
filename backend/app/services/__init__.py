from app.services.sector_service import SectorService
from app.services.execution_service import ExecutionService

class ServiceFactory:
    @staticmethod
    def get_sector_service() -> SectorService:
        return SectorService()
    
    @staticmethod
    def get_execution_service() -> ExecutionService:
        return ExecutionService()
