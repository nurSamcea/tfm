from fastapi import APIRouter

router = APIRouter(prefix="/transactions", tags=["transactions"])

@router.get("/")
def get_transactions():
    return {"message": "List of transactions"}
