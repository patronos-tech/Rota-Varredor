from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from app.core.config import settings
from app.core.logging import logger

class MongoDB:
    client: AsyncIOMotorClient = None
    db: AsyncIOMotorDatabase = None

db = MongoDB()

async def connect_db():
    try:
        db.client = AsyncIOMotorClient(settings.MONGODB_URL)
        db.db = db.client[settings.MONGODB_DATABASE]
        logger.info(f"Connected to MongoDB: {settings.MONGODB_DATABASE}")
    except Exception as e:
        logger.error(f"Failed to connect to MongoDB: {e}")
        raise

async def close_db():
    try:
        if db.client:
            db.client.close()
            logger.info("Closed MongoDB connection")
    except Exception as e:
        logger.error(f"Failed to close MongoDB connection: {e}")
        raise
