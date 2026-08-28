from fastapi import APIRouter, status
from app.services import ServiceFactory
from app.schemas.schemas import ExecutionCreate, TrackLocationRequest
from app.schemas.response import success_response, error_response

router = APIRouter(prefix="/api/executions", tags=["executions"])

@router.post("/start", status_code=status.HTTP_201_CREATED)
async def start_execution(execution: ExecutionCreate):
    try:
        service = ServiceFactory.get_execution_service()
        created = await service.start_execution(execution)
        return success_response(created)
    except Exception as e:
        return error_response("INTERNAL_ERROR", str(e))

@router.get("/{execution_id}")
async def get_execution(execution_id: str):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.get_execution(execution_id)
        return success_response(execution)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.post("/{execution_id}/pause")
async def pause_execution(execution_id: str):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.pause_execution(execution_id)
        return success_response(execution)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.post("/{execution_id}/resume")
async def resume_execution(execution_id: str):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.resume_execution(execution_id)
        return success_response(execution)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.post("/{execution_id}/complete")
async def complete_execution(execution_id: str):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.complete_execution(execution_id)
        return success_response(execution)
    except Exception as e:
        return error_response("NOT_FOUND", str(e))

@router.post("/segments/complete")
async def complete_segment(execution_id: str, segment_id: str):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.complete_segment(execution_id, segment_id)
        return success_response(execution)
    except Exception as e:
        return error_response("INTERNAL_ERROR", str(e))

@router.post("/track-location")
async def track_location(request: TrackLocationRequest):
    try:
        service = ServiceFactory.get_execution_service()
        execution = await service.get_execution(request.execution_id)
        # Location tracking logic here
        return success_response({"tracked": True})
    except Exception as e:
        return error_response("INTERNAL_ERROR", str(e))
