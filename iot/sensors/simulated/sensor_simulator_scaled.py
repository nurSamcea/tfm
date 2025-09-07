#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Simulador IoT Escalado - 100 Sensores Distribuidos
Simula múltiples sensores ESP32 distribuidos en diferentes zonas de granjas.

Características:
- 100 sensores simulados distribuidos en 10 zonas
- Patrones realistas de temperatura y humedad
- Simulación de fallos y reconexiones
- Distribución geográfica realista
- Comunicación HTTP optimizada

Uso:
  python iot/sensors/simulated/sensor_simulator_scaled.py --backend http://127.0.0.1:8000 --zones 10 --sensors-per-zone 10
"""

import argparse
import asyncio
import json
import random
import signal
import sys
import time
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
import aiohttp
import numpy as np
from dataclasses import dataclass, asdict

@dataclass
class SensorConfig:
    """Configuración de un sensor individual."""
    sensor_id: int
    device_id: str
    zone_id: int
    zone_name: str
    location_lat: float
    location_lon: float
    sensor_type: str
    temp_base: float
    humidity_base: float
    temp_variance: float
    humidity_variance: float
    reading_interval: int
    failure_probability: float
    recovery_time: int

@dataclass
class SensorReading:
    """Lectura de sensor."""
    sensor_id: int
    temperature: float
    humidity: float
    reading_quality: float
    is_processed: bool
    extra_data: Dict
    timestamp: datetime

class SensorSimulator:
    """Simulador de sensores IoT distribuidos."""
    
    def __init__(self, backend_url: str, zones: int, sensors_per_zone: int):
        self.backend_url = backend_url.rstrip('/')
        self.zones = zones
        self.sensors_per_zone = sensors_per_zone
        self.total_sensors = zones * sensors_per_zone
        
        # Configuración de zonas (coordenadas aproximadas de España)
        self.zone_configs = self._generate_zone_configs()
        
        # Configuración de sensores
        self.sensors: Dict[int, SensorConfig] = {}
        self.sensor_states: Dict[int, Dict] = {}
        self.sensor_readings: Dict[int, List[float]] = {}
        
        # Estadísticas
        self.stats = {
            'total_readings': 0,
            'successful_sends': 0,
            'failed_sends': 0,
            'sensor_failures': 0,
            'sensor_recoveries': 0,
            'start_time': datetime.now()
        }
        
        # Configuración de patrones
        self.patterns = self._load_patterns()
        
        # Estado de ejecución
        self.running = True
        self.session: Optional[aiohttp.ClientSession] = None
        
    def _generate_zone_configs(self) -> List[Dict]:
        """Generar configuración de zonas con coordenadas realistas."""
        zones = []
        
        # Coordenadas aproximadas de diferentes regiones de España
        locations = [
            {"name": "Zona Norte", "lat": 43.2, "lon": -2.9, "temp_base": 12.0, "humidity_base": 75.0},
            {"name": "Zona Centro", "lat": 40.4, "lon": -3.7, "temp_base": 15.0, "humidity_base": 60.0},
            {"name": "Zona Este", "lat": 39.5, "lon": -0.4, "temp_base": 18.0, "humidity_base": 65.0},
            {"name": "Zona Sur", "lat": 37.4, "lon": -5.9, "temp_base": 20.0, "humidity_base": 55.0},
            {"name": "Zona Oeste", "lat": 38.7, "lon": -9.1, "temp_base": 16.0, "humidity_base": 70.0},
            {"name": "Zona Montaña", "lat": 42.6, "lon": -1.6, "temp_base": 10.0, "humidity_base": 80.0},
            {"name": "Zona Costera", "lat": 36.7, "lon": -4.4, "temp_base": 19.0, "humidity_base": 70.0},
            {"name": "Zona Interior", "lat": 41.6, "lon": -4.7, "temp_base": 14.0, "humidity_base": 65.0},
            {"name": "Zona Mediterránea", "lat": 38.0, "lon": 1.1, "temp_base": 17.0, "humidity_base": 60.0},
            {"name": "Zona Atlántica", "lat": 43.5, "lon": -8.2, "temp_base": 13.0, "humidity_base": 75.0}
        ]
        
        for i in range(self.zones):
            if i < len(locations):
                zone_config = locations[i].copy()
            else:
                # Generar zonas adicionales si es necesario
                zone_config = {
                    "name": f"Zona {i+1}",
                    "lat": 40.0 + random.uniform(-5, 5),
                    "lon": -3.0 + random.uniform(-5, 5),
                    "temp_base": random.uniform(10, 20),
                    "humidity_base": random.uniform(50, 80)
                }
            
            zone_config["zone_id"] = i + 1
            zones.append(zone_config)
        
        return zones
    
    def _load_patterns(self) -> Dict:
        """Cargar patrones de comportamiento de sensores."""
        return {
            "daily_cycle": {
                "temperature": {
                    "min_hour": 6,  # Hora de temperatura mínima
                    "max_hour": 14,  # Hora de temperatura máxima
                    "amplitude": 8.0  # Amplitud de variación diaria
                },
                "humidity": {
                    "min_hour": 14,  # Hora de humedad mínima
                    "max_hour": 6,   # Hora de humedad máxima
                    "amplitude": 20.0  # Amplitud de variación diaria
                }
            },
            "weather_effects": {
                "sunny": {"temp_modifier": 1.2, "humidity_modifier": 0.8},
                "cloudy": {"temp_modifier": 0.9, "humidity_modifier": 1.1},
                "rainy": {"temp_modifier": 0.8, "humidity_modifier": 1.3},
                "stormy": {"temp_modifier": 0.7, "humidity_modifier": 1.5}
            },
            "seasonal_effects": {
                "spring": {"temp_modifier": 1.0, "humidity_modifier": 1.1},
                "summer": {"temp_modifier": 1.3, "humidity_modifier": 0.9},
                "autumn": {"temp_modifier": 0.9, "humidity_modifier": 1.2},
                "winter": {"temp_modifier": 0.7, "humidity_modifier": 1.0}
            }
        }
    
    def _generate_sensor_configs(self) -> None:
        """Generar configuración de todos los sensores."""
        sensor_id = 1
        
        for zone_config in self.zone_configs:
            for i in range(self.sensors_per_zone):
                # Variar ligeramente la ubicación dentro de la zona
                lat_offset = random.uniform(-0.01, 0.01)
                lon_offset = random.uniform(-0.01, 0.01)
                
                sensor_config = SensorConfig(
                    sensor_id=sensor_id,
                    device_id=f"ESP32_SIM_{sensor_id:03d}",
                    zone_id=zone_config["zone_id"],
                    zone_name=zone_config["name"],
                    location_lat=zone_config["lat"] + lat_offset,
                    location_lon=zone_config["lon"] + lon_offset,
                    sensor_type="temperature_humidity",
                    temp_base=zone_config["temp_base"] + random.uniform(-2, 2),
                    humidity_base=zone_config["humidity_base"] + random.uniform(-5, 5),
                    temp_variance=random.uniform(1.0, 3.0),
                    humidity_variance=random.uniform(2.0, 5.0),
                    reading_interval=random.randint(20, 60),  # 20-60 segundos
                    failure_probability=random.uniform(0.001, 0.01),  # 0.1-1% por lectura
                    recovery_time=random.randint(300, 1800)  # 5-30 minutos
                )
                
                self.sensors[sensor_id] = sensor_config
                self.sensor_states[sensor_id] = {
                    'is_online': True,
                    'last_reading': None,
                    'failure_start': None,
                    'consecutive_failures': 0
                }
                self.sensor_readings[sensor_id] = []
                
                sensor_id += 1
    
    def _calculate_reading(self, sensor_id: int) -> Tuple[float, float]:
        """Calcular lectura de sensor con patrones realistas."""
        config = self.sensors[sensor_id]
        now = datetime.now()
        
        # Ciclo diario
        hour = now.hour + now.minute / 60.0
        
        # Temperatura con ciclo diario
        temp_cycle = np.sin(2 * np.pi * (hour - 6) / 24) * self.patterns["daily_cycle"]["temperature"]["amplitude"]
        temperature = config.temp_base + temp_cycle + random.gauss(0, config.temp_variance)
        
        # Humedad con ciclo diario (inverso a temperatura)
        humidity_cycle = -np.sin(2 * np.pi * (hour - 6) / 24) * self.patterns["daily_cycle"]["humidity"]["amplitude"]
        humidity = config.humidity_base + humidity_cycle + random.gauss(0, config.humidity_variance)
        
        # Efectos estacionales (simplificado)
        month = now.month
        if month in [12, 1, 2]:  # Invierno
            temperature *= 0.8
            humidity *= 1.1
        elif month in [6, 7, 8]:  # Verano
            temperature *= 1.2
            humidity *= 0.9
        
        # Efectos meteorológicos aleatorios
        weather = random.choices(
            list(self.patterns["weather_effects"].keys()),
            weights=[0.4, 0.3, 0.2, 0.1]  # Más probabilidad de sol
        )[0]
        
        weather_effect = self.patterns["weather_effects"][weather]
        temperature *= weather_effect["temp_modifier"]
        humidity *= weather_effect["humidity_modifier"]
        
        # Limitar valores a rangos realistas
        temperature = max(-10, min(50, temperature))
        humidity = max(10, min(100, humidity))
        
        return round(temperature, 2), round(humidity, 2)
    
    def _check_sensor_failure(self, sensor_id: int) -> bool:
        """Verificar si un sensor debe fallar."""
        config = self.sensors[sensor_id]
        state = self.sensor_states[sensor_id]
        
        if not state['is_online']:
            # Verificar si debe recuperarse
            if state['failure_start'] and \
               (datetime.now() - state['failure_start']).seconds > config.recovery_time:
                state['is_online'] = True
                state['failure_start'] = None
                state['consecutive_failures'] = 0
                self.stats['sensor_recoveries'] += 1
                print(f"Sensor {sensor_id} ({config.device_id}) recuperado")
            return False
        
        # Verificar probabilidad de fallo
        if random.random() < config.failure_probability:
            state['is_online'] = False
            state['failure_start'] = datetime.now()
            state['consecutive_failures'] += 1
            self.stats['sensor_failures'] += 1
            print(f"Sensor {sensor_id} ({config.device_id}) falló")
            return True
        
        return False
    
    async def _send_reading(self, reading: SensorReading) -> bool:
        """Enviar lectura al backend."""
        try:
            data = {
                "sensor_id": reading.sensor_id,
                "temperature": reading.temperature,
                "humidity": reading.humidity,
                "reading_quality": reading.reading_quality,
                "is_processed": reading.is_processed,
                "extra_data": reading.extra_data
            }
            
            async with self.session.post(
                f"{self.backend_url}/sensor_readings/",
                json=data,
                timeout=aiohttp.ClientTimeout(total=10)
            ) as response:
                if response.status == 200:
                    self.stats['successful_sends'] += 1
                    return True
                else:
                    print(f"Error HTTP {response.status} para sensor {reading.sensor_id}")
                    self.stats['failed_sends'] += 1
                    return False
                    
        except Exception as e:
            print(f"Error enviando lectura del sensor {reading.sensor_id}: {e}")
            self.stats['failed_sends'] += 1
            return False
    
    async def _simulate_sensor(self, sensor_id: int) -> None:
        """Simular un sensor individual."""
        config = self.sensors[sensor_id]
        state = self.sensor_states[sensor_id]
        
        while self.running:
            try:
                # Verificar fallo del sensor
                if self._check_sensor_failure(sensor_id):
                    await asyncio.sleep(config.reading_interval)
                    continue
                
                # Generar lectura
                temperature, humidity = self._calculate_reading(sensor_id)
                
                # Calcular calidad de lectura
                reading_quality = random.uniform(0.8, 1.0)
                
                # Crear lectura
                reading = SensorReading(
                    sensor_id=sensor_id,
                    temperature=temperature,
                    humidity=humidity,
                    reading_quality=reading_quality,
                    is_processed=False,  # Los sensores simulados no procesan
                    extra_data={
                        "device_id": config.device_id,
                        "zone_id": config.zone_id,
                        "zone_name": config.zone_name,
                        "location_lat": config.location_lat,
                        "location_lon": config.location_lon,
                        "sensor_type": config.sensor_type,
                        "simulated": True,
                        "firmware_version": "3.0_sim",
                        "uptime": random.randint(1000, 86400)
                    },
                    timestamp=datetime.now()
                )
                
                # Enviar lectura
                await self._send_reading(reading)
                
                # Actualizar estado
                state['last_reading'] = reading
                self.sensor_readings[sensor_id].append(reading)
                
                # Mantener solo las últimas 100 lecturas
                if len(self.sensor_readings[sensor_id]) > 100:
                    self.sensor_readings[sensor_id] = self.sensor_readings[sensor_id][-100:]
                
                self.stats['total_readings'] += 1
                
                # Esperar intervalo del sensor
                await asyncio.sleep(config.reading_interval)
                
            except Exception as e:
                print(f"Error en sensor {sensor_id}: {e}")
                await asyncio.sleep(5)
    
    async def _print_stats(self) -> None:
        """Imprimir estadísticas periódicamente."""
        while self.running:
            await asyncio.sleep(60)  # Cada minuto
            
            uptime = datetime.now() - self.stats['start_time']
            online_sensors = sum(1 for state in self.sensor_states.values() if state['is_online'])
            
            print(f"\n=== ESTADÍSTICAS ({uptime}) ===")
            print(f"Sensores online: {online_sensors}/{self.total_sensors}")
            print(f"Lecturas totales: {self.stats['total_readings']}")
            print(f"Envíos exitosos: {self.stats['successful_sends']}")
            print(f"Envíos fallidos: {self.stats['failed_sends']}")
            print(f"Fallos de sensores: {self.stats['sensor_failures']}")
            print(f"Recuperaciones: {self.stats['sensor_recoveries']}")
            
            if self.stats['total_readings'] > 0:
                success_rate = (self.stats['successful_sends'] / self.stats['total_readings']) * 100
                print(f"Tasa de éxito: {success_rate:.1f}%")
    
    async def run(self) -> None:
        """Ejecutar el simulador."""
        print(f"Iniciando simulador de {self.total_sensors} sensores en {self.zones} zonas")
        print(f"Backend: {self.backend_url}")
        
        # Generar configuraciones
        self._generate_sensor_configs()
        
        # Crear sesión HTTP
        self.session = aiohttp.ClientSession()
        
        try:
            # Crear tareas para todos los sensores
            tasks = []
            for sensor_id in self.sensors.keys():
                task = asyncio.create_task(self._simulate_sensor(sensor_id))
                tasks.append(task)
            
            # Tarea para estadísticas
            stats_task = asyncio.create_task(self._print_stats())
            tasks.append(stats_task)
            
            # Esperar a que terminen todas las tareas
            await asyncio.gather(*tasks)
            
        finally:
            if self.session:
                await self.session.close()

def signal_handler(signum, frame):
    """Manejar señales de interrupción."""
    print("\nDeteniendo simulador...")
    sys.exit(0)

def main():
    """Función principal."""
    parser = argparse.ArgumentParser(description="Simulador IoT Escalado")
    parser.add_argument("--backend", default="http://127.0.0.1:8000", help="URL del backend")
    parser.add_argument("--zones", type=int, default=10, help="Número de zonas")
    parser.add_argument("--sensors-per-zone", type=int, default=10, help="Sensores por zona")
    parser.add_argument("--total-sensors", type=int, help="Total de sensores (sobrescribe zones*sensors-per-zone)")
    
    args = parser.parse_args()
    
    # Configurar manejador de señales
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)
    
    # Calcular configuración
    if args.total_sensors:
        total_sensors = args.total_sensors
        zones = args.zones
        sensors_per_zone = total_sensors // zones
        if total_sensors % zones != 0:
            sensors_per_zone += 1
    else:
        zones = args.zones
        sensors_per_zone = args.sensors_per_zone
    
    # Crear y ejecutar simulador
    simulator = SensorSimulator(args.backend, zones, sensors_per_zone)
    
    try:
        asyncio.run(simulator.run())
    except KeyboardInterrupt:
        print("\nSimulador detenido por el usuario")
    except Exception as e:
        print(f"Error en el simulador: {e}")

if __name__ == "__main__":
    main()
