from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from backend.app import models, schemas, database

router = APIRouter(prefix="/shopping-list-groups", tags=["Shopping List Groups"])

@router.post("/", response_model=schemas.ShoppingListGroupRead)
def create_group(group: schemas.ShoppingListGroupCreate, db: Session = Depends(database.get_db)):
    db_group = models.ShoppingListGroup(**group.dict())
    db.add(db_group)
    db.commit()
    db.refresh(db_group)
    return db_group

@router.get("/", response_model=list[schemas.ShoppingListGroupRead])
def get_all_groups(db: Session = Depends(database.get_db)):
    return db.query(models.ShoppingListGroup).all()

@router.get("/{group_id}", response_model=schemas.ShoppingListGroupRead)
def get_group(group_id: int, db: Session = Depends(database.get_db)):
    group = db.query(models.ShoppingListGroup).get(group_id)
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")
    return group

@router.put("/{group_id}", response_model=schemas.ShoppingListGroupRead)
def update_group(group_id: int, new_data: schemas.ShoppingListGroupCreate, db: Session = Depends(database.get_db)):
    group = db.query(models.ShoppingListGroup).get(group_id)
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")
    for k, v in new_data.dict().items():
        setattr(group, k, v)
    db.commit()
    db.refresh(group)
    return group

@router.delete("/{group_id}")
def delete_group(group_id: int, db: Session = Depends(database.get_db)):
    group = db.query(models.ShoppingListGroup).get(group_id)
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")
    db.delete(group)
    db.commit()
    return {"message": "Group deleted"}
