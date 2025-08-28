-- =====================================================
-- DATOS DE EJEMPLO - Plataforma de Gestión Alimentaria
-- =====================================================

-- =====================================================
-- USUARIOS DE EJEMPLO
-- =====================================================
INSERT INTO users (name, email, password_hash, role, entity_name, location_lat, location_lon, preferences) VALUES
('Juan Pérez', 'juan@email.com', '$2b$12$hash123', 'consumer', 'Juan Pérez', 40.4168, -3.7038, '{"diet": "omnivore", "preferences": ["local_products"]}'),
('María García', 'maria@email.com', '$2b$12$hash456', 'farmer', 'Huerta Ecológica María', 40.4168, -3.7038, '{"certifications": ["organic", "eco_friendly"], "specialties": ["vegetables", "herbs"]}'),
('Carlos López', 'carlos@email.com', '$2b$12$hash789', 'retailer', 'Supermercado Carlos', 40.4168, -3.7038, '{"services": ["delivery", "pickup"], "specialties": ["dairy", "bakery"]}'),
('Admin', 'admin@email.com', '$2b$12$hashadmin', 'admin', 'Administrador', 40.4168, -3.7038, '{"permissions": ["all"], "role": "system_admin"}'),
('Lucía Fernández', 'lucia.fernandez@email.com', 'hash_lucia', 'consumer', NULL, 40.4200, -3.7050, '{"diet": "vegetarian", "allergies": ["nuts"], "preferences": ["organic_only"]}'),
('Pedro Sánchez', 'pedro.sanchez@email.com', 'hash_pedro', 'farmer', 'Huerta Pedro', 40.4300, -3.7100, '{"certifications": ["organic"], "specialties": ["root_vegetables", "fruits"]}'),
('Ana Torres', 'ana.torres@email.com', 'hash_ana', 'retailer', 'Frutería Ana', 40.4250, -3.7000, '{"services": ["local_delivery"], "specialties": ["fruits", "vegetables"]}'),
('Ana Martínez', 'ana@email.com', '$2b$12$hashana', 'consumer', 'Ana Martínez', 40.4168, -3.7038, '{"diet": "vegetarian", "allergies": ["lactose"]}'),
('Laura Fernández', 'laura@email.com', '$2b$12$hashlaura', 'retailer', 'Mercado Local Laura', 40.4168, -3.7038, '{"specialties": ["local_products", "seasonal"]}'),
('Miguel Torres', 'miguel@email.com', '$2b$12$hashmiguel', 'supermarket', 'Supermercado Torres', 40.4168, -3.7038, '{"services": ["delivery", "pickup"]}'),
('Carmen Ruiz', 'carmen@email.com', '$2b$12$hashcarmen', 'consumer', 'Carmen Ruiz', 40.4168, -3.7038, '{"diet": "vegan", "preferences": ["organic_only"]}');


-- =====================================================
-- PRODUCTOS DE EJEMPLO
-- =====================================================
INSERT INTO products (name, description, price, currency, unit, category, stock_available, expiration_date, is_eco, image_url, provider_id, certifications) VALUES
-- Productos de María (Huerta Ecológica)
('Tomates Ecológicos', 'Tomates cultivados sin pesticidas', 2.50, 'EUR', 'kg', 'Verduras', 50.0, '2024-09-15', TRUE, 'https://img.com/tomates.jpg', 2, '{"organic": true, "pesticide_free": true}'),
('Lechuga Fresca', 'Lechuga de hoja verde recién cosechada', 1.20, 'EUR', 'unidad', 'Verduras', 30.0, '2024-09-10', TRUE, 'https://img.com/lechuga.jpg', 2, '{"organic": true, "fresh": true}'),
('Manzanas Rojas', 'Manzanas rojas dulces y jugosas', 1.80, 'EUR', 'kg', 'Frutas', 25.0, '2024-09-20', TRUE, 'https://img.com/manzanas.jpg', 2, '{"organic": true, "sweet": true}'),

