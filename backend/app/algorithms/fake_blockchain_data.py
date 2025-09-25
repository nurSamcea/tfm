"""
Generador de datos falsos para la trazabilidad blockchain
Este módulo genera datos realistas para simular la trazabilidad de productos
"""

from typing import Dict, List, Optional, Tuple, Any
from datetime import datetime, timedelta
import random
import math


class FakeProductData:
    """Clase para almacenar datos falsos de un producto"""
    
    def __init__(self):
        self.distance_to_consumer_km = 0.0
        self.time_since_publication_hours = 0.0
        self.sustainability_score = 0.0
        self.quality_score = 0.0
        self.freshness_score = 0.0
        self.local_sourcing_score = 0.0
        self.packaging_score = 0.0
        self.carbon_footprint_kg = 0.0
        self.water_usage_liters = 0.0


class FakeBlockchainDataGenerator:
    """Generador de datos falsos para la trazabilidad blockchain"""
    
    def __init__(self):
        self.random_seed = 42
        random.seed(self.random_seed)
    
    def generate_fake_product_data(
        self,
        product_category: str,
        product_id: int,
        consumer_location: Optional[Tuple[float, float]] = None,
        farmer_location: Optional[Tuple[float, float]] = None,
        publication_date: Optional[datetime] = None
    ) -> FakeProductData:
        """
        Genera datos falsos para un producto específico
        """
        # Usar el ID del producto como semilla para consistencia
        random.seed(product_id)
        
        fake_data = FakeProductData()
        
        # Calcular distancia si tenemos ambas ubicaciones
        if consumer_location and farmer_location:
            fake_data.distance_to_consumer_km = self._calculate_distance(
                farmer_location[0], farmer_location[1],
                consumer_location[0], consumer_location[1]
            )
        else:
            # Generar distancia aleatoria realista
            fake_data.distance_to_consumer_km = 15.0 + random.uniform(0, 50.0)
        
        # Calcular tiempo desde publicación
        if publication_date:
            time_diff = datetime.utcnow() - publication_date
            fake_data.time_since_publication_hours = time_diff.total_seconds() / 3600
        else:
            fake_data.time_since_publication_hours = random.uniform(2.0, 168.0)  # 2 horas a 1 semana
        
        # Generar puntuaciones basadas en la categoría del producto
        category_scores = self._get_category_scores(product_category)
        
        # Puntuación de sostenibilidad (0-10)
        fake_data.sustainability_score = random.uniform(
            category_scores["sustainability_min"],
            category_scores["sustainability_max"]
        )
        
        # Puntuación de calidad (0-10)
        fake_data.quality_score = random.uniform(
            category_scores["quality_min"],
            category_scores["quality_max"]
        )
        
        # Puntuación de frescura (0-10)
        fake_data.freshness_score = random.uniform(
            category_scores["freshness_min"],
            category_scores["freshness_max"]
        )
        
        # Puntuación de abastecimiento local (0-10)
        if fake_data.distance_to_consumer_km < 50:
            fake_data.local_sourcing_score = random.uniform(7.0, 10.0)
        elif fake_data.distance_to_consumer_km < 200:
            fake_data.local_sourcing_score = random.uniform(4.0, 7.0)
        else:
            fake_data.local_sourcing_score = random.uniform(1.0, 4.0)
        
        # Puntuación de embalaje (0-10)
        fake_data.packaging_score = random.uniform(6.0, 9.5)
        
        # Huella de carbono (kg CO2)
        base_carbon = category_scores["base_carbon"]
        distance_factor = fake_data.distance_to_consumer_km * 0.05  # 0.05 kg CO2 por km
        fake_data.carbon_footprint_kg = base_carbon + distance_factor
        
        # Uso de agua (litros)
        fake_data.water_usage_liters = random.uniform(
            category_scores["water_min"],
            category_scores["water_max"]
        )
        
        return fake_data
    
    def generate_fake_sustainability_metrics(
        self,
        product_id: int,
        product_category: str
    ) -> Dict[str, Any]:
        """
        Genera métricas falsas de sostenibilidad
        """
        random.seed(product_id + 100)  # Diferente semilla para variedad
        
        # Generar certificaciones aleatorias
        all_certifications = [
            "Certificación Orgánica",
            "Fair Trade",
            "Rainforest Alliance",
            "UTZ Certified",
            "GlobalG.A.P.",
            "ISO 14001",
            "Carbon Trust",
            "B-Corp"
        ]
        
        num_certifications = random.randint(1, 4)
        certifications = random.sample(all_certifications, num_certifications)
        
        # Generar prácticas ecológicas
        all_practices = [
            "Agricultura regenerativa",
            "Uso de energías renovables",
            "Reducción de pesticidas químicos",
            "Conservación del agua",
            "Biodiversidad protegida",
            "Comercio justo",
            "Embalaje biodegradable",
            "Transporte eficiente"
        ]
        
        num_practices = random.randint(3, 6)
        eco_practices = random.sample(all_practices, num_practices)
        
        # Puntuación general de sostenibilidad
        overall_score = random.uniform(6.5, 9.2)
        
        return {
            "overall_sustainability_score": round(overall_score, 1),
            "certifications": certifications,
            "eco_friendly_practices": eco_practices,
            "carbon_neutral": random.choice([True, False]),
            "renewable_energy_percentage": random.uniform(60.0, 95.0),
            "waste_reduction_percentage": random.uniform(70.0, 90.0)
        }
    
    def generate_fake_quality_metrics(
        self,
        product_id: int,
        product_category: str
    ) -> Dict[str, Any]:
        """
        Genera métricas falsas de calidad
        """
        random.seed(product_id + 200)  # Diferente semilla para variedad
        
        # Indicadores de frescura
        freshness_indicators = {
            "color_vibrant": random.choice([True, False]),
            "firm_texture": random.choice([True, False]),
            "natural_aroma": random.choice([True, False]),
            "no_bruising": random.choice([True, False]),
            "proper_size": random.choice([True, False])
        }
        
        # Estándares de seguridad
        safety_standards = {
            "pesticide_free": random.choice([True, False]),
            "pathogen_tested": True,  # Siempre positivo
            "heavy_metal_safe": True,  # Siempre positivo
            "allergen_free": random.choice([True, False]),
            "temperature_controlled": random.choice([True, False])
        }
        
        # Calidad nutricional
        nutritional_quality = {
            "vitamin_content": random.uniform(70.0, 95.0),
            "mineral_content": random.uniform(75.0, 90.0),
            "antioxidant_level": random.uniform(60.0, 85.0),
            "fiber_content": random.uniform(65.0, 88.0),
            "natural_sugars": random.uniform(80.0, 95.0)
        }
        
        # Puntuación general de calidad
        overall_score = random.uniform(7.0, 9.5)
        
        return {
            "overall_quality_score": round(overall_score, 1),
            "freshness_indicators": freshness_indicators,
            "safety_standards": safety_standards,
            "nutritional_quality": nutritional_quality,
            "storage_conditions": "Óptimas",
            "quality_grade": random.choice(["A", "A+", "Premium"])
        }
    
    def generate_fake_supply_chain_events(
        self,
        product_id: int,
        product_category: str,
        farmer_location: Tuple[float, float],
        consumer_location: Tuple[float, float]
    ) -> List[Dict[str, Any]]:
        """
        Genera eventos falsos de la cadena de suministro
        """
        random.seed(product_id + 300)  # Diferente semilla para variedad
        
        events = []
        
        # Evento 1: Producción/Cosecha
        harvest_date = datetime.utcnow() - timedelta(days=random.randint(1, 7))
        events.append({
            "event_type": "HARVEST",
            "timestamp": harvest_date.isoformat(),
            "location": {
                "lat": farmer_location[0],
                "lon": farmer_location[1],
                "description": "Campo de cultivo"
            },
            "actor": {
                "type": "farmer",
                "name": "Productor Local"
            },
            "details": {
                "harvest_method": random.choice(["Manual", "Mecánica", "Mixta"]),
                "weather_conditions": random.choice(["Óptimas", "Buenas", "Aceptables"]),
                "quality_grade": random.choice(["A", "A+", "Premium"])
            }
        })
        
        # Evento 2: Transporte inicial
        transport_date = harvest_date + timedelta(hours=random.randint(2, 12))
        mid_point = self._get_midpoint(farmer_location, consumer_location)
        events.append({
            "event_type": "TRANSPORT",
            "timestamp": transport_date.isoformat(),
            "location": {
                "lat": mid_point[0],
                "lon": mid_point[1],
                "description": "En tránsito"
            },
            "actor": {
                "type": "logistics",
                "name": "Transporte Local"
            },
            "details": {
                "vehicle_type": random.choice(["Camión refrigerado", "Furgoneta", "Vehículo eléctrico"]),
                "temperature_controlled": True,
                "estimated_arrival": (transport_date + timedelta(hours=random.randint(4, 8))).isoformat()
            }
        })
        
        # Evento 3: Almacenamiento
        storage_date = transport_date + timedelta(hours=random.randint(4, 8))
        events.append({
            "event_type": "STORAGE",
            "timestamp": storage_date.isoformat(),
            "location": {
                "lat": consumer_location[0] + random.uniform(-0.01, 0.01),
                "lon": consumer_location[1] + random.uniform(-0.01, 0.01),
                "description": "Centro de distribución"
            },
            "actor": {
                "type": "warehouse",
                "name": "Almacén Local"
            },
            "details": {
                "storage_conditions": "Refrigeración controlada",
                "temperature": f"{random.uniform(2, 8):.1f}°C",
                "humidity": f"{random.uniform(60, 80):.1f}%",
                "duration_hours": random.randint(12, 48)
            }
        })
        
        # Evento 4: Venta
        sale_date = storage_date + timedelta(hours=random.randint(12, 48))
        events.append({
            "event_type": "SALE",
            "timestamp": sale_date.isoformat(),
            "location": {
                "lat": consumer_location[0],
                "lon": consumer_location[1],
                "description": "Punto de venta"
            },
            "actor": {
                "type": "retailer",
                "name": "Supermercado Local"
            },
            "details": {
                "sale_price": round(random.uniform(2.5, 8.5), 2),
                "currency": "EUR",
                "payment_method": random.choice(["Efectivo", "Tarjeta", "Digital"]),
                "receipt_number": f"RCP-{random.randint(100000, 999999)}"
            }
        })
        
        return events
    
    def _get_category_scores(self, category: str) -> Dict[str, float]:
        """
        Obtiene puntuaciones base según la categoría del producto
        """
        category_scores = {
            "frutas": {
                "sustainability_min": 7.0,
                "sustainability_max": 9.0,
                "quality_min": 7.5,
                "quality_max": 9.5,
                "freshness_min": 6.0,
                "freshness_max": 9.0,
                "base_carbon": 0.8,
                "water_min": 100.0,
                "water_max": 300.0
            },
            "verduras": {
                "sustainability_min": 7.5,
                "sustainability_max": 9.2,
                "quality_min": 7.0,
                "quality_max": 9.0,
                "freshness_min": 6.5,
                "freshness_max": 9.2,
                "base_carbon": 0.6,
                "water_min": 80.0,
                "water_max": 250.0
            },
            "lacteos": {
                "sustainability_min": 6.0,
                "sustainability_max": 8.5,
                "quality_min": 8.0,
                "quality_max": 9.5,
                "freshness_min": 7.0,
                "freshness_max": 9.0,
                "base_carbon": 1.2,
                "water_min": 200.0,
                "water_max": 400.0
            },
            "carnes": {
                "sustainability_min": 5.0,
                "sustainability_max": 7.5,
                "quality_min": 8.5,
                "quality_max": 9.8,
                "freshness_min": 8.0,
                "freshness_max": 9.5,
                "base_carbon": 2.5,
                "water_min": 500.0,
                "water_max": 800.0
            },
            "cereales": {
                "sustainability_min": 7.0,
                "sustainability_max": 8.8,
                "quality_min": 7.5,
                "quality_max": 9.2,
                "freshness_min": 8.5,
                "freshness_max": 9.8,
                "base_carbon": 0.4,
                "water_min": 50.0,
                "water_max": 150.0
            }
        }
        
        # Valores por defecto si la categoría no está definida
        default_scores = {
            "sustainability_min": 6.5,
            "sustainability_max": 8.5,
            "quality_min": 7.0,
            "quality_max": 9.0,
            "freshness_min": 6.5,
            "freshness_max": 8.8,
            "base_carbon": 1.0,
            "water_min": 100.0,
            "water_max": 300.0
        }
        
        return category_scores.get(category.lower(), default_scores)
    
    def _calculate_distance(self, lat1: float, lon1: float, lat2: float, lon2: float) -> float:
        """
        Calcula la distancia entre dos puntos geográficos en kilómetros
        """
        R = 6371  # Radio de la Tierra en kilómetros
        
        dlat = math.radians(lat2 - lat1)
        dlon = math.radians(lon2 - lon1)
        
        a = (math.sin(dlat/2) * math.sin(dlat/2) +
             math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) *
             math.sin(dlon/2) * math.sin(dlon/2))
        
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        distance = R * c
        
        return distance
    
    def _get_midpoint(self, point1: Tuple[float, float], point2: Tuple[float, float]) -> Tuple[float, float]:
        """
        Calcula el punto medio entre dos coordenadas
        """
        lat = (point1[0] + point2[0]) / 2
        lon = (point1[1] + point2[1]) / 2
        return (lat, lon)
