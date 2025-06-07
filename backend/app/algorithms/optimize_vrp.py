from typing import List, Dict, Tuple
from geopy.distance import geodesic
from datetime import datetime, date
from sqlalchemy.orm import Session
from backend.app.models import User, ShoppingListGroup, ShoppingList, LogisticsRoute

class LogisticsRouteOptimizer:
    def __init__(self, db: Session):
        self.db = db

    def get_pending_deliveries(self) -> List[Dict]:
        groups = (
            self.db.query(ShoppingListGroup, ShoppingList, User)
            .join(ShoppingList, ShoppingListGroup.shopping_list_id == ShoppingList.id)
            .join(User, ShoppingList.user_id == User.id)
            .filter(ShoppingListGroup.delivery_status == 'not_delivered')
            .all()
        )

        deliveries = []
        for group, shopping_list, consumer in groups:
            provider = self.db.query(User).filter(User.id == group.provider_id).first()
            if provider and provider.location_lat and provider.location_lon and \
               consumer.location_lat and consumer.location_lon:
                deliveries.append({
                    "group_id": group.id,
                    "sender_id": provider.id,
                    "receiver_id": consumer.id,
                    "sender_location": (float(provider.location_lat), float(provider.location_lon)),
                    "receiver_location": (float(consumer.location_lat), float(consumer.location_lon))
                })
        return deliveries

    def compute_distance_km(self, loc1: Tuple[float, float], loc2: Tuple[float, float]) -> float:
        return geodesic(loc1, loc2).km

    def generate_logistics_route(self) -> Dict:
        deliveries = self.get_pending_deliveries()
        if not deliveries:
            return {"message": "No hay entregas pendientes."}

        # Crear secuencia de paradas: pickup → delivery
        route_points = []
        for d in deliveries:
            route_points.append((d["group_id"], 'pickup', d["sender_id"], d["receiver_id"], d["sender_location"]))
            route_points.append((d["group_id"], 'delivery', d["sender_id"], d["receiver_id"], d["receiver_location"]))

        # Orden simplificado por distancia incremental desde primer punto
        origin = route_points[0][-1]  # primera ubicación
        ordered_route = []
        visited = set()
        current = origin
        while len(visited) < len(route_points):
            closest = None
            min_distance = float('inf')
            for i, (_, _, _, _, loc) in enumerate(route_points):
                if i in visited:
                    continue
                distance = self.compute_distance_km(current, loc)
                if distance < min_distance:
                    closest = i
                    min_distance = distance
            visited.add(closest)
            ordered_route.append(route_points[closest])
            current = route_points[closest][-1]

        # Guardar en la base de datos
        logistics_route = LogisticsRoute(
            driver_name="AutoGen",
            distance_km=sum(
                self.compute_distance_km(ordered_route[i][-1], ordered_route[i+1][-1])
                for i in range(len(ordered_route)-1)
            ),
            orders_ids=[d["group_id"] for d in deliveries],
            created_at=datetime.now(),
            vehicle_type="van",
            estimated_time_min=60,
            date=date.today(),
            status="pending"
        )
        self.db.add(logistics_route)
        self.db.commit()
        self.db.refresh(logistics_route)

        # Ya no se crean instancias de LogisticsRouteStop
        # Si necesitas guardar las paradas, deberías crear una nueva tabla o estructura

        return {"message": "Ruta logística generada con éxito", "route_id": logistics_route.id}