-- Productos de Carlos (Supermercado)
('Pan Integral', 'Pan integral de trigo completo', 2.00, 'EUR', 'unidad', 'Panadería', 20.0, '2024-09-05', FALSE, 'https://img.com/pan.jpg', 3, '{"whole_grain": true, "fresh_baked": true}'),
('Leche Orgánica', 'Leche orgánica de vacas alimentadas con pasto', 1.50, 'EUR', 'litro', 'Lácteos', 40.0, '2024-09-08', TRUE, 'https://img.com/leche.jpg', 3, '{"organic": true, "grass_fed": true}'),

-- Productos de Pedro (Huerta Pedro)
('Zanahorias', 'Zanahorias frescas de la huerta', 1.10, 'EUR', 'kg', 'Verduras', 80.0, '2024-09-10', TRUE, 'https://img.com/zanahorias.jpg', 6, '{"organic": true, "fresh": true}'),
('Naranjas', 'Naranjas valencianas', 2.00, 'EUR', 'kg', 'Frutas', 120.0, '2024-09-20', FALSE, 'https://img.com/naranjas.jpg', 6, '{"local": true, "valencian": true}'),
('Huevos camperos', 'Huevos de gallinas en libertad', 2.50, 'EUR', 'docena', 'Huevos', 30.0, '2024-08-30', TRUE, 'https://img.com/huevos.jpg', 6, '{"free_range": true, "organic": true}'),
('Huevos Ecológicos', 'Huevos de gallinas criadas en libertad', 3.50, 'EUR', 'docena', 'Huevos', 15.0, '2024-02-15', TRUE, 'https://img.com/huevos_eco.jpg', 6, '{"organic": true, "free_range": true}'),
('Fresas Ecológicas', 'Fresas dulces de temporada', 4.20, 'EUR', 'kg', 'Frutas', 12.0, '2024-01-25', TRUE, 'https://img.com/fresas.jpg', 6, '{"organic": true, "seasonal": true}'),

-- Productos de Ana (Frutería Ana)
('Zanahorias Orgánicas', 'Zanahorias cultivadas sin químicos', 1.80, 'EUR', 'kg', 'Verduras', 40.0, '2024-02-10', TRUE, 'https://img.com/zanahorias_org.jpg', 7, '{"organic": true}'),

-- Productos de Laura (Mercado Local)
('Queso Manchego', 'Queso manchego curado 12 meses', 8.50, 'EUR', 'kg', 'Lácteos', 8.0, '2024-03-01', FALSE, 'https://img.com/queso.jpg', 9, '{"local": true, "artisan": true}'),
('Miel de Lavanda', 'Miel natural de lavanda local', 6.00, 'EUR', '500g', 'Miel', 10.0, '2025-12-31', TRUE, 'https://img.com/miel.jpg', 9, '{"local": true, "natural": true}'),
('Aceite de Oliva', 'Aceite de oliva virgen extra', 12.00, 'EUR', 'litro', 'Aceites', 20.0, '2025-06-30', TRUE, 'https://img.com/aceite.jpg', 9, '{"local": true, "extra_virgin": true}'),

-- Productos de Miguel (Supermercado Torres)
('Arroz Integral', 'Arroz integral de grano largo', 2.80, 'EUR', 'kg', 'Cereales', 50.0, '2025-01-01', FALSE, 'https://img.com/arroz.jpg', 10, '{"whole_grain": true}'),
('Atún en Conserva', 'Atún en aceite de oliva', 3.50, 'EUR', 'lata', 'Conservas', 30.0, '2026-01-01', FALSE, 'https://img.com/atun.jpg', 10, '{"sustainable_fishing": true}'),
('Yogur Griego', 'Yogur griego natural sin azúcar', 2.20, 'EUR', 'unidad', 'Lácteos', 25.0, '2024-02-05', FALSE, 'https://img.com/yogur.jpg', 10, '{"high_protein": true}');

