from motor.motor_asyncio import AsyncIOMotorDatabase
from app.schemas.schemas import Sector, SectorCreate
from bson import ObjectId
from typing import List, Optional
from datetime import datetime

class SectorRepository:
    def __init__(self, db: AsyncIOMotorDatabase):
        self.collection = db["sectors"]

    async def create(self, sector: SectorCreate) -> Sector:
        sector_dict = sector.dict()
        sector_dict["created_at"] = datetime.utcnow()
        sector_dict["updated_at"] = datetime.utcnow()
        sector_dict["status"] = "READY"
        
        result = await self.collection.insert_one(sector_dict)
        sector_dict["_id"] = str(result.inserted_id)
        return Sector(**sector_dict)

    async def get_by_id(self, sector_id: str) -> Optional[Sector]:
        doc = await self.collection.find_one({"_id": ObjectId(sector_id)})
        if doc:
            doc["_id"] = str(doc["_id"])
            return Sector(**doc)
        return None

    async def list_all(self) -> List[Sector]:
        cursor = self.collection.find()
        sectors = []
        async for doc in cursor:
            doc["_id"] = str(doc["_id"])
            sectors.append(Sector(**doc))
        return sectors

    async def update(self, sector_id: str, sector: SectorCreate) -> Optional[Sector]:
        sector_dict = sector.dict()
        sector_dict["updated_at"] = datetime.utcnow()
        
        result = await self.collection.update_one(
            {"_id": ObjectId(sector_id)},
            {"$set": sector_dict}
        )
        
        if result.matched_count:
            return await self.get_by_id(sector_id)
        return None

    async def delete(self, sector_id: str) -> bool:
        result = await self.collection.delete_one({"_id": ObjectId(sector_id)})
        return result.deleted_count > 0
