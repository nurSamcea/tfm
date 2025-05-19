from fastapi import APIRouter

router = APIRouter(prefix="/blockchain", tags=["blockchain"])

@router.get("/logs")
def get_blockchain_logs():
    return {"message": "Blockchain logs"}
