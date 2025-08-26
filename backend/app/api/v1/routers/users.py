from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from backend.app import models, schemas, database
from backend.app.core.security import get_password_hash

router = APIRouter(prefix="/users", tags=["User"]) 


@router.post("/", response_model=schemas.UserRead, status_code=status.HTTP_201_CREATED)
def create_user(item: schemas.UserCreate, db: Session = Depends(database.get_db)):
    existing = db.query(models.User).filter(models.User.email == item.email).first()
    if existing:
        raise HTTPException(status_code=400, detail="Email already in use")
    db_item = models.User(
        name=item.name,
        email=item.email,
        password_hash=get_password_hash(item.password),
        role=item.role,
        entity_name=item.entity_name,
        location_lat=item.location_lat,
        location_lon=item.location_lon,
        preferences=item.preferences,
    )
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item


@router.get("/", response_model=list[schemas.UserRead])
def list_users(db: Session = Depends(database.get_db)):
    return db.query(models.User).all()


@router.get("/{user_id}", response_model=schemas.UserRead)
def get_user(user_id: int, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(user_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")
    return db_item


@router.put("/{user_id}", response_model=schemas.UserRead)
def update_user(user_id: int, item: schemas.UserUpdate, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(user_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")

    payload = item.dict(exclude_unset=True)
    if "password" in payload and payload["password"]:
        db_item.password_hash = get_password_hash(payload.pop("password"))
    for key, value in payload.items():
        setattr(db_item, key, value)
    db.commit()
    db.refresh(db_item)
    return db_item


@router.delete("/{user_id}")
def delete_user(user_id: int, db: Session = Depends(database.get_db)):
    db_item = db.query(models.User).get(user_id)
    if not db_item:
        raise HTTPException(status_code=404, detail="User not found")
    db.delete(db_item)
    db.commit()
    return {"message": "User deleted"}
