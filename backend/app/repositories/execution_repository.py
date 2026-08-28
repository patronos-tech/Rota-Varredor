from motor.motor_asyncio import AsyncIOMotorDatabase
from app.schemas.schemas import Execution, ExecutionCreate
from bson import ObjectId
from typing import List, Optional
from datetime import datetime

class ExecutionRepository:
    def __init__(self, db: AsyncIOMotorDatabase):
        self.collection = db["executions"]

    async def create(self, execution: ExecutionCreate) -> Execution:
        execution_dict = execution.dict()
        execution_dict["created_at"] = datetime.utcnow()
        execution_dict["updated_at"] = datetime.utcnow()
        execution_dict["status"] = "STARTED"
        execution_dict["segments"] = []
        execution_dict["pauses"] = []
        execution_dict["issues"] = []
        
        result = await self.collection.insert_one(execution_dict)
        execution_dict["_id"] = str(result.inserted_id)
        return execution_dict

    async def get_by_id(self, execution_id: str) -> Optional[dict]:
        doc = await self.collection.find_one({"_id": ObjectId(execution_id)})
        if doc:
            doc["_id"] = str(doc["_id"])
            return doc
        return None

    async def list_by_sector(self, sector_id: str) -> List[dict]:
        cursor = self.collection.find({"sector_id": sector_id})
        executions = []
        async for doc in cursor:
            doc["_id"] = str(doc["_id"])
            executions.append(doc)
        return executions

    async def update(self, execution_id: str, update_data: dict) -> Optional[dict]:
        update_data["updated_at"] = datetime.utcnow()
        
        result = await self.collection.update_one(
            {"_id": ObjectId(execution_id)},
            {"$set": update_data}
        )
        
        if result.matched_count:
            return await self.get_by_id(execution_id)
        return None
