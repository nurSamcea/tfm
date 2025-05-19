
-- Tabla de usuarios (actores del sistema)
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,
  role TEXT CHECK (role IN ('consumer', 'farmer', 'retailer', 'restaurant', 'admin')) NOT NULL,
  entity_name TEXT,
  location JSONB,
  preferences JSONB,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Catálogo base de productos
CREATE TABLE product_templates (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  category TEXT,
  subcategory TEXT,
  default_image_url TEXT,
  nutrition JSONB,
  allergens TEXT[],
  labels TEXT[]
);

-- Productos disponibles
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  price DECIMAL(10,2),
  stock INT,
  unit TEXT,
  expiration_date DATE,
  image_url TEXT,
  is_local BOOLEAN DEFAULT TRUE,
  owner_id INT REFERENCES users(id),
  template_id INT REFERENCES product_templates(id),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Listas de compra por usuario
CREATE TABLE shopping_lists (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shopping_list_items (
  id SERIAL PRIMARY KEY,
  shopping_list_id INT REFERENCES shopping_lists(id) ON DELETE CASCADE,
  product_id INT REFERENCES products(id),
  quantity INT,
  status TEXT CHECK (status IN ('pending', 'bought', 'replaced')) DEFAULT 'pending'
);

-- Transacciones (pedidos)
CREATE TABLE transactions (
  id SERIAL PRIMARY KEY,
  buyer_id INT REFERENCES users(id),
  seller_id INT REFERENCES users(id),
  total_price DECIMAL(10,2),
  payment_method TEXT,
  status TEXT CHECK (status IN ('pending','paid','delivered','cancelled')) DEFAULT 'pending',
  delivery_method TEXT,
  delivery_estimated TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recetas
CREATE TABLE recipes (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  image_url TEXT,
  is_vegan BOOLEAN,
  created_by INT REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipe_ingredients (
  id SERIAL PRIMARY KEY,
  recipe_id INT REFERENCES recipes(id) ON DELETE CASCADE,
  name TEXT,
  quantity TEXT
);

-- Planificación semanal
CREATE TABLE weekly_plans (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  week_start DATE NOT NULL
);

CREATE TABLE weekly_plan_items (
  id SERIAL PRIMARY KEY,
  plan_id INT REFERENCES weekly_plans(id) ON DELETE CASCADE,
  recipe_id INT REFERENCES recipes(id),
  day TEXT CHECK (day IN ('monday','tuesday','wednesday','thursday','friday','saturday','sunday')),
  meal TEXT CHECK (meal IN ('breakfast','lunch','dinner'))
);

-- QR y trazabilidad
CREATE TABLE product_qrs (
  id SERIAL PRIMARY KEY,
  product_id INT REFERENCES products(id),
  blockchain_hash TEXT NOT NULL,
  qr_code TEXT UNIQUE NOT NULL,
  origin TEXT,
  collected_at DATE,
  certification TEXT,
  temperature_log JSONB,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_items (
  id SERIAL PRIMARY KEY,
  transaction_id INT REFERENCES transactions(id),
  product_id INT REFERENCES products(id),
  quantity INT,
  unit_price DECIMAL(10,2),
  qr_id INT REFERENCES product_qrs(id)
);

-- Sensores
CREATE TABLE sensor_readings (
  id SERIAL PRIMARY KEY,
  device_id TEXT,
  product_id INT REFERENCES products(id),
  temperature DECIMAL,
  humidity DECIMAL,
  gas_level DECIMAL,
  voltage DECIMAL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Logs blockchain
CREATE TABLE blockchain_logs (
  id SERIAL PRIMARY KEY,
  transaction_id INT REFERENCES transactions(id),
  block_hash TEXT,
  block_timestamp TIMESTAMP,
  status TEXT CHECK (status IN ('initiated','validated','error')),
  qr_code_url TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Métricas de impacto ecológico/social
CREATE TABLE impact_metrics (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  co2_saved DECIMAL,
  km_saved DECIMAL,
  local_support_pct DECIMAL,
  zero_waste_score DECIMAL,
  calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rutas logísticas
CREATE TABLE logistics_routes (
  id SERIAL PRIMARY KEY,
  route_id TEXT,
  driver_name TEXT,
  from_point TEXT,
  to_point TEXT,
  distance_km DECIMAL,
  estimated_time_min INT,
  vehicle_type TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recomendaciones
CREATE TABLE recommendations (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  context TEXT,
  recommended TEXT,
  metadata JSONB,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
