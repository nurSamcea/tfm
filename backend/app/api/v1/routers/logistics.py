from fastapi import APIRouter

router = APIRouter(prefix="/logistics", tags=["logistics"])

@router.get("/routes")
def get_routes():
    return {"message": "Logistics routes"}
