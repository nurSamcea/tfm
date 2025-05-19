from fastapi import APIRouter

router = APIRouter(prefix="/planning", tags=["planning"])

@router.get("/weekly")
def get_weekly_plan():
    return {"message": "Weekly plan"}
