import json
import random
from datetime import datetime, timedelta

from faker import Faker

fake = Faker()


# 2. Users (consumers + providers)
roles = ['consumer', 'farmer', 'retailer', 'restaurant']
users = []
for i in range(50):
    role = random.choice(roles)
    preferences = {"eco_only": random.choice([True, False]), "vegan": random.choice([True, False])}
    users.append(f"""INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        '{fake.name()}', '{fake.email()}', '{fake.sha256()}', '{role}', '{fake.company()}',
        {fake.latitude()}, {fake.longitude()}, '{json.dumps(preferences)}', {random.randint(1, 3)}
    );""")

# 3. Products
products = []
for i in range(50):
    nutrition = {"kcal": random.randint(50, 200), "protein": random.uniform(1, 5)}
    products.append(f"""INSERT INTO products (
        name, description, price, currency, unit, category, nutritional_info,
        stock_available, expiration_date, is_eco, image_url, provider_id
    ) VALUES (
        '{fake.word()}', '{fake.sentence()}', {random.uniform(1.0, 10.0):.2f}, 'EUR', 'kg',
        'vegetables', '{json.dumps(nutrition)}', {random.uniform(10, 100):.2f},
        '{(datetime.today() + timedelta(days=random.randint(5, 30))).strftime('%Y-%m-%d')}',
        {random.choice(['TRUE', 'FALSE'])}, '{fake.image_url()}', {random.randint(1, 10)}
    );""")

# Combine and output
all_inserts = intake_profiles + users + products
insert_script = "\n".join(all_inserts)

insert_script[:5000]  # Show the first part of the SQL insert script
