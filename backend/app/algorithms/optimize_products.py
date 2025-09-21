from typing import List, Dict, Tuple, Optional
from geopy.distance import geodesic
from collections import defaultdict
import math


def apply_filters(product: Dict, filters: Dict, user_location: Tuple[float, float]) -> bool:
    if filters.get("eco") and not product.get("is_eco", False):
        return False
    if filters.get("gluten_free") and not product.get("is_gluten_free", False):
        return False
    if "max_distance_km" in filters:
        distance = geodesic(user_location, (product["provider_lat"], product["provider_lon"])).km
        if distance > filters["max_distance_km"]:
            return False
    return True


def find_alternative_products(product: Dict, available_products: List[Dict], max_price_diff_percent: float = 20) -> List[Dict]:
    """Encuentra productos alternativos similares al producto dado."""
    alternatives = []
    base_price = product["price"]
    
    for p in available_products:
        if p["id"] == product["id"]:
            continue
            
        # Calcular similitud basada en nombre y categoría
        name_similarity = calculate_name_similarity(product["name"], p["name"])
        category_similarity = 1.0 if product.get("category") == p.get("category") else 0.0
        
        # Verificar diferencia de precio
        price_diff_percent = abs(p["price"] - base_price) / base_price * 100
        
        if (name_similarity > 0.5 or category_similarity > 0) and price_diff_percent <= max_price_diff_percent:
            alternatives.append({
                **p,
                "similarity_score": (name_similarity + category_similarity) / 2,
                "price_diff_percent": price_diff_percent
            })
    
    return sorted(alternatives, key=lambda x: (-x["similarity_score"], x["price_diff_percent"]))


def calculate_name_similarity(name1: str, name2: str) -> float:
    """Calcula la similitud entre dos nombres de productos."""
    words1 = set(name1.lower().split())
    words2 = set(name2.lower().split())
    
    if not words1 or not words2:
        return 0.0
        
    intersection = words1.intersection(words2)
    union = words1.union(words2)
    
    return len(intersection) / len(union)


def group_by_provider(basket: List[Dict]) -> Dict[int, List[Dict]]:
    """Agrupa los productos por proveedor."""
    grouped = defaultdict(list)
    for item in basket:
        grouped[item["provider_id"]].append(item)
    return dict(grouped)


def calculate_route_efficiency(provider_groups: Dict[int, List[Dict]], user_location: Tuple[float, float]) -> float:
    """Calcula la eficiencia de la ruta considerando la distancia y el número de paradas."""
    total_distance = 0
    for provider_id, items in provider_groups.items():
        provider_location = (items[0]["provider_lat"], items[0]["provider_lon"])
        distance = geodesic(user_location, provider_location).km
        total_distance += distance * len(items)  # Penalización por número de paradas
    
    return total_distance


def evaluate_basket(basket: List[Dict], criteria: Dict) -> float:
    total_price = sum(item["price"] * item["quantity"] for item in basket)
    total_distance = sum(item["distance_km"] for item in basket)
    unique_providers = set(item["provider_id"] for item in basket)
    
    # Agrupar por proveedor para calcular eficiencia de ruta
    provider_groups = group_by_provider(basket)
    route_efficiency = calculate_route_efficiency(provider_groups, (basket[0]["user_lat"], basket[0]["user_lon"]))
    
    total_cost = (
        criteria["price_weight"] * total_price +
        criteria["distance_weight"] * total_distance +
        criteria["provider_weight"] * len(unique_providers) +
        criteria.get("route_efficiency_weight", 0.1) * route_efficiency
    )
    return total_cost


