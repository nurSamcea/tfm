#!/usr/bin/env python3
"""
Sistema IoT Híbrido - Sensores Reales y Simulados
Maneja tanto sensores reales (ESP32) como simulados (Python)
"""

import os
import sys
import logging
import asyncio
import aiohttp
import json
import time
import random
from datetime import datetime
from typing import Dict, List, Optional, Any
from abc import ABC, abstractmethod
from dataclasses import dataclass
from pathlib import Path
from dotenv import load_dotenv

# Cargar variables de entorno
load_dotenv()

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger(__name__)

@dataclass
class IoTConfig:
    backend_url: str = None
    ingest_token: str = None
    ingest_endpoint: str = "/iot/ingest"
    
    def __post_init__(self):
        if self.backend_url is None:
            backend_ip = os.getenv('BACKEND_IP', '192.168.68.116')
            backend_port = os.getenv('BACKEND_PORT', '8000')
            backend_protocol = os.getenv('BACKEND_PROTOCOL', 'http')
            self.backend_url = f"{backend_protocol}://{backend_ip}:{backend_port}"
            logger.info(f"Backend URL configurada: {self.backend_url}")
        
        if self.ingest_token is None:
            self.ingest_token = os.getenv('IOT_INGEST_TOKEN', 'dev_iot_token_2024')

class IoTConnection:    
    def __init__(self, config: IoTConfig):
        self.config = config
        self.session: Optional[aiohttp.ClientSession] = None
    
    async def connect(self) -> bool:
        try:
            self.session = aiohttp.ClientSession(
                timeout=aiohttp.ClientTimeout(total=30),
                headers={"Content-Type": "application/json"}
            )
            logger.info("Conexión HTTP establecida")
            return True
        except Exception as e:
            logger.error(f"Error estableciendo conexión HTTP: {e}")
            return False
    
    async def disconnect(self):
        if self.session:
            await self.session.close()
            logger.info("Conexión HTTP cerrada")

class Sensor(ABC):
    def __init__(self, device_id: str, name: str, sensor_type: str):
        self.device_id = device_id
        self.name = name
        self.sensor_type = sensor_type
        self.last_reading = None
        self.reading_count = 0
    
    @abstractmethod
    async def read_sensor_data(self) -> Dict[str, Any]:
        pass
    
    def get_stats(self) -> Dict[str, Any]:
        return {
            "device_id": self.device_id,
            "name": self.name,
            "type": self.sensor_type,
            "readings": self.reading_count,
            "last_reading": self.last_reading
        }

class RealSensor(Sensor):    
    def __init__(self, device_id: str, name: str, sensor_type: str):
        super().__init__(device_id, name, sensor_type)
        logger.info(f"Sensor real configurado: {device_id}")
    
    async def read_sensor_data(self) -> Dict[str, Any]:
        # Los sensores reales se conectan directamente al backend
        # Este método es solo para documentación
        return {
            "device_id": self.device_id,
            "sensor_type": self.sensor_type,
            "status": "real_sensor_connected_directly"
        }

class SimulatedSensor(Sensor):    
    def __init__(self, device_id: str, name: str, sensor_type: str, 
                 base_temperature: float = 22.0, base_humidity: float = 70.0,
                 temperature_variance: float = 3.0, humidity_variance: float = 8.0):
        super().__init__(device_id, name, sensor_type)
        self.base_temperature = base_temperature
        self.base_humidity = base_humidity
        self.temperature_variance = temperature_variance
        self.humidity_variance = humidity_variance
        logger.info(f"Sensor simulado configurado: {device_id}")
    
    async def read_sensor_data(self) -> Dict[str, Any]:
        # Simular variaciones naturales
        temp_variation = random.uniform(-self.temperature_variance, self.temperature_variance)
        humidity_variation = random.uniform(-self.humidity_variance, self.humidity_variance)
        
        temperature = round(self.base_temperature + temp_variation, 2)
        humidity = round(self.base_humidity + humidity_variation, 2)
        
        # Asegurar rangos realistas
        temperature = max(10.0, min(40.0, temperature))
        humidity = max(20.0, min(95.0, humidity))
        
        self.reading_count += 1
        self.last_reading = datetime.now()
        
        return {
            "device_id": self.device_id,
            "sensor_type": self.sensor_type,
            "temperature": temperature,
            "humidity": humidity,
            "timestamp": datetime.now().isoformat(),
            "reading_quality": random.uniform(0.8, 1.0),
            "extra_data": {
                "sensor_name": self.name,
                "simulated": True,
                "reading_number": self.reading_count
            }
        }

