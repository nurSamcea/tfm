# main.py

from fastapi import FastAPI
from backend.app.api.v1.routers import (
    auth_router,
    users_router,
    products_router,
    shopping_router,
    transactions_router,
    recipes_router,
    planning_router,
    qrs_router,
    sensors_router,
    blockchain_router,
    impact_router,
    logistics_router,
    recommendations_router,
)

app = FastAPI(title="Zero Platform API", version="1.0.0")

# Register routers
app.include_router(auth_router)
app.include_router(users_router)
app.include_router(products_router)
app.include_router(shopping_router)
app.include_router(transactions_router)
app.include_router(recipes_router)
app.include_router(planning_router)
app.include_router(qrs_router)
app.include_router(sensors_router)
app.include_router(blockchain_router)
app.include_router(impact_router)
app.include_router(logistics_router)
app.include_router(recommendations_router)


@app.get("/")
def root():
    return {"message": "Welcome to the Zero Platform API 🚀"}
