
-- USUARIOS (8 en total)
INSERT INTO users (name, email, password, role, entity_name, location, preferences) VALUES
('Laura García', 'laura@example.com', 'hashedpass1', 'consumer', NULL, '{"lat": 40.416, "lon": -3.703, "address": "Madrid"}', '{"vegetarian": true}'),
('Carlos Rivera', 'carlos@example.com', 'hashedpass2', 'consumer', NULL, '{"lat": 40.420, "lon": -3.710}', '{"gluten_free": true}'),
('Finca Las Rosas', 'finca@example.com', 'hashedpass3', 'farmer', 'Finca Las Rosas', '{"lat": 40.1, "lon": -3.5}', '{"certified": true}'),
('Granja El Sol', 'granja@example.com', 'hashedpass4', 'farmer', 'Granja El Sol', '{"lat": 40.15, "lon": -3.55}', '{}'),
('DIA Chamartín', 'dia@example.com', 'hashedpass5', 'retailer', 'DIA Chamartín', '{"lat": 40.45, "lon": -3.68}', '{}'),
('Mercadona Hortaleza', 'merca@example.com', 'hashedpass6', 'retailer', 'Mercadona Hortaleza', '{"lat": 40.47, "lon": -3.67}', '{}'),
('Restaurante Verde', 'verde@example.com', 'hashedpass7', 'restaurant', 'Restaurante Verde', '{"lat": 40.42, "lon": -3.70}', '{"eco_friendly": true}'),
('Veggie Bar', 'veggie@example.com', 'hashedpass8', 'restaurant', 'Veggie Bar', '{"lat": 40.43, "lon": -3.72}', '{"vegan": true}');

-- TEMPLATES (4)
INSERT INTO product_templates (name, category, subcategory, default_image_url, nutrition, allergens, labels) VALUES
('Tomate', 'Frutas y Verduras', 'Hortalizas', 'https://cdn.example.com/img/tomate.jpg', '{"kcal": 18}', '{}', '{"eco","km0"}'),
('Leche de avena', 'Bebidas', 'Vegetal', 'https://cdn.example.com/img/avena.jpg', '{"kcal": 45}', '{"gluten"}', '{"vegano"}'),
('Pan integral', 'Panadería', 'Integral', 'https://cdn.example.com/img/pan.jpg', '{"kcal": 250}', '{"gluten"}', '{"artesanal"}'),
('Zanahoria', 'Frutas y Verduras', 'Raíces', 'https://cdn.example.com/img/zanahoria.jpg', '{"kcal": 41}', '{}', '{"eco"}');

-- PRODUCTOS (6)
INSERT INTO products (name, description, price, stock, unit, expiration_date, image_url, is_local, owner_id, template_id) VALUES
('Tomate ecológico', 'Cultivo sin pesticidas', 2.30, 100, 'kg', '2025-06-01', 'https://cdn.example.com/img/tomate.jpg', true, 3, 1),
('Leche de avena BIO', 'Bebida vegetal ecológica', 1.85, 50, 'litro', '2025-07-15', 'https://cdn.example.com/img/avena.jpg', true, 5, 2),
('Pan integral artesano', 'Hecho en horno de leña', 1.20, 80, 'unidad', '2025-06-05', 'https://cdn.example.com/img/pan.jpg', false, 6, 3),
('Zanahorias frescas', 'Directas del campo', 1.10, 90, 'kg', '2025-06-03', 'https://cdn.example.com/img/zanahoria.jpg', true, 4, 4),
('Leche de avena DIA', 'Marca blanca DIA', 1.55, 100, 'litro', '2025-07-20', 'https://cdn.example.com/img/avena.jpg', false, 5, 2),
('Tomate Mercadona', 'Económico y fresco', 2.00, 120, 'kg', '2025-06-02', 'https://cdn.example.com/img/tomate.jpg', false, 6, 1);

-- LISTAS DE COMPRA (2 listas, 4 items)
INSERT INTO shopping_lists (user_id) VALUES (1), (2);
INSERT INTO shopping_list_items (shopping_list_id, product_id, quantity, status) VALUES
(1, 1, 2, 'pending'),
(1, 2, 1, 'pending'),
(2, 4, 2, 'pending'),
(2, 5, 1, 'pending');

-- RECETAS (2 recetas)
INSERT INTO recipes (name, description, image_url, is_vegan, created_by) VALUES
('Ensalada fresca', 'Con tomate y zanahoria', 'https://cdn.example.com/img/ensalada.jpg', true, 1),
('Sándwich vegano', 'Pan integral y leche de avena', 'https://cdn.example.com/img/sandwich.jpg', true, 2);

INSERT INTO recipe_ingredients (recipe_id, name, quantity) VALUES
(1, 'Tomate ecológico', '150g'),
(1, 'Zanahorias frescas', '100g'),
(2, 'Pan integral artesano', '2 unidades'),
(2, 'Leche de avena BIO', '50ml');

-- PLANES SEMANALES (2)
INSERT INTO weekly_plans (user_id, week_start) VALUES (1, '2025-06-03'), (2, '2025-06-03');
INSERT INTO weekly_plan_items (plan_id, recipe_id, day, meal) VALUES
(1, 1, 'monday', 'lunch'),
(1, 2, 'tuesday', 'dinner'),
(2, 1, 'wednesday', 'lunch');

-- QRs (3)
INSERT INTO product_qrs (product_id, blockchain_hash, qr_code, origin, collected_at, certification, temperature_log) VALUES
(1, 'abc123', 'QR001', 'Segovia', '2025-05-10', 'EcoCert', '[{"temp":7,"time":"2025-05-12T09:00:00"}]'),
(4, 'def456', 'QR002', 'Toledo', '2025-05-11', NULL, '[{"temp":8,"time":"2025-05-12T10:00:00"}]'),
(3, 'ghi789', 'QR003', 'Madrid', '2025-05-12', NULL, '[]');

-- TRANSACCIONES (2)
INSERT INTO transactions (buyer_id, seller_id, total_price, payment_method, status, delivery_method, delivery_estimated) VALUES
(1, 3, 4.60, 'card', 'paid', 'delivery', '2025-06-05 13:00:00'),
(2, 4, 2.20, 'cash', 'paid', 'pickup', '2025-06-06 10:00:00');

INSERT INTO transaction_items (transaction_id, product_id, quantity, unit_price, qr_id) VALUES
(1, 1, 2, 2.30, 1),
(2, 4, 2, 1.10, 2);

-- IMPACTO (2 usuarios)
INSERT INTO impact_metrics (user_id, co2_saved, km_saved, local_support_pct, zero_waste_score) VALUES
(1, 1.4, 5.8, 90, 8.5),
(2, 0.8, 3.2, 80, 7.9);
