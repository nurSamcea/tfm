import logging
import random
import uuid
from datetime import datetime, timedelta

import pandas as pd
from faker import Faker

# Configurar el logging para que muestre INFO en consola
logging.basicConfig(
    level=logging.INFO,  # Nivel mínimo de mensajes que se mostrarán
    format='%(asctime)s - %(levelname)s - %(message)s',
)

fake = Faker()

# 1. Intake profiles
intake_profiles = [
    {
        "id": 1,
        "name": "Adulto activo",
        "gender": "female",
        "age_range": "30-40",
        "activity": "active",
        "kcal": 2200,
        "protein": 50.0,
        "carbohydrates": 275.0,
        "sugars": 50.0,
        "fat": 70.0,
        "saturated_fat": 20.0,
        "fiber": 30.0,
        "salt": 5.0,
        "cholesterol": 300.0,
        "calcium": 1000.0,
        "iron": 18.0,
        "vitamin_c": 75.0,
        "vitamin_d": 15.0,
        "vitamin_b12": 2.4,
        "potassium": 4700.0,
        "magnesium": 310.0,
    },
    {
        "id": 2,
        "name": "Niño escolar",
        "gender": "male",
        "age_range": "6-12",
        "activity": "moderate",
        "kcal": 1600,
        "protein": 30.0,
        "carbohydrates": 200.0,
        "sugars": 35.0,
        "fat": 50.0,
        "saturated_fat": 15.0,
        "fiber": 25.0,
        "salt": 4.0,
        "cholesterol": 200.0,
        "calcium": 1300.0,
        "iron": 10.0,
        "vitamin_c": 45.0,
        "vitamin_d": 10.0,
        "vitamin_b12": 1.8,
        "potassium": 3000.0,
        "magnesium": 200.0,
    },
    {
        "id": 3,
        "name": "Deportista adulto",
        "gender": "male",
        "age_range": "20-35",
        "activity": "very_active",
        "kcal": 2800,
        "protein": 120.0,
        "carbohydrates": 350.0,
        "sugars": 60.0,
        "fat": 90.0,
        "saturated_fat": 25.0,
        "fiber": 35.0,
        "salt": 6.0,
        "cholesterol": 350.0,
        "calcium": 1200.0,
        "iron": 20.0,
        "vitamin_c": 90.0,
        "vitamin_d": 20.0,
        "vitamin_b12": 3.0,
        "potassium": 5000.0,
        "magnesium": 350.0,
    },
]

# 2. Users: 2 agricultores, 2 supermercados, 3 consumidores
roles = ["farmer", "farmer", "farmer", "farmer", "farmer", "farmer", "retailer", "retailer", "retailer", "retailer",
         "retailer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer",
         "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer",
         "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer", "consumer",
         "consumer", "consumer", "consumer", "consumer"]
users = []

for i, role in enumerate(roles, start=1):
    users.append({
        "id": i,
        "name": fake.name(),
        "email": fake.unique.email(),
        "password_hash": fake.sha256(),
        "role": role,
        "entity_name": fake.company() if role in ["farmer", "retailer"] else None,
        "location_lat": round(fake.latitude(), 6),
        "location_lon": round(fake.longitude(), 6),
        "preferences": {},
        "intake_profile_id": random.choice([1, 2, 3]) if role == "consumer" else None,
        "created_at": fake.date_time_this_year()
    })

