import datetime
import random

from backend.app.database import SessionLocal
from backend.app.models import user, product, shopping_list, shopping_list_group, shopping_list_item, transaction

def main():
    db = SessionLocal()
    try:
        # Vaciar tablas (orden correcto para evitar conflictos)
        db.query(transaction.Transaction).delete()
        db.query(shopping_list_item.ShoppingListItem).delete()
        db.query(shopping_list_group.ShoppingListGroup).delete()
        db.query(shopping_list.ShoppingList).delete()
        db.query(product.Product).delete()
        db.query(user.User).delete()
        db.commit()

        # 1. Usuarios
        users = [
            user.User(id=1, name='Juan Agrícola', email='juan@campo.com', password_hash='hash1', role='farmer',
                      entity_name='Finca Juan',
                      location_lat=41.5033, location_lon=-5.7446),  # Zamora

            user.User(id=2, name='Marta Orgánica', email='marta@eco.com', password_hash='hash2', role='farmer',
                      entity_name='EcoMarta',
                      location_lat=36.7213, location_lon=-4.4214),  # Málaga

            user.User(id=3, name='SuperMercadoX', email='x@super.com', password_hash='hash3', role='supermarket',
                      entity_name='SuperMercadoX',
                      location_lat=40.4168, location_lon=-3.7038),  # Madrid

            user.User(id=4, name='SuperMercadoY', email='y@super.com', password_hash='hash4', role='supermarket',
                      entity_name='SuperMercadoY',
                      location_lat=41.3874, location_lon=2.1686),  # Barcelona

            user.User(id=5, name='Laura Cliente', email='laura@cliente.com', password_hash='hash5', role='consumer',
                      entity_name=None,
                      location_lat=39.4699, location_lon=-0.3763),  # Valencia

            user.User(id=6, name='Pedro Cliente', email='pedro@cliente.com', password_hash='hash6', role='consumer',
                      entity_name=None,
                      location_lat=43.3623, location_lon=-8.4115),  # A Coruña
        ]

        db.add_all(users)
        db.commit()

        # 2. Productos (más variados y coherentes)
        products = []
        pid = 1

        for i in range(10):
            products.append(product.Product(
                id=pid,
                name=f'Tomate Ecológico {i+1}',
                description='Tomate fresco y ecológico',
                price=round(random.uniform(1.0, 3.5), 2),
                currency='EUR',
                unit='kg',
                category='Verdura',
                stock_available=50 + i * 5,
                expiration_date=datetime.date.today() + datetime.timedelta(days=30),
                is_eco=True,
                provider_id=1 if i % 2 == 0 else 2,
                image_url=None,
                created_at=datetime.datetime.now()
            ))
            pid += 1

        for i in range(10):
            products.append(product.Product(
                id=pid,
                name=f'Manzana Roja {i+1}',
                description='Manzana dulce del huerto',
                price=round(random.uniform(2.0, 4.5), 2),
            currency='EUR',
                unit='kg',
                category='Fruta',
                stock_available=60 + i * 4,
                expiration_date=datetime.date.today() + datetime.timedelta(days=40),
                is_eco=(i % 2 == 0),
                provider_id=2 if i % 3 == 0 else 1,
                image_url=None,
                created_at=datetime.datetime.now()
            ))
            pid += 1

        products.append(product.Product(id=21, name='Lechuga Romana', description='Lechuga crujiente', price=1.20, currency='EUR',
                        unit='unidad', category='Verdura', stock_available=80,
                        expiration_date=datetime.date(2024, 7, 15), is_eco=True, provider_id=2, image_url=None,
                        created_at=datetime.datetime(2024, 6, 2, 11, 0, 0)))
        products.append(product.Product(id=22, name='Zanahoria', description='Zanahoria orgánica', price=1.80, currency='EUR', unit='kg',
                        category='Verdura', stock_available=60, expiration_date=datetime.date(2024, 8, 10), is_eco=True,
                        provider_id=2, image_url=None, created_at=datetime.datetime(2024, 6, 4, 13, 0, 0)))

        db.add_all(products)
        db.commit()

        # 3. Listas de compra
        shopping_lists = [
            shopping_list.ShoppingList(id=1, user_id=5, created_at=datetime.datetime.now(), total_price=20.0, currency='EUR', status='paid'),
            shopping_list.ShoppingList(id=2, user_id=6, created_at=datetime.datetime.now(), total_price=15.0, currency='EUR', status='pending'),
        ]
        db.add_all(shopping_lists)
        db.commit()

        # 4. Grupos de lista
        shopping_list_groups = [
            shopping_list_group.ShoppingListGroup(id=1, shopping_list_id=1, provider_id=1, subtotal_price=12.0, delivery_estimate=None),
            shopping_list_group.ShoppingListGroup(id=2, shopping_list_id=1, provider_id=2, subtotal_price=8.0, delivery_estimate=None),
            shopping_list_group.ShoppingListGroup(id=3, shopping_list_id=2, provider_id=1, subtotal_price=10.0, delivery_estimate=None),
            shopping_list_group.ShoppingListGroup(id=4, shopping_list_id=2, provider_id=2, subtotal_price=5.0, delivery_estimate=None),
        ]
        db.add_all(shopping_list_groups)
        db.commit()

        # 5. Items
        shopping_list_items = [
            shopping_list_item.ShoppingListItem(id=1, shopping_list_group_id=1, product_id=1, quantity=2, unit='kg', price_unit=2.1, currency='EUR', total_price=4.2, expiration_date=datetime.date.today() + datetime.timedelta(days=30), trace_hash=None, nutritional_info=None, added_at=datetime.datetime.now()),
            shopping_list_item.ShoppingListItem(id=2, shopping_list_group_id=2, product_id=2, quantity=1.5, unit='kg', price_unit=2.3, currency='EUR', total_price=3.45, expiration_date=datetime.date.today() + datetime.timedelta(days=30), trace_hash=None, nutritional_info=None, added_at=datetime.datetime.now()),
            shopping_list_item.ShoppingListItem(id=3, shopping_list_group_id=3, product_id=11, quantity=1, unit='kg', price_unit=2.5, currency='EUR', total_price=2.5, expiration_date=datetime.date.today() + datetime.timedelta(days=40), trace_hash=None, nutritional_info=None, added_at=datetime.datetime.now()),
            shopping_list_item.ShoppingListItem(id=4, shopping_list_group_id=4, product_id=15, quantity=1.5, unit='kg', price_unit=2.8, currency='EUR', total_price=4.2, expiration_date=datetime.date.today() + datetime.timedelta(days=40), trace_hash=None, nutritional_info=None, added_at=datetime.datetime.now()),
        ]
        db.add_all(shopping_list_items)
        db.commit()

        # 6. Transacciones
        transactions = [
            transaction.Transaction(id=1, user_id=5, shopping_list_id=1, total_price=20.0, currency='EUR', status='paid', created_at=datetime.datetime.now(), confirmed_at=datetime.datetime.now()),
            transaction.Transaction(id=2, user_id=6, shopping_list_id=2, total_price=15.0, currency='EUR', status='pending', created_at=datetime.datetime.now(), confirmed_at=None),
        ]
        db.add_all(transactions)
        db.commit()

        print('✅ ¡Datos coherentes insertados correctamente!')
    finally:
        db.close()

if __name__ == '__main__':
    main()