-- =====================================================
-- LISTA DE COMPRA DE EJEMPLO
-- =====================================================
INSERT INTO shopping_lists (user_id, total_price, currency, status) VALUES
(1, 11.20, 'EUR', 'draft'), -- Juan - lista en borrador (tomates + lechuga + pan + leche)
(8, 25.50, 'EUR', 'pending'), -- Ana Martínez - lista pendiente
(11, 45.20, 'EUR', 'paid'), -- Carmen - lista pagada
(1, 20.50, 'EUR', 'delivered'); -- Juan - lista entregada (arroz + atún + yogur)

-- =====================================================
-- GRUPOS DE LISTA DE COMPRA (por proveedor)
-- =====================================================
INSERT INTO shopping_list_groups (shopping_list_id, provider_id, subtotal_price, delivery_estimate) VALUES
-- Lista de Juan (draft) - ID 1
(1, 2, 6.20, '2024-01-20 14:00:00'), -- Grupo de María (tomates + lechuga)
(1, 3, 5.00, '2024-01-20 14:30:00'), -- Grupo de Carlos (pan + leche)

-- Lista de Ana Martínez (pending) - ID 2
(2, 6, 9.20, '2024-01-20 14:00:00'), -- Ana compra de Pedro (huevos + zanahorias + fresas)
(2, 9, 16.30, '2024-01-20 16:00:00'), -- Ana compra de Laura (queso + miel)

-- Lista de Carmen (paid) - ID 3
(3, 6, 16.60, '2024-01-19 10:00:00'), -- Carmen compra de Pedro (huevos + zanahorias + fresas)
(3, 9, 28.60, '2024-01-19 12:00:00'), -- Carmen compra de Laura (miel + aceite)

-- Lista de Juan (delivered) - ID 4
(4, 10, 20.50, '2024-01-18 15:00:00'); -- Juan compra de Miguel (arroz + atún + yogur)

-- =====================================================
-- ITEMS DE LISTA DE COMPRA
-- =====================================================
INSERT INTO shopping_list_items (shopping_list_group_id, product_id, quantity, unit, price_unit, total_price, nutritional_info) VALUES
-- Lista de Juan (draft) - ID 1 - Productos de María
(1, 1, 2.0, 'kg', 2.50, 5.00, '{"vitamin_c": "21mg", "potassium": "237mg", "fiber": "1.2g"}'), -- 2kg de tomates
(1, 2, 1.0, 'unidad', 1.20, 1.20, '{"vitamin_a": "166%", "vitamin_k": "126%", "fiber": "1.3g"}'), -- 1 lechuga

-- Lista de Juan (draft) - ID 1 - Productos de Carlos
(2, 4, 1.0, 'unidad', 2.00, 2.00, '{"fiber": "3.5g", "protein": "7g", "complex_carbs": "45g"}'), -- 1 pan
(2, 5, 2.0, 'litro', 1.50, 3.00, '{"protein": "8g", "calcium": "300mg", "vitamin_d": "120IU"}'), -- 2 litros de leche

-- Lista de Ana Martínez (pending) - ID 2 - Productos de Pedro
(3, 6, 1.0, 'docena', 3.50, 3.50, '{"protein": "12g", "fat": "10g", "cholesterol": "180mg"}'), -- Huevos ecológicos
(3, 7, 2.0, 'kg', 1.80, 3.60, '{"vitamin_a": "835%", "fiber": "3.6g", "vitamin_c": "9%"}'), -- Zanahorias orgánicas
(3, 8, 0.5, 'kg', 4.20, 2.10, '{"vitamin_c": "85mg", "fiber": "2g", "antioxidants": "high"}'), -- Fresas ecológicas

-- Lista de Ana Martínez (pending) - ID 2 - Productos de Laura
(4, 10, 0.3, 'kg', 8.50, 2.55, '{"protein": "25g", "calcium": "700mg", "fat": "30g"}'), -- Queso manchego
(4, 11, 1.0, '500g', 6.00, 6.00, '{"natural_sugar": "82g", "antioxidants": "high"}'), -- Miel de lavanda

