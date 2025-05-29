from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from backend.app import models, schemas, database

router = APIRouter(prefix="/users", tags=["User"])


@router.post("/", response_model=schemas.UserRead)
def create_item(item: schemas.UserCreate, db: Session = Depends(database.get_db)):
    db_item = models.User(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item


@router.get("/", response_model=list[schemas.UserRead])
def read_all_items(db: Session = Depends(database.get_db)):
    return db.query(models.User).all()


@router.get("/{item_id}", response_model=schemas.UserRead)
def read_item(item_id: int, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(item_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")
    return db_item


@router.put("/{item_id}", response_model=schemas.UserRead)
def update_item(item_id: int, item: schemas.UserCreate, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(item_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")
    for key, value in item.dict().items():
        setattr(db_item, key, value)
    db.commit()
    db.refresh(db_item)
    return db_item


@router.delete("/{item_id}")
def delete_item(item_id: int, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(item_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")
    db.delete(db_item)
    db.commit()
    return {"message": "User deleted"}
