from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from backend.app import schemas, models, database

router = APIRouter(prefix="/shopping-lists", tags=["Shopping Lists"])

@router.post("/", response_model=schemas.ShoppingListRead)
def create_shopping_list(list_data: schemas.ShoppingListCreate, db: Session = Depends(database.get_db)):
    db_list = models.ShoppingList(**list_data.dict())
    db.add(db_list)
    db.commit()
    db.refresh(db_list)
    return db_list

@router.get("/", response_model=list[schemas.ShoppingListRead])
def get_all_lists(db: Session = Depends(database.get_db)):
    return db.query(models.ShoppingList).all()

@router.get("/{list_id}", response_model=schemas.ShoppingListRead)
def get_list(list_id: int, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    return db_list

@router.put("/{list_id}", response_model=schemas.ShoppingListRead)
def update_list(list_id: int, new_data: schemas.ShoppingListCreate, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    for k, v in new_data.dict().items():
        setattr(db_list, k, v)
    db.commit()
    db.refresh(db_list)
    return db_list

@router.delete("/{list_id}")
def delete_list(list_id: int, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    db.delete(db_list)
    db.commit()
    return {"message": "Shopping list deleted"}