-- Lista de Carmen (paid) - ID 3 - Productos de Pedro
(5, 6, 2.0, 'docena', 3.50, 7.00, '{"protein": "12g", "fat": "10g", "cholesterol": "180mg"}'), -- Huevos ecológicos
(5, 7, 3.0, 'kg', 1.80, 5.40, '{"vitamin_a": "835%", "fiber": "3.6g", "vitamin_c": "9%"}'), -- Zanahorias orgánicas
(5, 8, 1.0, 'kg', 4.20, 4.20, '{"vitamin_c": "85mg", "fiber": "2g", "antioxidants": "high"}'), -- Fresas ecológicas

-- Lista de Carmen (paid) - ID 3 - Productos de Laura
(6, 11, 2.0, '500g', 6.00, 12.00, '{"natural_sugar": "82g", "antioxidants": "high"}'), -- Miel de lavanda
(6, 12, 1.0, 'litro', 12.00, 12.00, '{"healthy_fats": "14g", "vitamin_e": "14mg"}'), -- Aceite de oliva

-- Lista de Juan (delivered) - ID 4 - Productos de Miguel
(7, 13, 2.0, 'kg', 2.80, 5.60, '{"fiber": "3.5g", "protein": "7g", "complex_carbs": "73g"}'), -- Arroz integral
(7, 14, 3.0, 'lata', 3.50, 10.50, '{"protein": "26g", "omega3": "1.5g", "vitamin_d": "200IU"}'), -- Atún en conserva
(7, 15, 2.0, 'unidad', 2.20, 4.40, '{"protein": "17g", "calcium": "200mg", "probiotics": "true"}'); -- Yogur griego

-- =====================================================
-- CÓDIGOS QR DE EJEMPLO
-- =====================================================
INSERT INTO qrs (product_id, qr_code, url) VALUES
-- Productos de María
(1, 'QR_TOMATES_001', 'https://trazabilidad.com/producto/1'),
(2, 'QR_LECHUGA_001', 'https://trazabilidad.com/producto/2'),
(3, 'QR_MANZANAS_001', 'https://trazabilidad.com/producto/3'),

-- Productos de Carlos
(4, 'QR_PAN_001', 'https://trazabilidad.com/producto/4'),
(5, 'QR_LECHE_001', 'https://trazabilidad.com/producto/5'),

-- Productos de Pedro
(6, 'QR_HUEVOS_001', 'https://trazabilidad.com/producto/6'),
(7, 'QR_ZANAHORIAS_001', 'https://trazabilidad.com/producto/7'),
(8, 'QR_FRESAS_001', 'https://trazabilidad.com/producto/8'),

-- Productos de Ana Torres
(9, 'QR_ZANAHORIAS_ORG_001', 'https://trazabilidad.com/producto/9'),

-- Productos de Laura
(10, 'QR_QUESO_001', 'https://trazabilidad.com/producto/10'),
(11, 'QR_MIEL_001', 'https://trazabilidad.com/producto/11'),
(12, 'QR_ACEITE_001', 'https://trazabilidad.com/producto/12'),

-- Productos de Miguel
(13, 'QR_ARROZ_001', 'https://trazabilidad.com/producto/13'),
(14, 'QR_ATUN_001', 'https://trazabilidad.com/producto/14'),
(15, 'QR_YOGUR_001', 'https://trazabilidad.com/producto/15');

-- =====================================================
-- MÉTRICAS DE IMPACTO DE EJEMPLO
-- =====================================================
INSERT INTO impact_metrics (user_id, product_id, metric_type, value, unit) VALUES
-- Métricas de María (Huerta Ecológica)
(2, 1, 'co2_emissions', 0.5, 'kg CO2/kg'),
(2, 1, 'water_usage', 180, 'litros/kg'),
(2, 2, 'co2_emissions', 0.3, 'kg CO2/unidad'),
(2, 2, 'water_usage', 15, 'litros/unidad'),
(2, 3, 'co2_emissions', 0.4, 'kg CO2/kg'),
(2, 3, 'water_usage', 120, 'litros/kg'),