# Datos nutricionales genéricos
generic_nutrition = {
    "tomate": {
        "kcal": 18, "protein": 0.9, "carbohydrates": 3.9, "sugars": 2.6,
        "fat": 0.2, "saturated_fat": 0.0, "fiber": 1.2, "salt": 0.01,
        "cholesterol": 0.0, "calcium": 10.0, "iron": 0.3, "vitamin_c": 13.7,
        "vitamin_d": 0.0, "vitamin_b12": 0.0, "potassium": 237.0, "magnesium": 11.0,
        "per_unit": "100g", "source": "USDA"
    },
    "zanahoria": {
        "kcal": 41, "protein": 0.9, "carbohydrates": 10.0, "sugars": 4.7,
        "fat": 0.2, "saturated_fat": 0.0, "fiber": 2.8, "salt": 0.07,
        "cholesterol": 0.0, "calcium": 33.0, "iron": 0.3, "vitamin_c": 5.9,
        "vitamin_d": 0.0, "vitamin_b12": 0.0, "potassium": 320.0, "magnesium": 12.0,
        "per_unit": "100g", "source": "USDA"
    },
    "manzana": {
        "kcal": 52, "protein": 0.3, "carbohydrates": 14.0, "sugars": 10.4,
        "fat": 0.2, "saturated_fat": 0.0, "fiber": 2.4, "salt": 0.01,
        "cholesterol": 0.0, "calcium": 6.0, "iron": 0.1, "vitamin_c": 4.6,
        "vitamin_d": 0.0, "vitamin_b12": 0.0, "potassium": 107.0, "magnesium": 5.0,
        "per_unit": "100g", "source": "USDA"
    },
    "pan integral": {
        "kcal": 247, "protein": 8.8, "carbohydrates": 41.0, "sugars": 5.0,
        "fat": 3.4, "saturated_fat": 0.5, "fiber": 7.0, "salt": 0.45,
        "cholesterol": 0.0, "calcium": 107.0, "iron": 2.5, "vitamin_c": 0.0,
        "vitamin_d": 0.0, "vitamin_b12": 0.0, "potassium": 230.0, "magnesium": 70.0,
        "per_unit": "100g", "source": "Tabla Española"
    },
    "leche vegetal": {
        "kcal": 46, "protein": 1.0, "carbohydrates": 6.0, "sugars": 2.9,
        "fat": 1.5, "saturated_fat": 0.3, "fiber": 0.4, "salt": 0.13,
        "cholesterol": 0.0, "calcium": 120.0, "iron": 0.3, "vitamin_c": 0.0,
        "vitamin_d": 1.0, "vitamin_b12": 0.5, "potassium": 150.0, "magnesium": 15.0,
        "per_unit": "100ml", "source": "Envase fabricante"
    },
    "tofu": {
        "kcal": 76, "protein": 8.0, "carbohydrates": 1.9, "sugars": 0.3,
        "fat": 4.8, "saturated_fat": 0.7, "fiber": 0.3, "salt": 0.01,
        "cholesterol": 0.0, "calcium": 350.0, "iron": 5.4, "vitamin_c": 0.0,
        "vitamin_d": 0.0, "vitamin_b12": 0.0, "potassium": 121.0, "magnesium": 30.0,
        "per_unit": "100g", "source": "USDA"
    }
}

# Creamos un DataFrame con la info nutricional
nutritional_info_df = pd.DataFrame([
    {"generic_name": name, **values}
    for name, values in generic_nutrition.items()
])
# Generar lista coherente con la tabla nutritional_info
nutritional_info = []

for i, row in nutritional_info_df.iterrows():
    record = {
        "id": i + 1,
        "generic_name": row["generic_name"],
        "kcal": row["kcal"],
        "protein": row["protein"],
        "carbohydrates": row["carbohydrates"],
        "sugars": row["sugars"],
        "fat": row["fat"],
        "saturated_fat": row["saturated_fat"],
        "fiber": row["fiber"],
        "salt": row["salt"],
        "cholesterol": row["cholesterol"],
        "calcium": row["calcium"],
        "iron": row["iron"],
        "vitamin_c": row["vitamin_c"],
        "vitamin_d": row["vitamin_d"],
        "vitamin_b12": row["vitamin_b12"],
        "potassium": row["potassium"],
        "magnesium": row["magnesium"],
        "per_unit": row["per_unit"],
        "source": row["source"]
    }
    nutritional_info.append(record)


# Simulación de roles y proveedores
roles = ["consumer", "farmer", "farmer", "retailer", "retailer"]
provider_map = {
    "farmer": [i + 1 for i, r in enumerate(roles) if r == "farmer"],
    "retailer": [i + 1 for i, r in enumerate(roles) if r == "retailer"]
}

