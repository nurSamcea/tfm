-- =====================================================
-- ESQUEMA SIMPLIFICADO - SOLO TABLAS FUNCIONALES
-- =====================================================
-- Mantiene únicamente las 6 tablas que realmente se usan
-- Elimina 7 tablas de código muerto (54% menos complejidad)

-- =====================================================
-- 1. USUARIOS
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('consumer', 'farmer', 'supermarket')),
    entity_name TEXT,
    location_lat DECIMAL,
    location_lon DECIMAL,
    preferences JSON,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2. ZONAS DE SENSORES
-- =====================================================
CREATE TABLE IF NOT EXISTS sensor_zones (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    location_lat FLOAT,
    location_lon FLOAT,
    location_description TEXT,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. SENSORES
-- =====================================================
CREATE TABLE IF NOT EXISTS sensors (
    id SERIAL PRIMARY KEY,
    device_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    sensor_type VARCHAR(50) NOT NULL CHECK (sensor_type IN ('temperature', 'humidity', 'soil_moisture', 'gas', 'light', 'shock', 'gps', 'ph')),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'error', 'maintenance')),
    zone_id INTEGER REFERENCES sensor_zones(id) ON DELETE SET NULL,
    location_lat FLOAT,
    location_lon FLOAT,
    location_description TEXT,
    min_threshold FLOAT,
    max_threshold FLOAT,
    alert_enabled BOOLEAN DEFAULT TRUE,
    reading_interval INTEGER DEFAULT 30,
    firmware_version VARCHAR(50),
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    config JSON
);

-- =====================================================
-- 4. PRODUCTOS
-- =====================================================
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    unit VARCHAR(20),
    category VARCHAR(50) NOT NULL CHECK (category IN (
        'verduras','frutas','cereales','legumbres','frutos_secos','lacteos','carnes','pescados','huevos','hierbas','especias','otros'
    )),
    is_eco BOOLEAN DEFAULT FALSE,
    stock_available INTEGER DEFAULT 0,
    expiration_date DATE,
    image_url VARCHAR(500),
    provider_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    is_hidden BOOLEAN DEFAULT FALSE,
    certifications JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 5. LECTURAS DE SENSORES
-- =====================================================
CREATE TABLE IF NOT EXISTS sensor_readings (
    id SERIAL PRIMARY KEY,
    sensor_id INTEGER REFERENCES sensors(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES products(id),  -- Para compatibilidad
    temperature DECIMAL(5,2),
    humidity DECIMAL(5,2),
    gas_level DECIMAL(5,2),
    light_level DECIMAL(5,2),
    shock_detected BOOLEAN DEFAULT FALSE,
    soil_moisture DECIMAL(5,2),
    ph_level DECIMAL(4,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source_device TEXT,  -- ID del dispositivo (para compatibilidad)
    reading_quality DECIMAL(3,2) DEFAULT 1.0,  -- Calidad de la señal (0-1)
    is_processed BOOLEAN DEFAULT FALSE,  -- Si fue procesado por edge computing
    extra_data JSON  -- Datos adicionales
);

-- =====================================================
-- 5.b ALERTAS DE SENSORES
-- =====================================================
-- Implementado con VARCHAR + CHECK para evitar dependencias de tipos ENUM nativos.
-- Compatible con el modelo ORM (almacena valores string del Enum).
CREATE TABLE IF NOT EXISTS sensor_alerts (
    id               SERIAL PRIMARY KEY,
    sensor_id        INTEGER NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    alert_type       VARCHAR(50) NOT NULL CHECK (
        alert_type IN (
            'temperature_high','temperature_low','humidity_high','humidity_low',
            'sensor_offline','sensor_error','threshold_exceeded'
        )
    ),
    status           VARCHAR(20) DEFAULT 'active' CHECK (
        status IN ('active','acknowledged','resolved','dismissed')
    ),
    title            VARCHAR(200) NOT NULL,
    message          TEXT NOT NULL,
    severity         VARCHAR(20),
    threshold_value  DOUBLE PRECISION,
    actual_value     DOUBLE PRECISION,
    unit             VARCHAR(20),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at  TIMESTAMP,
    resolved_at      TIMESTAMP,
    acknowledged_by  INTEGER REFERENCES users(id),
    extra_data       JSON
);

-- =====================================================
-- 6. TRANSACCIONES (PEDIDOS Y VENTAS)
-- =====================================================
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    buyer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    seller_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    buyer_type VARCHAR(20) NOT NULL CHECK (buyer_type IN ('consumer', 'supermarket')),
    seller_type VARCHAR(20) NOT NULL CHECK (seller_type IN ('farmer', 'supermarket')),
    buyer_name VARCHAR(100),
    seller_name VARCHAR(100),
    total_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'in_progress', 'delivered', 'cancelled')),
    order_details JSON NOT NULL DEFAULT '[]',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_sensors_device_id ON sensors(device_id);
CREATE INDEX IF NOT EXISTS idx_sensors_zone_id ON sensors(zone_id);
CREATE INDEX IF NOT EXISTS idx_sensor_readings_sensor_id ON sensor_readings(sensor_id);
CREATE INDEX IF NOT EXISTS idx_sensor_readings_timestamp ON sensor_readings(created_at);
CREATE INDEX IF NOT EXISTS idx_sensor_alerts_sensor_id ON sensor_alerts(sensor_id);
CREATE INDEX IF NOT EXISTS idx_sensor_alerts_status ON sensor_alerts(status);
CREATE INDEX IF NOT EXISTS idx_products_provider_id ON products(provider_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_transactions_buyer_id ON transactions(buyer_id);
CREATE INDEX IF NOT EXISTS idx_transactions_seller_id ON transactions(seller_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);

-- =====================================================
-- COMENTARIOS
-- =====================================================
COMMENT ON TABLE users IS 'Usuarios del sistema (consumidores, agricultores, supermercados)';
COMMENT ON TABLE sensor_zones IS 'Zonas donde se ubican los sensores IoT';
COMMENT ON TABLE sensors IS 'Sensores IoT conectados al sistema';
COMMENT ON TABLE sensor_readings IS 'Lecturas de datos de los sensores IoT';
COMMENT ON TABLE sensor_alerts IS 'Alertas generadas a partir de las lecturas de sensores';
COMMENT ON TABLE products IS 'Catálogo de productos disponibles';
COMMENT ON TABLE transactions IS 'Transacciones de pedidos y ventas entre usuarios';
