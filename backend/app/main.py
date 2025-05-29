from fastapi import FastAPI

from backend.app.api.v1.routers import (
    users, intake_profiles, products, shopping_lists, shopping_list_groups,
    shopping_list_items, recipes, recipe_ingredients, weekly_plans,
    weekly_plan_items, transactions, logistics_routes, sensor_readings,
    qrs, blockchain_logs, impact_metrics
)

app = FastAPI(title="Zero Platform API", version="1.0.0")

# Registrar rutas
app.include_router(users.router)
app.include_router(intake_profiles.router)
app.include_router(products.router)
app.include_router(shopping_lists.router)
app.include_router(shopping_list_groups.router)
app.include_router(shopping_list_items.router)
app.include_router(recipes.router)
app.include_router(recipe_ingredients.router)
app.include_router(weekly_plans.router)
app.include_router(weekly_plan_items.router)
app.include_router(transactions.router)
app.include_router(logistics_routes.router)
app.include_router(sensor_readings.router)
app.include_router(qrs.router)
app.include_router(blockchain_logs.router)
app.include_router(impact_metrics.router)


@app.get("/")
def root():
    return {"message": "Welcome to the Zero Platform API 🚀"}
