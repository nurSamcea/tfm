from typing import Dict, List, Optional
from dataclasses import dataclass
import math


@dataclass
class ProductImpact:
    co2_saved_kg: float
    local_support_eur: float
    waste_prevented_kg: float
    sustainability_score: float


class ImpactCalculator:
    # Factores de impacto por categoría de producto (kg CO2 por kg de producto)
    CO2_FACTORS = {
        "frutas": 0.5,  # kg CO2 por kg de producto
        "verduras": 0.4,
        "carnes": 2.5,
        "pescados": 1.8,
        "lacteos": 1.2,
        "cereales": 0.3,
        "legumbres": 0.2,
        "otros": 0.8
    }

    # Porcentaje de apoyo local por tipo de proveedor
    LOCAL_SUPPORT_FACTORS = {
        "farmer": 1.0,      # 100% del valor va al productor local
        "supermarket": 0.6, # 60% del valor se queda en la economía local
        "consumer": 0.0     # 0% del valor se queda en la economía local
    }

    # Factores de prevención de desperdicio por tipo de producto
    WASTE_PREVENTION_FACTORS = {
        "frutas": 0.15,  # 15% del peso se evita desperdiciar
        "verduras": 0.15,
        "carnes": 0.05,
        "pescados": 0.05,
        "lacteos": 0.10,
        "cereales": 0.02,
        "legumbres": 0.02,
        "otros": 0.08
    }

    @staticmethod
    def calculate_product_impact(
        product: Dict,
        quantity: float,
        distance_km: float,
        provider_type: str
    ) -> ProductImpact:
        """
        Calcula el impacto de un producto específico.
        
        Args:
            product: Diccionario con información del producto
            quantity: Cantidad en kg
            distance_km: Distancia de transporte en km
            provider_type: Tipo de proveedor
            
        Returns:
            ProductImpact con las métricas calculadas
        """
        # Obtener categoría del producto o usar "otros" por defecto
        category = product.get("category", "otros").lower()
        
        # Calcular CO2 ahorrado
        co2_factor = ImpactCalculator.CO2_FACTORS.get(category, ImpactCalculator.CO2_FACTORS["otros"])
        transport_co2 = distance_km * 0.1  # 0.1 kg CO2 por km
        co2_saved = (co2_factor * quantity) - transport_co2
        
        # Calcular apoyo local
        local_factor = ImpactCalculator.LOCAL_SUPPORT_FACTORS.get(
            provider_type, 
            ImpactCalculator.LOCAL_SUPPORT_FACTORS["farmer"]
        )
        local_support = product["price"] * quantity * local_factor
        
        # Calcular desperdicio prevenido
        waste_factor = ImpactCalculator.WASTE_PREVENTION_FACTORS.get(
            category, 
            ImpactCalculator.WASTE_PREVENTION_FACTORS["otros"]
        )
        waste_prevented = quantity * waste_factor
        
        # Calcular puntuación de sostenibilidad (0-100)
        sustainability_score = ImpactCalculator._calculate_sustainability_score(
            co2_saved=co2_saved,
            local_support=local_support,
            waste_prevented=waste_prevented,
            distance_km=distance_km,
            price=product["price"]
        )
        
        return ProductImpact(
            co2_saved_kg=max(0, co2_saved),
            local_support_eur=round(local_support, 2),
            waste_prevented_kg=round(waste_prevented, 2),
            sustainability_score=round(sustainability_score, 1)
        )

    @staticmethod
    def _calculate_sustainability_score(
        co2_saved: float,
        local_support: float,
        waste_prevented: float,
        distance_km: float,
        price: float
    ) -> float:
        """
        Calcula una puntuación de sostenibilidad de 0 a 100.
        """
        # Pesos para cada factor
        weights = {
            "co2": 0.4,
            "local": 0.3,
            "waste": 0.2,
            "distance": 0.1
        }
        
        # Normalizar cada factor
        co2_score = min(100, (co2_saved / 10) * 100)  # 10kg CO2 = 100 puntos
        local_score = min(100, (local_support / price) * 100)  # 100% local = 100 puntos
        waste_score = min(100, (waste_prevented / 2) * 100)  # 2kg prevenidos = 100 puntos
        distance_score = max(0, 100 - (distance_km / 2))  # 0km = 100 puntos, 200km = 0 puntos
        
        # Calcular puntuación final
        final_score = (
            weights["co2"] * co2_score +
            weights["local"] * local_score +
            weights["waste"] * waste_score +
            weights["distance"] * distance_score
        )
        
        return max(0, min(100, final_score))

    @staticmethod
    def calculate_basket_impact(
        basket: List[Dict],
        provider_types: Dict[int, str]
    ) -> Dict:
        """
        Calcula el impacto total de una cesta de compra.
        
        Args:
            basket: Lista de productos en la cesta
            provider_types: Diccionario con el tipo de proveedor por ID
            
        Returns:
            Diccionario con las métricas totales y por producto
        """
        total_impact = ProductImpact(0, 0, 0, 0)
        product_impacts = []
        
        for item in basket:
            provider_type = provider_types.get(item["provider_id"], "farmer")
            impact = ImpactCalculator.calculate_product_impact(
                product=item,
                quantity=item["quantity"],
                distance_km=item["distance_km"],
                provider_type=provider_type
            )
            
            total_impact.co2_saved_kg += impact.co2_saved_kg
            total_impact.local_support_eur += impact.local_support_eur
            total_impact.waste_prevented_kg += impact.waste_prevented_kg
            
            product_impacts.append({
                "product_id": item["product_id"],
                "product_name": item["product_name"],
                "impact": {
                    "co2_saved_kg": round(impact.co2_saved_kg, 2),
                    "local_support_eur": round(impact.local_support_eur, 2),
                    "waste_prevented_kg": round(impact.waste_prevented_kg, 2),
                    "sustainability_score": impact.sustainability_score
                }
            })
        
        # Calcular puntuación promedio de sostenibilidad
        avg_sustainability = sum(p["impact"]["sustainability_score"] for p in product_impacts) / len(product_impacts) if product_impacts else 0
        
        return {
            "total_impact": {
                "co2_saved_kg": round(total_impact.co2_saved_kg, 2),
                "local_support_eur": round(total_impact.local_support_eur, 2),
                "waste_prevented_kg": round(total_impact.waste_prevented_kg, 2),
                "average_sustainability_score": round(avg_sustainability, 1)
            },
            "product_impacts": product_impacts
        } 