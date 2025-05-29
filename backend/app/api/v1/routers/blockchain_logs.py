from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models

from backend.app import schemas
from backend.app.database import get_db

router = APIRouter(prefix="/blockchain_logs", tags=["Blockchain Logs"])

@router.post("/", response_model=schemas.BlockchainLogOut)
def create_blockchain_log(item: schemas.BlockchainLogCreate, db: Session = Depends(get_db)):
    db_item = models.BlockchainLog(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.BlockchainLogOut])
def read_blockchain_logs(db: Session = Depends(get_db)):
    return db.query(models.BlockchainLog).all()
