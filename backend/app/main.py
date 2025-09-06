from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
import os
from fastapi.middleware.cors import CORSMiddleware

from backend.app.api.v1.routers import (
    auth, users, products, shopping_lists, shopping_list_groups,
    shopping_list_items, transactions, sensor_readings,
    qrs, blockchain_logs, impact_metrics, traceability
)

app = FastAPI(title="Zero Platform API", version="1.0.0")

# Configurar CORS para permitir conexiones desde la aplicación Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especificar las IPs específicas
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Registrar rutas
app.include_router(auth.router)
app.include_router(users.router)
app.include_router(products.router)
app.include_router(shopping_lists.router)
app.include_router(shopping_list_groups.router)
app.include_router(shopping_list_items.router)
app.include_router(transactions.router)
app.include_router(sensor_readings.router)
app.include_router(qrs.router)
app.include_router(blockchain_logs.router)
app.include_router(impact_metrics.router)
app.include_router(traceability.router)


@app.get("/")
def root():
    return {"message": "Welcome to the Zero Platform API 🚀"}

# Montar archivos estáticos para servir imágenes subidas
# Asegurar que el directorio existe para evitar errores en el arranque
os.makedirs("media", exist_ok=True)
app.mount("/media", StaticFiles(directory="media"), name="media")