# Productos concretos asociados a proveedores
product_data = [
    ("Tomates Ecológicos", "tomate", "kg", "vegetales", True),
    ("Zanahorias", "zanahoria", "kg", "vegetales", True),
    ("Manzanas", "manzana", "kg", "frutas", True),
    ("Pan integral", "pan integral", "unidad", "panadería", False),
    ("Leche vegetal", "leche vegetal", "litro", "bebidas", False),
    ("Tofu orgánico", "tofu", "unidad", "proteínas", True)
]

products = []
for i, (name, generic, unit, category, is_eco) in enumerate(product_data, start=1):
    role = "farmer" if i <= 3 else "retailer"
    provider_id = random.choice(provider_map[role])
    product = {
        "id": i,
        "name": name,
        "generic_name": generic,
        "description": f"{name} de alta calidad",
        "price": round(random.uniform(1.0, 5.0), 2),
        "currency": "EUR",
        "unit": unit,
        "category": category,
        "nutritional_info": None,
        "stock_available": round(random.uniform(50, 200), 1),
        "expiration_date": datetime.now() + timedelta(days=random.randint(5, 20)),
        "is_eco": is_eco,
        "image_url": f"https://dummyimage.com/600x400/000/fff&text={name.replace(' ', '+')}",
        "provider_id": provider_id,
        "created_at": datetime.now()
    }
    products.append(product)

# Crear listas de compra
shopping_lists = []
shopping_list_groups = []
shopping_list_items = []
group_id_counter = 1
item_id_counter = 1

for i in range(1, 4):  # 3 listas de compra
    consumer = random.choice([u for u in users if u["role"] == "consumer"])
    list_id = i
    created_at = datetime.now() - timedelta(days=random.randint(1, 5))
    shopping_lists.append({
        "id": list_id,
        "user_id": consumer["id"],
        "created_at": created_at,
        "total_price": 0.0,
        "currency": "EUR",
        "status": "pending"
    })

    # Seleccionamos entre 1 y 2 proveedores para esta lista
    selected_products = random.sample(products, k=random.randint(2, 4))
    grouped_by_provider = {}
    for p in selected_products:
        grouped_by_provider.setdefault(p["provider_id"], []).append(p)

    for provider_id, product_group in grouped_by_provider.items():
        group_id = group_id_counter
        group_id_counter += 1
        delivery_estimate = created_at + timedelta(days=1)
        logistics_route_id = None

        shopping_list_groups.append({
            "id": group_id,
            "shopping_list_id": list_id,
            "provider_id": provider_id,
            "subtotal_price": 0.0,
            "delivery_estimate": delivery_estimate,
            "logistics_route_id": logistics_route_id,
            "delivery_window": {"start": "10:00", "end": "12:00"},
            "delivery_status": "not_delivered",
            "logistics_stop_order": None
        })

        for p in product_group:
            quantity = random.uniform(1, 5)
            price_unit = round(random.uniform(1.0, 3.0), 2)
            total_price = round(quantity * price_unit, 2)
            shopping_list_items.append({
                "id": item_id_counter,
                "shopping_list_group_id": group_id,
                "product_id": p["id"],
                "quantity": round(quantity, 2),
                "unit": "kg",
                "price_unit": price_unit,
                "currency": "EUR",
                "total_price": total_price,
                "expiration_date": datetime.now() + timedelta(days=10),
                "trace_hash": uuid.uuid4().hex,
                "nutritional_info": {},
                "added_at": datetime.now()
            })
            item_id_counter += 1

# Actualizamos el total_price de listas y grupos
for sl in shopping_lists:
    sl_groups = [g for g in shopping_list_groups if g["shopping_list_id"] == sl["id"]]
    sl["total_price"] = round(sum(
        sum(i["total_price"] for i in shopping_list_items if i["shopping_list_group_id"] == g["id"])
        for g in sl_groups
    ), 2)

for g in shopping_list_groups:
    g["subtotal_price"] = round(sum(
        i["total_price"] for i in shopping_list_items if i["shopping_list_group_id"] == g["id"]
    ), 2)

from datetime import datetime, timedelta
import random

# Datos base ya conocidos
user_ids = [1, 2, 3]  # consumers
shopping_list_ids = [1, 2, 3]
recipe_ids = [1, 2, 3]
product_ids = list(range(1, 7))  # productos creados antes