def calculate_product_score(
    product: Dict, 
    user_location: Optional[Tuple[float, float]] = None,
    weights: Optional[Dict[str, float]] = None,
    filters: Optional[Dict[str, bool]] = None
) -> float:
    """
    Calcula la puntuación de un producto basada en múltiples criterios.
    Puntuación más alta = mejor producto.
    
    Variables consideradas:
    - Precio: menor precio = mayor puntuación
    - Distancia: menor distancia = mayor puntuación
    - Sostenibilidad/Huella de carbono: menor impacto ambiental = mayor puntuación
    """
    if weights is None:
        weights = {
            "price": 0.5,
            "distance": 0.3,
            "sustainability": 0.2
        }
    
    score = 0.0
    
    # 1. Puntuación por precio (más bajo = mejor)
    # Normalizar precio entre 0-100 (asumiendo rango 0-10€)
    price_score = max(0, 100 - (product.get("price", 0) * 10))
    score += weights.get("price", 0.3) * price_score
    
    # 2. Puntuación por distancia (más cerca = mejor)
    distance_km = None
    if user_location and product.get("distance_km") is not None:
        distance_km = product["distance_km"]
    elif user_location and product.get("provider_lat") and product.get("provider_lon"):
        # Calcular distancia si no está precalculada
        try:
            distance_km = geodesic(
                user_location, 
                (product["provider_lat"], product["provider_lon"])
            ).km
        except:
            distance_km = None
    
    if distance_km is not None:
        distance_score = max(0, 100 - (distance_km * 2))  # Penalizar distancia
        score += weights.get("distance", 0.25) * distance_score
    
    # 3. Puntuación por sostenibilidad/huella de carbono
    sustainability_score = calculate_sustainability_score(product, distance_km)
    score += weights.get("sustainability", 0.2) * sustainability_score
    
    # 4. Bonus por filtros activos
    if filters:
        if filters.get("eco") and product.get("is_eco", False):
            score += 20  # Bonus extra por cumplir filtro ecológico
        if filters.get("gluten_free") and product.get("is_gluten_free", False):
            score += 15  # Bonus por ser sin gluten
    
    return round(score, 2)


def calculate_sustainability_score(product: Dict, distance_km: Optional[float] = None) -> float:
    """
    Calcula la puntuación de sostenibilidad/huella de carbono de un producto.
    
    Considera:
    - Score base del producto (0-100)
    - Impacto de la distancia de transporte
    - Tipo de producto y su impacto ambiental
    - Certificaciones ecológicas
    """
    base_score = product.get("score", 0) or 0
    
    # Factores de huella de carbono por categoría de producto (kg CO2 por kg)
    co2_factors = {
        "frutas": 0.5,
        "verduras": 0.4,
        "carnes": 2.5,
        "pescados": 1.8,
        "lacteos": 1.2,
        "cereales": 0.3,
        "legumbres": 0.2,
        "otros": 0.8
    }
    
    # Obtener categoría del producto
    category = product.get("category", "otros")
    if hasattr(category, 'value'):
        category = category.value
    category = str(category).lower()
    
    # Factor de CO2 del producto
    co2_factor = co2_factors.get(category, co2_factors["otros"])
    
    # Penalización por distancia de transporte (0.1 kg CO2 por km)
    transport_penalty = 0
    if distance_km is not None:
        transport_co2 = distance_km * 0.1
        # Penalizar productos que vienen de lejos (máximo 10 kg CO2 de transporte = -20 puntos)
        transport_penalty = min(20, transport_co2 * 2)
    
    # Calcular puntuación final de sostenibilidad
    sustainability_score = base_score - transport_penalty
    
    # Asegurar que esté en el rango 0-100
    return max(0, min(100, sustainability_score))


