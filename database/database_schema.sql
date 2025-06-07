
-- Tabla de usuarios
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role TEXT CHECK (role IN ('consumer', 'farmer', 'retailer', 'restaurant', 'admin')) NOT NULL,
  entity_name TEXT,
  location_lat DECIMAL,
  location_lon DECIMAL,
  preferences JSONB,
  intake_profile_id INT REFERENCES intake_profiles(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de perfiles de ingesta
CREATE TABLE intake_profiles (
  id SERIAL PRIMARY KEY,
  name TEXT,
  gender TEXT,
  age_range TEXT,
  activity TEXT,
  kcal INT,
  protein FLOAT,
  carbohydrates FLOAT,
  sugars FLOAT,
  fat FLOAT,
  saturated_fat FLOAT,
  fiber FLOAT,
  salt FLOAT,
  cholesterol FLOAT,
  calcium FLOAT,
  iron FLOAT,
  vitamin_c FLOAT,
  vitamin_d FLOAT,
  vitamin_b12 FLOAT,
  potassium FLOAT,
  magnesium FLOAT
);

-- Tabla de productos
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  price DECIMAL,
  currency TEXT,
  unit TEXT,
  category TEXT,
  nutritional_info JSONB,
  stock_available FLOAT,
  expiration_date DATE,
  is_eco BOOLEAN,
  image_url TEXT,
  provider_id INT REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE products
ADD COLUMN generic_name TEXT;
ALTER TABLE products
ADD CONSTRAINT fk_generic_name
FOREIGN KEY (generic_name) REFERENCES nutritional_info(generic_name);


CREATE TABLE nutritional_info (
  id SERIAL PRIMARY KEY,
  generic_name TEXT NOT NULL UNIQUE,  -- "tomate", "lenteja", "pimiento", etc.
  kcal FLOAT,
  protein FLOAT,
  carbohydrates FLOAT,
  sugars FLOAT,
  fat FLOAT,
  saturated_fat FLOAT,
  fiber FLOAT,
  salt FLOAT,
  cholesterol FLOAT,
  calcium FLOAT,
  iron FLOAT,
  vitamin_c FLOAT,
  vitamin_d FLOAT,
  vitamin_b12 FLOAT,
  potassium FLOAT,
  magnesium FLOAT,
  per_unit TEXT,          -- Ej: "100g", "unidad", etc.
  source TEXT             -- Fuente opcional de los datos
);

-- Información nutricional (copiada en cada compra o receta)
-- No es tabla en este diseño, es campo JSONB usado en varias tablas

-- Tabla de listas de compra
CREATE TABLE shopping_lists (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total_price DECIMAL,
  currency TEXT,
  status TEXT
);

-- Grupos de lista de compra (por proveedor)
CREATE TABLE shopping_list_groups (
  id SERIAL PRIMARY KEY,
  shopping_list_id INT REFERENCES shopping_lists(id),
  provider_id INT REFERENCES users(id),
  subtotal_price DECIMAL,
  delivery_estimate TIMESTAMP,
  logistics_route_id INT REFERENCES logistics_routes(id)
);
ALTER TABLE shopping_list_groups
ADD COLUMN delivery_window JSONB, -- ej: {"start": "10:00", "end": "12:00"}
ADD COLUMN delivery_status TEXT DEFAULT 'not_delivered', -- estado de entrega
ADD COLUMN logistics_stop_order INT; -- orden de parada en la ruta


-- Elementos dentro del grupo de compra
CREATE TABLE shopping_list_items (
  id SERIAL PRIMARY KEY,
  shopping_list_group_id INT REFERENCES shopping_list_groups(id),
  product_id INT REFERENCES products(id),
  quantity FLOAT,
  unit TEXT,
  price_unit FLOAT,
  currency TEXT,
  total_price FLOAT,
  expiration_date DATE,
  trace_hash TEXT,
  nutritional_info JSONB,
  added_at TIMESTAMP
);

-- Tabla de recetas
CREATE TABLE recipes (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  author_id INT REFERENCES users(id),
  image_url TEXT,
  steps JSONB,
  time_minutes INT,
  difficulty TEXT,
  is_vegan BOOLEAN,
  is_gluten_free BOOLEAN,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  tags JSONB,
  nutrition_total JSONB
);

-- Ingredientes de recetas
CREATE TABLE recipe_ingredients (
  id SERIAL PRIMARY KEY,
  recipe_id INT REFERENCES recipes(id),
  product_id INT REFERENCES products(id),
  name TEXT,
  quantity FLOAT,
  unit TEXT,
  nutritional_info JSONB,
  optional BOOLEAN
);

-- Planificación semanal de recetas
CREATE TABLE weekly_plans (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  intake_profile_id INT REFERENCES intake_profiles(id),
  week_start DATE,
  created_at TIMESTAMP,
  comment TEXT
);

-- Items de planificación semanal
CREATE TABLE weekly_plan_items (
  id SERIAL PRIMARY KEY,
  weekly_plan_id INT REFERENCES weekly_plans(id),
  day_of_week TEXT,
  meal_type TEXT,
  recipe_id INT REFERENCES recipes(id),
  portions INT,
  nutrition_total JSONB
);

-- Transacciones
CREATE TABLE transactions (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  shopping_list_id INT REFERENCES shopping_lists(id),
  total_price DECIMAL,
  currency TEXT,
  status TEXT,
  created_at TIMESTAMP,
  confirmed_at TIMESTAMP
);

-- Rutas logísticas
CREATE TABLE logistics_routes (
  id SERIAL PRIMARY KEY,
  driver_name TEXT,
  distance_km FLOAT,
  orders_ids JSONB,
  created_at TIMESTAMP,
  vehicle_type TEXT,
  estimated_time_min INT
);
ALTER TABLE logistics_routes
ADD COLUMN driver_id INT REFERENCES drivers(id),
ADD COLUMN vehicle_id INT REFERENCES vehicles(id),
ADD COLUMN date DATE,
ADD COLUMN status TEXT DEFAULT 'pending';


-- nodos de recogida y entrega
CREATE TABLE logistics_route_stops (
  id SERIAL PRIMARY KEY,
  logistics_route_id INT REFERENCES logistics_routes(id),
  stop_order INT,
  stop_type TEXT CHECK (stop_type IN ('pickup', 'delivery')),
  related_group_id INT REFERENCES shopping_list_groups(id),
  sender_id INT REFERENCES users(id),     -- el que entrega (agricultor o supermercado)
  receiver_id INT REFERENCES users(id),   -- el que recibe (supermercado o consumidor)
  location_lat DECIMAL,
  location_lon DECIMAL,
  eta TIMESTAMP,                          -- hora estimada de llegada
  status TEXT DEFAULT 'pending'          -- 'pending', 'delivered', 'failed', etc.
);


-- Conductores
CREATE TABLE drivers (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  phone TEXT,
  email TEXT UNIQUE,
  license_number TEXT,
  vehicle_id INT REFERENCES vehicles(id),
  location_lat DECIMAL, -- ubicación inicial o actual
  location_lon DECIMAL,
  working_hours JSONB, -- ej: {"start": "08:00", "end": "18:00"}
  available BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- vehiculos propios
CREATE TABLE vehicles (
  id SERIAL PRIMARY KEY,
  type TEXT, -- "bike", "van", "truck", etc.
  name TEXT,
  plate_number TEXT,
  capacity_kg FLOAT,
  capacity_m3 FLOAT,
  speed_kmph FLOAT,
  emissions_factor FLOAT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- Lecturas de sensores
CREATE TABLE sensor_readings (
  id SERIAL PRIMARY KEY,
  product_id INT REFERENCES products(id),
  temperature FLOAT,
  humidity FLOAT,
  gas_level FLOAT,
  light_level FLOAT,
  shock_detected BOOLEAN,
  created_at TIMESTAMP,
  source_device TEXT
);

-- Códigos QR y trazabilidad
CREATE TABLE qrs (
  id SERIAL PRIMARY KEY,
  product_id INT REFERENCES products(id),
  qr_hash TEXT,
  created_at TIMESTAMP,
  metadata JSONB
);

-- Registros blockchain
CREATE TABLE blockchain_logs (
  id SERIAL PRIMARY KEY,
  entity_type TEXT,
  entity_id INT,
  hash TEXT,
  timestamp TIMESTAMP
);

-- Métricas de impacto
CREATE TABLE impact_metrics (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  co2_saved_kg FLOAT,
  local_support_eur FLOAT,
  waste_prevented_kg FLOAT
);
