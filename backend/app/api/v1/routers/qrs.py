from fastapi import APIRouter

router = APIRouter(prefix="/qrs", tags=["qrs"])

@router.get("/")
def get_qrs():
    return {"message": "QR codes"}
