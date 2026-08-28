from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class Coordinates(BaseModel):
    latitude: float
    longitude: float

class SectorBase(BaseModel):
    name: str
    code: str
    pickup_point: Coordinates
    dropoff_point: Coordinates
    map_image: Optional[str] = None

class SectorCreate(SectorBase):
    pass

class Sector(SectorBase):
    id: str = Field(alias="_id")
    status: str = "READY"
    created_at: datetime
    updated_at: datetime
    streets: List[dict] = []

    class Config:
        populate_by_name = True

class StreetBase(BaseModel):
    name: str
    geometry: List[Coordinates]
    start_point: Coordinates
    end_point: Coordinates
    direction: float

class StreetCreate(StreetBase):
    sector_id: str

class Street(StreetBase):
    id: str = Field(alias="_id")
    sector_id: str
    status: str = "PENDING"
    suggested_order: int
    segments: List[dict] = []

    class Config:
        populate_by_name = True

class SegmentBase(BaseModel):
    start_point: Coordinates
    end_point: Coordinates
    geometry: List[Coordinates]
    direction: float

class SegmentCreate(SegmentBase):
    sector_id: str
    street_id: str

class Segment(SegmentBase):
    id: str = Field(alias="_id")
    sector_id: str
    street_id: str
    status: str = "PENDING"
    planned_order: int

    class Config:
        populate_by_name = True

class ExecutionBase(BaseModel):
    sector_id: str
    worker_id: str

class ExecutionCreate(ExecutionBase):
    pass

class Location(BaseModel):
    latitude: float
    longitude: float
    accuracy: float
    altitude: Optional[float] = None
    speed: Optional[float] = None
    heading: Optional[float] = None

class TrackLocationRequest(BaseModel):
    execution_id: str
    location: Location

class ExecutionMetrics(BaseModel):
    execution_id: str
    total_segments: int
    completed_segments: int
    completion_percentage: float
    planned_distance: float
    real_distance: float
    distance_efficiency: float
    planned_duration: float
    real_duration: float
    time_efficiency: float
    average_speed: float
    deviations: int
    pause_count: int
    total_pause_time: float