# -------------------------------
# Tabla: transactions
# -------------------------------
transactions = []
for i, user_id in enumerate(user_ids, start=1):
    shopping_list_id = shopping_list_ids[i - 1]
    total_price = round(random.uniform(10, 50), 2)
    transaction = {
        "id": i,
        "user_id": user_id,
        "shopping_list_id": shopping_list_id,
        "total_price": total_price,
        "currency": "EUR",
        "status": random.choice(["completed", "pending", "cancelled"]),
        "created_at": datetime.now() - timedelta(days=random.randint(1, 10)),
        "confirmed_at": datetime.now() if random.random() > 0.2 else None
    }
    transactions.append(transaction)

# -------------------------------
# Tabla: recipes y recipe_ingredients
# -------------------------------
recipes = []
recipe_ingredients = []
for i in range(1, 4):
    recipe = {
        "id": i,
        "name": f"Receta Saludable {i}",
        "description": f"Descripción de la receta {i}",
        "author_id": random.choice(user_ids),
        "image_url": f"https://dummyimage.com/600x400/000/fff&text=Receta+{i}",
        "steps": [{"step": 1, "instruction": "Mezclar los ingredientes"},
                  {"step": 2, "instruction": "Cocinar por 20 min"}],
        "time_minutes": random.randint(10, 45),
        "difficulty": random.choice(["fácil", "media", "difícil"]),
        "is_vegan": bool(random.getrandbits(1)),
        "is_gluten_free": bool(random.getrandbits(1)),
        "created_at": datetime.now(),
        "tags": ["saludable", "rápido"],
        "nutrition_total": {}
    }
    recipes.append(recipe)

    used_products = random.sample(product_ids, k=2)
    for pid in used_products:
        ingredient = {
            "id": len(recipe_ingredients) + 1,
            "recipe_id": i,
            "product_id": pid,
            "name": f"Ingrediente {pid}",
            "quantity": round(random.uniform(0.1, 1.0), 2),
            "unit": "kg",
            "nutritional_info": {},
            "optional": bool(random.getrandbits(1))
        }
        recipe_ingredients.append(ingredient)

# -------------------------------
# Tabla: weekly_plans y weekly_plan_items
# -------------------------------
weekly_plans = []
weekly_plan_items = []
days = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
meals = ["Desayuno", "Comida", "Cena"]

for i, user_id in enumerate(user_ids, start=1):
    weekly_plan = {
        "id": i,
        "user_id": user_id,
        "intake_profile_id": i,
        "week_start": datetime.now().date() - timedelta(days=datetime.now().weekday()),
        "created_at": datetime.now(),
        "comment": f"Plan semanal {i}"
    }
    weekly_plans.append(weekly_plan)

    for day in days:
        for meal in meals:
            item = {
                "id": len(weekly_plan_items) + 1,
                "weekly_plan_id": i,
                "day_of_week": day,
                "meal_type": meal,
                "recipe_id": random.choice(recipe_ids),
                "portions": random.randint(1, 4),
                "nutrition_total": {}
            }
            weekly_plan_items.append(item)

# ----------------------------
# DRIVERS
# ----------------------------
vehicle_types = ['bike', 'van', 'truck']
vehicles = []
drivers = []

for i in range(5):
    vehicle = {
        "id": i + 1,
        "type": random.choice(vehicle_types),
        "name": f"Vehículo {i + 1}",
        "plate_number": f"{fake.random_uppercase_letter()}{random.randint(1000, 9999)}{fake.random_uppercase_letter()}",
        "capacity_kg": random.uniform(100, 1000),
        "capacity_m3": random.uniform(1, 10),
        "speed_kmph": random.uniform(30, 90),
        "emissions_factor": round(random.uniform(0.1, 0.5), 2),
        "created_at": datetime.now()
    }
    vehicles.append(vehicle)

    driver = {
        "id": i + 1,
        "name": fake.name(),
        "phone": fake.phone_number(),
        "email": fake.email(),
        "license_number": f"{random.randint(100000, 999999)}",
        "vehicle_id": vehicle["id"],
        "location_lat": fake.latitude(),
        "location_lon": fake.longitude(),
        "working_hours": {"start": "08:00", "end": "18:00"},
        "available": True,
        "created_at": datetime.now()
    }
    drivers.append(driver)

