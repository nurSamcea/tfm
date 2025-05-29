from typing import List, Dict, Tuple
from geopy.distance import geodesic


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


def evaluate_basket(basket: List[Dict], criteria: Dict) -> float:
    total_price = sum(item["price"] * item["quantity"] for item in basket)
    total_distance = sum(item["distance_km"] for item in basket)
    unique_providers = set(item["provider_id"] for item in basket)

    total_cost = (
        criteria["price_weight"] * total_price +
        criteria["distance_weight"] * total_distance +
        criteria["provider_weight"] * len(unique_providers)
    )
    return total_cost


def generate_optimal_basket(
    user_location: Tuple[float, float],
    requested_products: List[Dict],
    available_products: List[Dict],
    filters: Dict,
    criteria: Dict
) -> Tuple[List[Dict], float]:
    basket = []

    for request in requested_products:
        candidates = [
            p for p in available_products
            if p["name"] == request["name"] and
               p["stock_available"] >= request["quantity"] and
               apply_filters(p, filters, user_location)
        ]
        if not candidates:
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
            "distance_km": round(distance_km, 2)
        })

    total_cost = evaluate_basket(basket, criteria)
    return basket, round(total_cost, 2)
