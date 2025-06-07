import unittest

from backend.app.algorithms.optimize_vrp import VRPOptimizer


class TestVRPOptimizer(unittest.TestCase):

    def test_simple_case(self):
        # Ejemplo basado en una red con 2 proveedores y 3 consumidores
        distance_matrix = [
            [0, 10, 15, 20, 25],
            [10, 0, 35, 25, 30],
            [15, 35, 0, 30, 5],
            [20, 25, 30, 0, 10],
            [25, 30, 5, 10, 0]
        ]
        demands = [0, 1, 1, 1, 1]  # El nodo 0 es el depósito (proveedor)
        vehicle_capacities = [2, 2]
        starts = [0, 0]  # Ambos vehículos empiezan en el proveedor 0
        ends = [0, 0]  # Y también terminan allí

        optimizer = VRPOptimizer(distance_matrix, demands, vehicle_capacities, starts, ends)
        result = optimizer.solve()

        self.assertIsInstance(result, dict)
        self.assertEqual(len(result), 2)
        all_visited = set()
        for route in result.values():
            all_visited.update(route[1:-1])  # ignoramos depósito inicial/final

        self.assertEqual(all_visited, {1, 2, 3, 4})


if __name__ == '__main__':
    unittest.main()