def sort_products_by_priority(
    products: List[Dict],
    user_location: Optional[Tuple[float, float]] = None,
    weights: Optional[Dict[str, float]] = None,
    filters: Optional[Dict[str, bool]] = None,
    sort_criteria: Optional[str] = None
) -> List[Dict]:
    """
    Ordena productos según criterios de prioridad.
    
    Args:
        products: Lista de productos a ordenar
        user_location: Ubicación del usuario (lat, lon)
        weights: Pesos para cada criterio
        filters: Filtros activos
        sort_criteria: Criterio específico de ordenación ("price", "distance", "sustainability", "optimal")
    """
    if not products:
        return products
    
    # Si no hay criterio específico, usar algoritmo óptimo
    if not sort_criteria or sort_criteria == "optimal":
        # Calcular puntuación para cada producto
        for product in products:
            product["optimization_score"] = calculate_product_score(
                product, user_location, weights, filters
            )
        
        # Ordenar por puntuación descendente
        return sorted(products, key=lambda p: p["optimization_score"], reverse=True)
    
    # Ordenación por criterio específico
    if sort_criteria == "price":
        # Ordenar por precio (menor a mayor)
        return sorted(products, key=lambda p: p.get("price", 0))
    elif sort_criteria == "price_desc":
        # Ordenar por precio (mayor a menor)
        return sorted(products, key=lambda p: p.get("price", 0), reverse=True)
    elif sort_criteria == "distance":
        # Ordenar por distancia (más cerca primero)
        return sorted(
            products,
            key=lambda p: (p.get("distance_km") if p.get("distance_km") is not None else float('inf'))
        )
    elif sort_criteria == "sustainability":
        # Ordenar por sostenibilidad/huella de carbono (mejor sostenibilidad primero)
        # Usar la función mejorada de cálculo de sostenibilidad
        for product in products:
            if "sustainability_score" not in product:
                product["sustainability_score"] = calculate_sustainability_score(
                    product, product.get("distance_km")
                )
        return sorted(products, key=lambda p: p.get("sustainability_score", 0), reverse=True)
    elif sort_criteria == "stock":
        # Ordenar por stock disponible (mayor stock primero)
        return sorted(products, key=lambda p: p.get("stock_available", 0) or p.get("stock", 0), reverse=True)
    elif sort_criteria == "eco":
        # Ordenar por productos ecológicos primero
        return sorted(products, key=lambda p: p.get("is_eco", False), reverse=True)
    
    # Por defecto, usar algoritmo óptimo
    return sort_products_by_priority(products, user_location, weights, filters, "optimal")


def generate_optimal_basket(
    user_location: Tuple[float, float],
    requested_products: List[Dict],
    available_products: List[Dict],
    filters: Dict,
    criteria: Dict
) -> Tuple[List[Dict], float, Dict]:
    basket = []
    alternatives = {}
    
    for request in requested_products:
        candidates = [
            p for p in available_products
            if p["name"] == request["name"] and
               p["stock_available"] >= request["quantity"] and
               apply_filters(p, filters, user_location)
        ]
        
        if not candidates:
            # Buscar alternativas si no hay productos exactos
            for p in available_products:
                if p["name"] != request["name"] and apply_filters(p, filters, user_location):
                    alt_candidates = find_alternative_products(p, available_products)
                    if alt_candidates:
                        alternatives[request["name"]] = alt_candidates[:3]  # Top 3 alternativas
            continue

        # Usar el nuevo algoritmo de puntuación para seleccionar el mejor producto
        best_option = max(
            candidates,
            key=lambda p: calculate_product_score(p, user_location, criteria.get("weights", {}), filters)
        )

        distance_km = geodesic(user_location, (best_option["provider_lat"], best_option["provider_lon"])).km

        basket.append({
            "product_name": best_option["name"],
            "product_id": best_option["id"],
            "provider_id": best_option["provider_id"],
            "price": best_option["price"],
            "quantity": request["quantity"],
            "distance_km": round(distance_km, 2),
            "user_lat": user_location[0],
            "user_lon": user_location[1]
        })

    # Agrupar por proveedor
    provider_groups = group_by_provider(basket)
    
    total_cost = evaluate_basket(basket, criteria)
    
    return basket, round(total_cost, 2), {
        "provider_groups": provider_groups,
        "alternatives": alternatives
    }