-- Métricas de Carlos (Supermercado)
(3, 4, 'co2_emissions', 0.8, 'kg CO2/unidad'),
(3, 4, 'transport_distance', 25, 'km'),
(3, 5, 'co2_emissions', 1.2, 'kg CO2/litro'),
(3, 5, 'transport_distance', 40, 'km'),

-- Métricas de Pedro (Huerta Pedro)
(6, 6, 'co2_emissions', 0.2, 'kg CO2/docena'),
(6, 6, 'water_usage', 150, 'litros/docena'),
(6, 7, 'co2_emissions', 0.1, 'kg CO2/kg'),
(6, 7, 'water_usage', 200, 'litros/kg'),
(6, 8, 'co2_emissions', 0.3, 'kg CO2/kg'),
(6, 8, 'water_usage', 300, 'litros/kg'),

-- Métricas de Ana Torres (Frutería)
(7, 9, 'co2_emissions', 0.15, 'kg CO2/kg'),
(7, 9, 'transport_distance', 15, 'km'),

-- Métricas de Laura (Mercado Local)
(9, 10, 'co2_emissions', 2.5, 'kg CO2/kg'),
(9, 10, 'transport_distance', 50, 'km'),
(9, 11, 'co2_emissions', 0.8, 'kg CO2/500g'),
(9, 11, 'transport_distance', 30, 'km'),
(9, 12, 'co2_emissions', 1.5, 'kg CO2/litro'),
(9, 12, 'transport_distance', 25, 'km'),

-- Métricas de Miguel (Supermercado Torres)
(10, 13, 'co2_emissions', 0.6, 'kg CO2/kg'),
(10, 13, 'transport_distance', 200, 'km'),
(10, 14, 'co2_emissions', 1.8, 'kg CO2/lata'),
(10, 14, 'sustainable_fishing_score', 85, 'points'),
(10, 15, 'co2_emissions', 0.9, 'kg CO2/unidad'),
(10, 15, 'transport_distance', 150, 'km');

-- =====================================================
-- LECTURAS DE SENSORES DE EJEMPLO
-- =====================================================
INSERT INTO sensor_readings (sensor_id, sensor_type, value, unit, location_lat, location_lon, metadata) VALUES
-- Sensores generales
('SENSOR_001', 'temperature', 22.5, '°C', 40.4168, -3.7038, '{"location": "general", "type": "ambient"}'),
('SENSOR_001', 'humidity', 65.0, '%', 40.4168, -3.7038, '{"location": "general", "type": "ambient"}'),
('SENSOR_002', 'temperature', 18.0, '°C', 40.4168, -3.7038, '{"location": "general", "type": "ambient"}'),
('SENSOR_002', 'humidity', 70.0, '%', 40.4168, -3.7038, '{"location": "general", "type": "ambient"}'),

-- Sensores de la granja de Pedro
('SENSOR_GRANJA_001', 'temperature', 24.0, '°C', 40.4168, -3.7038, '{"location": "invernadero", "crop": "tomates"}'),
('SENSOR_GRANJA_001', 'humidity', 75.0, '%', 40.4168, -3.7038, '{"location": "invernadero", "crop": "tomates"}'),
('SENSOR_GRANJA_002', 'soil_moisture', 65.0, '%', 40.4168, -3.7038, '{"location": "campo_zanahorias", "depth": "10cm"}'),
('SENSOR_GRANJA_003', 'light_intensity', 85000, 'lux', 40.4168, -3.7038, '{"location": "invernadero_fresas", "time": "12:00"}'),

