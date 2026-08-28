from app.core.database import db
from app.repositories.sector_repository import SectorRepository
from app.repositories.execution_repository import ExecutionRepository

class RepositoryFactory:
    @staticmethod
    def get_sector_repository():
        return SectorRepository(db.db)
    
    @staticmethod
    def get_execution_repository():
        return ExecutionRepository(db.db)
