import unittest

from backend.app.algorithms.optimize_products import generate_optimal_basket


class TestOptimalBasketAlgorithm(unittest.TestCase):
    def setUp(self):
        self.available_products = [
            {"id": 1, "name": "Apple", "price": 1.2, "stock_available": 100, "provider_id": 101,
             "provider_lat": 40.4168, "provider_lon": -3.7038, "is_eco": True, "is_gluten_free": True},
            {"id": 2, "name": "Apple", "price": 1.1, "stock_available": 100, "provider_id": 102,
             "provider_lat": 40.0, "provider_lon": -3.5, "is_eco": False, "is_gluten_free": True},
            {"id": 3, "name": "Bread", "price": 2.0, "stock_available": 50, "provider_id": 103,
             "provider_lat": 40.4, "provider_lon": -3.8, "is_eco": True, "is_gluten_free": False},
            {"id": 4, "name": "Bread", "price": 1.8, "stock_available": 100, "provider_id": 104,
             "provider_lat": 40.2, "provider_lon": -3.6, "is_eco": False, "is_gluten_free": True},
            {"id": 5, "name": "Milk", "price": 1.5, "stock_available": 100, "provider_id": 105,
             "provider_lat": 40.3, "provider_lon": -3.7, "is_eco": True, "is_gluten_free": True}
        ]

        self.requested_products = [
            {"name": "Apple", "quantity": 3},
            {"name": "Bread", "quantity": 2},
            {"name": "Milk", "quantity": 1}
        ]

        self.user_filters = {
            "eco": True,
            "gluten_free": True,
            "max_distance_km": 50.0
        }

        self.optimization_criteria = {
            "price_weight": 1.0,
            "distance_weight": 0.5,
            "provider_weight": 2.0
        }

        self.user_location = (40.4168, -3.7038)  # Madrid

    def test_generate_optimal_basket(self):
        basket, total_cost = generate_optimal_basket(
            user_location=self.user_location,
            requested_products=self.requested_products,
            available_products=self.available_products,
            filters=self.user_filters,
            criteria=self.optimization_criteria
        )

        # We expect 2 items because there is no Bread that is both eco and gluten-free
        self.assertEqual(len(basket), 2)
        self.assertGreater(total_cost, 0)
        self.assertTrue(all(item["distance_km"] <= 50.0 for item in basket))
        self.assertTrue(all(item["price"] > 0 for item in basket))

        print("\nOptimal Basket:")
        for item in basket:
            print(item)
        print(f"Total Cost (weighted): {total_cost}")


# Run the test
suite = unittest.TestLoader().loadTestsFromTestCase(TestOptimalBasketAlgorithm)
result = unittest.TextTestRunner(verbosity=2).run(suite)