-- Sensores del almacén de Laura
('SENSOR_ALMACEN_001', 'temperature', 4.0, '°C', 40.4168, -3.7038, '{"location": "camara_fria", "product": "queso"}'),
('SENSOR_ALMACEN_001', 'humidity', 85.0, '%', 40.4168, -3.7038, '{"location": "camara_fria", "product": "queso"}'),
('SENSOR_ALMACEN_002', 'temperature', 18.0, '°C', 40.4168, -3.7038, '{"location": "almacen_seco", "product": "miel"}'),

-- Sensores del supermercado de Miguel
('SENSOR_SUPER_001', 'temperature', 2.0, '°C', 40.4168, -3.7038, '{"location": "nevera_lacteos", "product": "yogur"}'),
('SENSOR_SUPER_002', 'temperature', 22.0, '°C', 40.4168, -3.7038, '{"location": "pasillo_seco", "product": "arroz"}');

-- =====================================================
-- LOGS DE BLOCKCHAIN DE EJEMPLO
-- =====================================================
INSERT INTO blockchain_logs (transaction_hash, block_number, product_id, action, data) VALUES
-- Creación de productos de María
('0x1234567890abcdef', 12345, 1, 'create', '{"product_name": "Tomates Ecológicos", "provider": "María García", "certifications": ["organic", "pesticide_free"]}'),
('0xabcdef1234567890', 12346, 2, 'create', '{"product_name": "Lechuga Fresca", "provider": "María García", "certifications": ["organic", "fresh"]}'),
('0x7890abcdef123456', 12347, 3, 'create', '{"product_name": "Manzanas Rojas", "provider": "María García", "certifications": ["organic", "sweet"]}'),

-- Creación de productos de Carlos
('0x1111111111111111', 12348, 4, 'create', '{"product_name": "Pan Integral", "provider": "Carlos López", "certifications": ["whole_grain", "fresh_baked"]}'),
('0x2222222222222222', 12349, 5, 'create', '{"product_name": "Leche Orgánica", "provider": "Carlos López", "certifications": ["organic", "grass_fed"]}'),

-- Creación de productos de Pedro
('0x3333333333333333', 12350, 6, 'create', '{"product_name": "Huevos camperos", "provider": "Pedro Sánchez", "certifications": ["free_range", "organic"]}'),
('0x4444444444444444', 12351, 7, 'create', '{"product_name": "Zanahorias", "provider": "Pedro Sánchez", "certifications": ["organic", "fresh"]}'),
('0x5555555555555555', 12352, 8, 'create', '{"product_name": "Fresas Ecológicas", "provider": "Pedro Sánchez", "certifications": ["organic", "seasonal"]}'),

-- Creación de productos de Ana Torres
('0x6666666666666666', 12353, 9, 'create', '{"product_name": "Zanahorias Orgánicas", "provider": "Ana Torres", "certifications": ["organic"]}'),

-- Creación de productos de Laura
('0x7777777777777777', 12354, 10, 'create', '{"product_name": "Queso Manchego", "provider": "Laura Fernández", "certifications": ["local", "artisan"]}'),
('0x8888888888888888', 12355, 11, 'create', '{"product_name": "Miel de Lavanda", "provider": "Laura Fernández", "certifications": ["local", "natural"]}'),
('0x9999999999999999', 12356, 12, 'create', '{"product_name": "Aceite de Oliva", "provider": "Laura Fernández", "certifications": ["local", "extra_virgin"]}'),

-- Creación de productos de Miguel
('0xaaaaaaaaaaaaaaaa', 12357, 13, 'create', '{"product_name": "Arroz Integral", "provider": "Miguel Torres", "certifications": ["whole_grain"]}'),
('0xbbbbbbbbbbbbbbbb', 12358, 14, 'create', '{"product_name": "Atún en Conserva", "provider": "Miguel Torres", "certifications": ["sustainable_fishing"]}'),
('0xcccccccccccccccc', 12359, 15, 'create', '{"product_name": "Yogur Griego", "provider": "Miguel Torres", "certifications": ["high_protein"]}'),