class IoTManager:    
    def __init__(self, config: IoTConfig = None):
        self.config = config or IoTConfig()
        self.connection = IoTConnection(self.config)
        self.sensors: Dict[str, Sensor] = {}
        self.running = False
        self.stats = {
            "total_readings": 0,
            "successful_sends": 0,
            "failed_sends": 0,
            "sensors_active": 0
        }
    
    async def initialize(self) -> bool:
        logger.info("Inicializando sistema IoT híbrido...")
        
        if not await self.connection.connect():
            return False
        
        # Crear sensores simulados
        await self.create_simulated_sensors()
        
        logger.info("Sistema IoT híbrido inicializado exitosamente")
        return True
    
    async def create_simulated_sensors(self):
        # Sensor simulado de Pedro Sánchez (existe en la base de datos)
        pedro_sensor = SimulatedSensor(
            device_id="pedro-sanchez-sensor-01",
            name="Sensor Simulado Pedro Sánchez",
            sensor_type="humidity",
            base_temperature=22.0,
            base_humidity=70.0,
            temperature_variance=3.0,
            humidity_variance=8.0
        )
        self.sensors[pedro_sensor.device_id] = pedro_sensor
        
        logger.info(f"Creados {len(self.sensors)} sensores simulados")
    
    async def send_sensor_reading(self, sensor: Sensor) -> bool:
        try:
            # Leer datos del sensor
            sensor_data = await sensor.read_sensor_data()
            
            # Preparar payload para el backend
            payload = {
                "device_id": sensor.device_id,
                "sensor_type": sensor.sensor_type,
                "temperature": sensor_data.get("temperature"),
                "humidity": sensor_data.get("humidity"),
                "timestamp": sensor_data.get("timestamp", datetime.now().isoformat()),
                "reading_quality": sensor_data.get("reading_quality", 1.0),
                "extra_data": sensor_data.get("extra_data", {})
            }
            
            # Enviar al backend
            async with self.connection.session.post(
                f"{self.config.backend_url}{self.config.ingest_endpoint}",
                json=payload,
                headers={"X-Ingest-Token": self.config.ingest_token}
            ) as response:
                if response.status == 200:
                    self.stats["successful_sends"] += 1
                    logger.info(f"Datos enviados: {sensor.device_id} - Temp: {payload.get('temperature')}°C, Hum: {payload.get('humidity')}%")
                    return True
                else:
                    self.stats["failed_sends"] += 1
                    logger.error(f"Error enviando datos: {response.status} - {sensor.device_id}")
                    return False
                    
        except Exception as e:
            self.stats["failed_sends"] += 1
            logger.error(f"Error en sensor {sensor.device_id}: {e}")
            return False
    
    async def start_all_sensors(self):
        logger.info("Iniciando sensores simulados...")
        self.running = True
        
        while self.running:
            for sensor in self.sensors.values():
                if isinstance(sensor, SimulatedSensor):
                    await self.send_sensor_reading(sensor)
                    self.stats["total_readings"] += 1
                    await asyncio.sleep(1)  # Pequeña pausa entre sensores
            
            # Pausa entre ciclos completos
            await asyncio.sleep(30)  # 30 segundos entre ciclos
    
    async def shutdown(self):
        logger.info("Cerrando sistema IoT...")
        self.running = False
        await self.connection.disconnect()
        logger.info("Sistema IoT cerrado")
    
    def get_stats(self) -> Dict[str, Any]:
        return {
            **self.stats,
            "sensors_registered": len(self.sensors),
            "running": self.running
        }

async def main():
    logger.info("=== SISTEMA IoT HÍBRIDO ===")
    logger.info("Sensores reales: ESP32 (conexión directa al backend)")
    logger.info("Sensores simulados: Python (este programa)")
    
    # Crear gestor IoT
    iot_manager = IoTManager()
    
    try:
        # Inicializar sistema
        if not await iot_manager.initialize():
            logger.error("Error inicializando sistema IoT")
            return
        
        # Mostrar estadísticas iniciales
        logger.info(f"Estadísticas iniciales: {iot_manager.get_stats()}")
        
        # Iniciar sensores simulados
        await iot_manager.start_all_sensors()
        
    except KeyboardInterrupt:
        logger.info("Sistema detenido por el usuario")
    except Exception as e:
        logger.error(f"Error en sistema IoT: {e}")
    finally:
        await iot_manager.shutdown()

if __name__ == "__main__":
    asyncio.run(main())
