> alembic upgrade head
> python .\database\railway_init.py delete create update status


> python -m uvicorn backend.app.main:app --reload --host 0.0.0.0 --port 8000 

Adaptador de LAN inalámbrica Wi-Fi 2 -> 

- Dirección IPv4
modificar el .env
BACKEND_IP=172.20.4.105


# Ver el estado actual de las migraciones
alembic current

# Aplicar todas las migraciones pendientes
alembic upgrade head

# Ahora crear la nueva migración para las categorías
alembic revision --autogenerate -m "update_product_category_to_enum"

# Aplicar la nueva migración
alembic upgrade head


   # Modo test (6 sensores)
   python iot/start_simulator.py --mode test
   
   # Modo completo (100 sensores)
   python iot/start_simulator.py --mode full