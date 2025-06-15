from typing import List, Dict, Tuple
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

        best_option = min(
            candidates,
            key=lambda p: (
                criteria["price_weight"] * p["price"] +
                criteria["distance_weight"] * geodesic(user_location, (p["provider_lat"], p["provider_lon"])).km
            )
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
