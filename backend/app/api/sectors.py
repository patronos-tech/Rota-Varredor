from fastapi import APIRouter, Depends, status
from app.services import ServiceFactory
from app.schemas.schemas import SectorCreate, Sector
from app.schemas.response import success_response, error_response

router = APIRouter(prefix="/api/sectors", tags=["sectors"])

@router.get("/")
async def get_sectors():
    try:
        service = ServiceFactory.get_sector_service()
        sectors = await service.list_sectors()
        return success_response(sectors)
    except Exception as e:
        return error_response("INTERNAL_ERROR", str(e))

@router.get("/{sector_id}")
async def get_sector(sector_id: str):
    try:
        service = ServiceFactory.get_sector_service()
        sector = await service.get_sector(sector_id)
        return success_response(sector)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_sector(sector: SectorCreate):
    try:
        service = ServiceFactory.get_sector_service()
        created = await service.create_sector(sector)
        return success_response(created)
    except Exception as e:
        return error_response("INTERNAL_ERROR", str(e))

@router.put("/{sector_id}")
async def update_sector(sector_id: str, sector: SectorCreate):
    try:
        service = ServiceFactory.get_sector_service()
        updated = await service.update_sector(sector_id, sector)
        return success_response(updated)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.delete("/{sector_id}")
async def delete_sector(sector_id: str):
    try:
        service = ServiceFactory.get_sector_service()
        await service.delete_sector(sector_id)
        return success_response({"deleted": True})
    except Exception as e:
        return error_response("NOT_FOUND", str(e))
