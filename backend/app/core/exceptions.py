from fastapi import HTTPException, status

class AppException(HTTPException):
    def __init__(self, code: str, message: str, status_code: int, details: dict = None):
        self.code = code
        self.message = message
        self.details = details
        super().__init__(status_code=status_code, detail=message)

class NotFoundException(AppException):
    def __init__(self, resource: str, resource_id: str):
        super().__init__(
            code="NOT_FOUND",
            message=f"{resource} with id {resource_id} not found",
            status_code=status.HTTP_404_NOT_FOUND,
            details={"resource": resource, "id": resource_id}
        )

class ValidationException(AppException):
    def __init__(self, message: str, details: dict = None):
        super().__init__(
            code="VALIDATION_ERROR",
            message=message,
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            details=details
        )

class UnauthorizedException(AppException):
    def __init__(self):
        super().__init__(
            code="UNAUTHORIZED",
            message="Unauthorized access",
            status_code=status.HTTP_401_UNAUTHORIZED
        )

class ForbiddenException(AppException):
    def __init__(self):
        super().__init__(
            code="FORBIDDEN",
            message="Access forbidden",
            status_code=status.HTTP_403_FORBIDDEN
        )
