from fastapi import APIRouter

router = APIRouter(prefix="/impact", tags=["impact"])

@router.get("/metrics")
def get_impact_metrics():
    return {"message": "Impact metrics"}
