#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Script de inicio para el simulador IoT escalado.
Facilita el inicio del simulador con diferentes configuraciones.

Uso:
  python iot/start_simulator.py --mode full      # 100 sensores
  python iot/start_simulator.py --mode medium    # 50 sensores
  python iot/start_simulator.py --mode small     # 10 sensores
  python iot/start_simulator.py --mode test      # 5 sensores para testing
"""

import argparse
import asyncio
import sys
import os
from pathlib import Path

# Agregar el directorio raíz al path para importar módulos
root_dir = Path(__file__).parent.parent
sys.path.insert(0, str(root_dir))

from iot.sensors.simulated.sensor_simulator_scaled import SensorSimulator

def main():
    """Función principal."""
    parser = argparse.ArgumentParser(description="Iniciar simulador IoT escalado")
    parser.add_argument("--mode", choices=["test", "small", "medium", "full"], 
                       default="test", help="Modo de simulación")
    parser.add_argument("--backend", default="http://127.0.0.1:8000", 
                       help="URL del backend")
    parser.add_argument("--zones", type=int, help="Número de zonas (sobrescribe modo)")
    parser.add_argument("--sensors-per-zone", type=int, 
                       help="Sensores por zona (sobrescribe modo)")
    
    args = parser.parse_args()
    
    # Configuraciones por modo
    mode_configs = {
        "test": {"zones": 2, "sensors_per_zone": 3, "total": 6},
        "small": {"zones": 5, "sensors_per_zone": 2, "total": 10},
        "medium": {"zones": 10, "sensors_per_zone": 5, "total": 50},
        "full": {"zones": 10, "sensors_per_zone": 10, "total": 100}
    }
    
    config = mode_configs[args.mode]
    
    # Sobrescribir con argumentos si se proporcionan
    if args.zones:
        config["zones"] = args.zones
    if args.sensors_per_zone:
        config["sensors_per_zone"] = args.sensors_per_zone
    
    # Recalcular total
    config["total"] = config["zones"] * config["sensors_per_zone"]
    
    print(f"=== SIMULADOR IoT ESCALADO ===")
    print(f"Modo: {args.mode}")
    print(f"Backend: {args.backend}")
    print(f"Zonas: {config['zones']}")
    print(f"Sensores por zona: {config['sensors_per_zone']}")
    print(f"Total sensores: {config['total']}")
    print(f"================================")
    
    # Crear y ejecutar simulador
    simulator = SensorSimulator(
        backend_url=args.backend,
        zones=config["zones"],
        sensors_per_zone=config["sensors_per_zone"]
    )
    
    try:
        asyncio.run(simulator.run())
    except KeyboardInterrupt:
        print("\nSimulador detenido por el usuario")
    except Exception as e:
        print(f"Error en el simulador: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
