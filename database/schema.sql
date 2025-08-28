-- =====================================================
-- ESQUEMA DE BASE DE DATOS - Plataforma de Gestión Alimentaria
-- =====================================================

-- Extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- TABLA DE USUARIOS
-- =====================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'consumer', -- consumer, farmer, retailer, supermarket, admin
    entity_name TEXT,
    location_lat DECIMAL,
    location_lon DECIMAL,
    preferences JSON DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- =====================================================
-- TABLA DE PRODUCTOS
-- =====================================================
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL,
    currency TEXT DEFAULT 'EUR',
    unit TEXT DEFAULT 'unidad',
    category TEXT,
    stock_available FLOAT DEFAULT 0,
    expiration_date DATE,
    is_eco BOOLEAN DEFAULT FALSE,
    image_url TEXT,
    provider_id INTEGER REFERENCES users(id),
    certifications JSON DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_provider ON products(provider_id);
CREATE INDEX idx_products_name ON products(name);

-- =====================================================
-- TABLA DE LISTAS DE COMPRA
-- =====================================================
CREATE TABLE shopping_lists (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_price DECIMAL DEFAULT 0,
    currency TEXT DEFAULT 'EUR',
    status TEXT DEFAULT 'draft', -- draft, pending, paid, delivered, cancelled
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shopping_lists_user ON shopping_lists(user_id);
CREATE INDEX idx_shopping_lists_status ON shopping_lists(status);

-- =====================================================
-- TABLA DE GRUPOS DE LISTA DE COMPRA (por proveedor)
-- =====================================================
CREATE TABLE shopping_list_groups (
    id SERIAL PRIMARY KEY,
    shopping_list_id INTEGER NOT NULL REFERENCES shopping_lists(id) ON DELETE CASCADE,
    provider_id INTEGER NOT NULL REFERENCES users(id),
    subtotal_price DECIMAL DEFAULT 0,
    delivery_estimate TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shopping_list_groups_list ON shopping_list_groups(shopping_list_id);
CREATE INDEX idx_shopping_list_groups_provider ON shopping_list_groups(provider_id);

-- =====================================================
-- TABLA DE ITEMS DE LISTA DE COMPRA
-- =====================================================
CREATE TABLE shopping_list_items (
    id SERIAL PRIMARY KEY,
    shopping_list_group_id INTEGER NOT NULL REFERENCES shopping_list_groups(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity FLOAT NOT NULL DEFAULT 1,
    unit TEXT DEFAULT 'unidad',
    price_unit FLOAT,
    currency TEXT DEFAULT 'EUR',
    total_price FLOAT,
    expiration_date DATE,
    trace_hash TEXT,
    nutritional_info JSON DEFAULT '{}',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shopping_list_items_group ON shopping_list_items(shopping_list_group_id);
CREATE INDEX idx_shopping_list_items_product ON shopping_list_items(product_id);

-- =====================================================
-- TABLA DE TRANSACCIONES
-- =====================================================
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    shopping_list_id INTEGER REFERENCES shopping_lists(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    amount DECIMAL NOT NULL,
    currency TEXT DEFAULT 'EUR',
    status TEXT DEFAULT 'pending', -- pending, completed, failed, refunded
    payment_method TEXT,
    transaction_hash TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_status ON transactions(status);

-- =====================================================
-- TABLA DE LECTURAS DE SENSORES
-- =====================================================
CREATE TABLE sensor_readings (
    id SERIAL PRIMARY KEY,
    sensor_id TEXT NOT NULL,
    sensor_type TEXT NOT NULL, -- temperature, humidity, light, etc.
    value FLOAT NOT NULL,
    unit TEXT,
    location_lat DECIMAL,
    location_lon DECIMAL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSON DEFAULT '{}'
);

CREATE INDEX idx_sensor_readings_sensor ON sensor_readings(sensor_id);
CREATE INDEX idx_sensor_readings_type ON sensor_readings(sensor_type);
CREATE INDEX idx_sensor_readings_timestamp ON sensor_readings(timestamp);

-- =====================================================
-- TABLA DE CÓDIGOS QR
-- =====================================================
CREATE TABLE qrs (
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES products(id),
    qr_code TEXT UNIQUE NOT NULL,
    url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_qrs_product ON qrs(product_id);
CREATE INDEX idx_qrs_code ON qrs(qr_code);

-- =====================================================
-- TABLA DE MÉTRICAS DE IMPACTO
-- =====================================================
CREATE TABLE impact_metrics (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    product_id INTEGER REFERENCES products(id),
    metric_type TEXT NOT NULL, -- co2_emissions, water_usage, etc.
    value FLOAT NOT NULL,
    unit TEXT,
    calculation_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_impact_metrics_user ON impact_metrics(user_id);
CREATE INDEX idx_impact_metrics_product ON impact_metrics(product_id);
CREATE INDEX idx_impact_metrics_type ON impact_metrics(metric_type);

-- =====================================================
-- TABLA DE LOGS DE BLOCKCHAIN
-- =====================================================
CREATE TABLE blockchain_logs (
    id SERIAL PRIMARY KEY,
    transaction_hash TEXT UNIQUE NOT NULL,
    block_number INTEGER,
    product_id INTEGER REFERENCES products(id),
    action TEXT NOT NULL, -- create, update, transfer, etc.
    data JSON NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blockchain_logs_hash ON blockchain_logs(transaction_hash);
CREATE INDEX idx_blockchain_logs_product ON blockchain_logs(product_id);
CREATE INDEX idx_blockchain_logs_action ON blockchain_logs(action);