# ----------------------------
# LOGISTICS ROUTES & STOPS
# ----------------------------
logistics_routes = []
logistics_route_stops = []
route_id = 1
stop_id = 1
group_ids = list(range(1, 7))

for g_id in group_ids:
    driver = random.choice(drivers)
    vehicle = next(v for v in vehicles if v["id"] == driver["vehicle_id"])

    route = {
        "id": route_id,
        "driver_name": driver["name"],
        "distance_km": round(random.uniform(2, 20), 2),
        "orders_ids": [g_id],
        "created_at": datetime.now(),
        "vehicle_type": vehicle["type"],
        "estimated_time_min": random.randint(10, 60),
        "driver_id": driver["id"],
        "vehicle_id": vehicle["id"],
        "date": datetime.now().date(),
        "status": "pending"
    }
    logistics_routes.append(route)

    # Añadimos dos paradas: pickup y delivery
    for stop_type in ["pickup", "delivery"]:
        stop = {
            "id": stop_id,
            "logistics_route_id": route_id,
            "stop_order": 1 if stop_type == "pickup" else 2,
            "stop_type": stop_type,
            "related_group_id": g_id,
            "sender_id": random.randint(1, 5),  # agricultor/retailer
            "receiver_id": random.randint(6, 10),  # consumidor
            "location_lat": fake.latitude(),
            "location_lon": fake.longitude(),
            "eta": datetime.now() + timedelta(minutes=random.randint(10, 90)),
            "status": "pending"
        }
        logistics_route_stops.append(stop)
        stop_id += 1
    route_id += 1

# ----------------------------
# SENSOR READINGS
# ----------------------------
sensor_readings = []
for pid in range(1, 11):
    for _ in range(3):  # 3 lecturas por producto
        reading = {
            "product_id": pid,
            "temperature": round(random.uniform(1, 10), 1),
            "humidity": round(random.uniform(30, 70), 1),
            "gas_level": round(random.uniform(0, 1), 2),
            "light_level": round(random.uniform(100, 500), 1),
            "shock_detected": random.choice([True, False]),
            "created_at": datetime.now(),
            "source_device": f"sensor_{random.randint(1, 5)}"
        }
        sensor_readings.append(reading)

# ----------------------------
# QRs
# ----------------------------
qrs = []
for pid in range(1, 11):
    qr = {
        "product_id": pid,
        "qr_hash": fake.sha256(),
        "created_at": datetime.now(),
        "metadata": {"trace_id": fake.uuid4()}
    }
    qrs.append(qr)

# ----------------------------
# BLOCKCHAIN LOGS
# ----------------------------
blockchain_logs = []
for eid in range(1, 11):
    log = {
        "entity_type": random.choice(["product", "transaction", "sensor"]),
        "entity_id": eid,
        "hash": fake.sha256(),
        "timestamp": datetime.now()
    }
    blockchain_logs.append(log)

# ----------------------------
# IMPACT METRICS
# ----------------------------
impact_metrics = []
for uid in range(1, 11):
    metric = {
        "user_id": uid,
        "co2_saved_kg": round(random.uniform(1.0, 15.0), 2),
        "local_support_eur": round(random.uniform(10.0, 200.0), 2),
        "waste_prevented_kg": round(random.uniform(0.5, 5.0), 2)
    }
    impact_metrics.append(metric)



dict_final = {
    "intake_profiles": intake_profiles,
    "users": users,
    "products": products,
    "nutritional_info": nutritional_info,
    "shopping_lists": shopping_lists,
    "shopping_list_groups": shopping_list_groups,
    "shopping_list_items": shopping_list_items,
    "transactions": transactions,
    "recipes": recipes,
    "recipe_ingredients": recipe_ingredients,
    "weekly_plans": weekly_plans,
    "weekly_plan_items": weekly_plan_items,
    "vehicles": vehicles,
    "drivers": drivers,
    "logistics_routes": logistics_routes,
    "logistics_route_stops": logistics_route_stops,
    "sensor_readings": sensor_readings,
    "qrs": qrs,
    "blockchain_logs": blockchain_logs,
    "impact_metrics": impact_metrics
}
logging.info(dict_final)