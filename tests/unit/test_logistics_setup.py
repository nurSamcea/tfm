import os
import random
from datetime import datetime, timedelta

from faker import Faker
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from backend.app.models import User, Product, ShoppingList, ShoppingListGroup, ShoppingListItem, Driver, Vehicle

os.environ["ENVIRONMENT"] = "test"
os.environ["DATABASE_URL"] = "sqlite:///./test.db"
os.environ["SECRET_KEY"] = "test"
os.environ["DEBUG"] = "True"
os.environ["ALGORITHM"] = "HS256"
os.environ["ACCESS_TOKEN_EXPIRE_MINUTES"] = "30"



# Configuración de base de datos (ajustar según tu entorno)
DATABASE_URL = "postgresql://postgres:sspBvGuWGQrNsdBTiHOnusZuLjtAFLVY@trolley.proxy.rlwy.net:55739/railway"
engine = create_engine(DATABASE_URL)
Session = sessionmaker(bind=engine)
session = Session()

fake = Faker()


# Crear datos simulados
def crear_usuarios_y_productos(n_consumidores=20, n_proveedores=10, n_productos=50):
    consumidores = []
    proveedores = []

    for _ in range(n_consumidores):
        user = User(
            name=fake.name(),
            email=fake.unique.email(),
            password_hash="hashed_password",
            role="consumer",
            location_lat=round(random.uniform(40.4, 40.5), 6),
            location_lon=round(random.uniform(-3.7, -3.6), 6)
        )
        session.add(user)
        consumidores.append(user)

    for _ in range(n_proveedores):
        user = User(
            name=fake.company(),
            email=fake.unique.email(),
            password_hash="hashed_password",
            role=random.choice(["farmer", "retailer"]),
            location_lat=round(random.uniform(40.4, 40.5), 6),
            location_lon=round(random.uniform(-3.7, -3.6), 6)
        )
        session.add(user)
        proveedores.append(user)

    session.commit()

    productos = []
    for _ in range(n_productos):
        proveedor = random.choice(proveedores)
        producto = Product(
            name=fake.word(),
            description=fake.text(max_nb_chars=50),
            price=round(random.uniform(1, 20), 2),
            currency="EUR",
            unit="kg",
            category="fruta",
            nutritional_info={},
            stock_available=round(random.uniform(10, 100), 2),
            is_eco=random.choice([True, False]),
            provider_id=proveedor.id
        )
        session.add(producto)
        productos.append(producto)

    session.commit()
    return consumidores, proveedores, productos


def crear_listas_de_compra(consumidores, productos):
    for consumidor in consumidores:
        sl = ShoppingList(
            user_id=consumidor.id,
            total_price=0,
            currency="EUR",
            status="pending"
        )
        session.add(sl)
        session.commit()

        grupos = {}
        for _ in range(random.randint(2, 4)):
            producto = random.choice(productos)
            if producto.provider_id not in grupos:
                grupo = ShoppingListGroup(
                    shopping_list_id=sl.id,
                    provider_id=producto.provider_id,
                    subtotal_price=0
                )
                session.add(grupo)
                session.commit()
                grupos[producto.provider_id] = grupo

            item = ShoppingListItem(
                shopping_list_group_id=grupos[producto.provider_id].id,
                product_id=producto.id,
                quantity=round(random.uniform(1, 5), 2),
                unit="kg",
                price_unit=producto.price,
                currency="EUR",
                total_price=producto.price * 2,
                expiration_date=datetime.now().date() + timedelta(days=5),
                nutritional_info={},
                added_at=datetime.now()
            )
            session.add(item)
            session.commit()


def crear_conductores_y_vehiculos(n=3):
    for _ in range(n):
        vehicle = Vehicle(
            type="van",
            name=fake.word(),
            plate_number=fake.license_plate(),
            capacity_kg=500,
            capacity_m3=10,
            speed_kmph=50,
            emissions_factor=0.2
        )
        session.add(vehicle)
        session.commit()

        driver = Driver(
            name=fake.name(),
            email=fake.unique.email(),
            phone=fake.phone_number(),
            license_number=fake.license_plate(),
            vehicle_id=vehicle.id,
            location_lat=round(random.uniform(40.4, 40.5), 6),
            location_lon=round(random.uniform(-3.7, -3.6), 6),
            working_hours={"start": "08:00", "end": "18:00"}
        )
        session.add(driver)
    session.commit()


# Ejecutar el test completo
session.begin()
try:
    consumidores, proveedores, productos = crear_usuarios_y_productos()
    crear_listas_de_compra(consumidores, productos)
    crear_conductores_y_vehiculos()
    session.commit()
    print("✅ Datos de prueba generados correctamente.")
except Exception as e:
    session.rollback()
    print("❌ Error al generar datos de prueba:", str(e))
finally:
    session.close()
