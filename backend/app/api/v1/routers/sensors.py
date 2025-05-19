from fastapi import APIRouter

router = APIRouter(prefix="/sensors", tags=["sensors"])

@router.get("/readings")
def get_sensor_readings():
    return {"message": "Sensor readings"}