-- Transacciones de compra
('0xdddddddddddddddd', 12360, 6, 'transfer', '{"from": "Pedro Sánchez", "to": "Ana Martínez", "quantity": "1 docena", "price": "3.50 EUR", "transaction_id": "TXN_ANA_001"}'),
('0xeeeeeeeeeeeeeeee', 12361, 10, 'transfer', '{"from": "Laura Fernández", "to": "Ana Martínez", "quantity": "0.3 kg", "price": "2.55 EUR", "transaction_id": "TXN_ANA_001"}'),
('0xffffffffffffffff', 12362, 6, 'transfer', '{"from": "Pedro Sánchez", "to": "Carmen Ruiz", "quantity": "2 docenas", "price": "7.00 EUR", "transaction_id": "TXN_CARMEN_001"}'),
('0x1010101010101010', 12363, 11, 'transfer', '{"from": "Laura Fernández", "to": "Carmen Ruiz", "quantity": "2.5 kg", "price": "24.00 EUR", "transaction_id": "TXN_CARMEN_001"}'),
('0x2020202020202020', 12364, 13, 'transfer', '{"from": "Miguel Torres", "to": "Juan Pérez", "quantity": "2 kg", "price": "5.60 EUR", "transaction_id": "TXN_JUAN_002"}'),
('0x3030303030303030', 12365, 14, 'transfer', '{"from": "Miguel Torres", "to": "Juan Pérez", "quantity": "3 latas", "price": "10.50 EUR", "transaction_id": "TXN_JUAN_002"}'),
('0x4040404040404040', 12366, 15, 'transfer', '{"from": "Miguel Torres", "to": "Juan Pérez", "quantity": "2 unidades", "price": "4.40 EUR", "transaction_id": "TXN_JUAN_002"}'),

-- Actualizaciones de stock
('0x5050505050505050', 12367, 6, 'update', '{"action": "stock_reduction", "previous_stock": 15, "new_stock": 12, "reason": "sale", "transaction_id": "TXN_ANA_001"}'),
('0x6060606060606060', 12368, 10, 'update', '{"action": "stock_reduction", "previous_stock": 8, "new_stock": 7.7, "reason": "sale", "transaction_id": "TXN_ANA_001"}'),
('0x7070707070707070', 12369, 6, 'update', '{"action": "stock_reduction", "previous_stock": 12, "new_stock": 10, "reason": "sale", "transaction_id": "TXN_CARMEN_001"}'),
('0x8080808080808080', 12370, 11, 'update', '{"action": "stock_reduction", "previous_stock": 10, "new_stock": 7.5, "reason": "sale", "transaction_id": "TXN_CARMEN_001"}'),
('0x9090909090909090', 12371, 13, 'update', '{"action": "stock_reduction", "previous_stock": 50, "new_stock": 48, "reason": "sale", "transaction_id": "TXN_JUAN_002"}'),
('0xa0a0a0a0a0a0a0a0', 12372, 14, 'update', '{"action": "stock_reduction", "previous_stock": 30, "new_stock": 27, "reason": "sale", "transaction_id": "TXN_JUAN_002"}'),
('0xb0b0b0b0b0b0b0b0', 12373, 15, 'update', '{"action": "stock_reduction", "previous_stock": 25, "new_stock": 23, "reason": "sale", "transaction_id": "TXN_JUAN_002"}');


-- =====================================================
-- TRANSACCIONES
-- =====================================================
INSERT INTO transactions (shopping_list_id, user_id, amount, currency, status, payment_method, transaction_hash) VALUES
-- Transacción de Ana Martínez (pending)
(2, 8, 25.50, 'EUR', 'pending', 'credit_card', 'TXN_ANA_001'),

-- Transacción de Carmen (completed)
(3, 11, 45.20, 'EUR', 'completed', 'paypal', 'TXN_CARMEN_001'),

-- Transacción de Juan (completed - lista entregada)
(4, 1, 20.50, 'EUR', 'completed', 'bank_transfer', 'TXN_JUAN_002');
