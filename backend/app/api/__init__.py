from fastapi import APIRouter

from app.api import sectors, executions

api_router = APIRouter()
api_router.include_router(sectors.router)
api_router.include_router(executions.router)
