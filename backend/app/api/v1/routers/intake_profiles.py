from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from backend.app import models, schemas, database

router = APIRouter(prefix="/intake-profiles", tags=["Intake Profiles"])

@router.post("/", response_model=schemas.IntakeProfileRead)
def create_intake_profile(profile: schemas.IntakeProfileCreate, db: Session = Depends(database.get_db)):
    db_profile = models.IntakeProfile(**profile.dict())
    db.add(db_profile)
    db.commit()
    db.refresh(db_profile)
    return db_profile

@router.get("/", response_model=list[schemas.IntakeProfileRead])
def get_all_profiles(db: Session = Depends(database.get_db)):
    return db.query(models.IntakeProfile).all()

@router.get("/{profile_id}", response_model=schemas.IntakeProfileRead)
def get_profile(profile_id: int, db: Session = Depends(database.get_db)):
    profile = db.query(models.IntakeProfile).get(profile_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    return profile

@router.put("/{profile_id}", response_model=schemas.IntakeProfileRead)
def update_profile(profile_id: int, new_data: schemas.IntakeProfileCreate, db: Session = Depends(database.get_db)):
    profile = db.query(models.IntakeProfile).get(profile_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    for field, value in new_data.dict().items():
        setattr(profile, field, value)
    db.commit()
    db.refresh(profile)
    return profile

@router.delete("/{profile_id}")
def delete_profile(profile_id: int, db: Session = Depends(database.get_db)):
    profile = db.query(models.IntakeProfile).get(profile_id)
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    db.delete(profile)
    db.commit()
    return {"message": "Profile deleted"}
