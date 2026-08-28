from typing import Optional, Generic, TypeVar, List
from pydantic import BaseModel
from datetime import datetime

T = TypeVar('T')

class ApiError(BaseModel):
    code: str
    message: str
    details: Optional[dict] = None

class ApiResponse(BaseModel, Generic[T]):
    success: bool
    data: Optional[T] = None
    error: Optional[ApiError] = None
    timestamp: datetime = datetime.utcnow()

    class Config:
        arbitrary_types_allowed = True

def success_response(data: T, message: str = "Success") -> ApiResponse[T]:
    return ApiResponse(
        success=True,
        data=data,
        timestamp=datetime.utcnow()
    )

def error_response(code: str, message: str, details: Optional[dict] = None) -> ApiResponse:
    return ApiResponse(
        success=False,
        error=ApiError(code=code, message=message, details=details),
        timestamp=datetime.utcnow()
    )
