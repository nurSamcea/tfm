from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from backend.app import schemas, database, models

router = APIRouter(prefix="/shopping-list-items", tags=["Shopping List Items"])

@router.post("/", response_model=schemas.ShoppingListItemRead)
def create_item(item: schemas.ShoppingListItemCreate, db: Session = Depends(database.get_db)):
    db_item = models.ShoppingListItem(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.ShoppingListItemRead])
def get_all_items(db: Session = Depends(database.get_db)):
    return db.query(models.ShoppingListItem).all()

@router.get("/{item_id}", response_model=schemas.ShoppingListItemRead)
def get_item(item_id: int, db: Session = Depends(database.get_db)):
    item = db.query(models.ShoppingListItem).get(item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Item not found")
    return item

@router.put("/{item_id}", response_model=schemas.ShoppingListItemRead)
def update_item(item_id: int, new_data: schemas.ShoppingListItemCreate, db: Session = Depends(database.get_db)):
    item = db.query(models.ShoppingListItem).get(item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Item not found")
    for k, v in new_data.dict().items():
        setattr(item, k, v)
    db.commit()
    db.refresh(item)
    return item

@router.delete("/{item_id}")
def delete_item(item_id: int, db: Session = Depends(database.get_db)):
    item = db.query(models.ShoppingListItem).get(item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Item not found")
    db.delete(item)
    db.commit()
    return {"message": "Item deleted"}
