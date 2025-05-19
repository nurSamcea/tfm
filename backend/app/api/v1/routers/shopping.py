from fastapi import APIRouter

router = APIRouter(prefix="/shopping", tags=["shopping"])

@router.get("/lists")
def get_shopping_lists():
    return {"message": "Shopping lists"}
