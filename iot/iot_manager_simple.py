#!/usr/bin/env python3
"""
Sistema IoT Híbrido - Sensores Simulados (Versión Simple)
Usa solo librerías estándar de Python para evitar problemas de dependencias
"""

import os
import sys
import logging
import json
import time
import random
from datetime import datetime
from typing import Dict, List, Optional, Any
from abc import ABC, abstractmethod
from dataclasses import dataclass
import urllib.request
import urllib.parse
import urllib.error

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
            backend_ip = os.getenv('BACKEND_IP', '10.167.157.98')
            backend_port = os.getenv('BACKEND_PORT', '8000')
            backend_protocol = os.getenv('BACKEND_PROTOCOL', 'http')
            self.backend_url = f"{backend_protocol}://{backend_ip}:{backend_port}"
            logger.info(f"Backend URL configurada: {self.backend_url}")
        
        if self.ingest_token is None:
            self.ingest_token = os.getenv('IOT_INGEST_TOKEN', 'dev_iot_token_2024')

class Sensor(ABC):
    def __init__(self, device_id: str, name: str, sensor_type: str):
        self.device_id = device_id
        self.name = name
        self.sensor_type = sensor_type
        self.last_reading = None
        self.reading_count = 0
    
    @abstractmethod
    def read_sensor_data(self) -> Dict[str, Any]:
        pass
    
    def get_stats(self) -> Dict[str, Any]:
        return {
            "device_id": self.device_id,
            "name": self.name,
            "type": self.sensor_type,
            "readings": self.reading_count,
            "last_reading": self.last_reading
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
    
    def read_sensor_data(self) -> Dict[str, Any]:
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
        self.sensors: Dict[str, Sensor] = {}
        self.running = False
        self.stats = {
            "total_readings": 0,
            "successful_sends": 0,
            "failed_sends": 0,
            "sensors_active": 0
        }
    
    def initialize(self) -> bool:
        logger.info("Inicializando sistema IoT híbrido...")
        
        # Verificar conectividad con el backend
        if not self.verify_backend_connection():
            logger.error("Error verificando conexión con backend")
            return False
        
        # Crear sensores simulados
        if not self.create_simulated_sensors():
            logger.error("Error creando sensores simulados")
            return False
        
        logger.info("Sistema IoT híbrido inicializado exitosamente")
        return True
    
    def verify_backend_connection(self) -> bool:
        """Verificar que el backend está disponible"""
        try:
            url = f"{self.config.backend_url}/"
            logger.info(f"Verificando conectividad con: {url}")
            
            req = urllib.request.Request(url)
            with urllib.request.urlopen(req, timeout=10) as response:
                response_text = response.read().decode('utf-8')
                logger.info(f"Respuesta backend: {response.status} - {response_text[:100]}...")
                
                if response.status == 200:
                    logger.info("Backend disponible")
                    return True
                else:
                    logger.error(f"Backend respondió con status {response.status}")
                    return False
        except Exception as e:
            logger.error(f"Error conectando al backend: {e}")
            return False
    
    def create_simulated_sensors(self):
        # Sensor simulado de Pedro Sánchez
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
        
        # Verificar que el sensor existe en la base de datos
        if not self.verify_sensor_exists(pedro_sensor.device_id):
            logger.error(f"Sensor {pedro_sensor.device_id} no existe en la base de datos")
            logger.error("Ejecuta primero: python register_sensor.py")
            return False
        
        logger.info(f"Creados {len(self.sensors)} sensores simulados")
        return True
    
    def verify_sensor_exists(self, device_id: str) -> bool:
        """Verificar que el sensor existe en la base de datos"""
        try:
            url = f"{self.config.backend_url}/iot/devices/{device_id}/telemetry?limit=1"
            logger.info(f"Verificando sensor en: {url}")
            
            req = urllib.request.Request(url)
            with urllib.request.urlopen(req, timeout=10) as response:
                response_text = response.read().decode('utf-8')
                logger.info(f"Respuesta verificación: {response.status} - {response_text}")
                
                if response.status == 200:
                    logger.info(f"Sensor {device_id} verificado en la base de datos")
                    return True
                elif response.status == 404:
                    logger.error(f"Sensor {device_id} no encontrado en la base de datos")
                    return False
                else:
                    logger.error(f"Error verificando sensor {device_id}: {response.status} - {response_text}")
                    return False
        except Exception as e:
            logger.error(f"Error verificando sensor {device_id}: {e}")
            return False
    
    def send_sensor_reading(self, sensor: Sensor) -> bool:
        try:
            # Leer datos del sensor
            sensor_data = sensor.read_sensor_data()
            
            # Preparar payload para el backend (formato exacto según el endpoint)
            payload = {
                "device_id": sensor.device_id,
                "temperature": sensor_data.get("temperature"),
                "humidity": sensor_data.get("humidity"),
                "timestamp": int(datetime.now().timestamp()),  # Unix timestamp
                "extra_data": sensor_data.get("extra_data", {})
            }
            
            # Enviar al backend
            url = f"{self.config.backend_url}{self.config.ingest_endpoint}"
            headers = {"X-Ingest-Token": self.config.ingest_token}
            
            logger.info(f"Enviando datos a: {url}")
            logger.info(f"Payload: {payload}")
            
            # Crear request
            data = json.dumps(payload).encode('utf-8')
            req = urllib.request.Request(
                url, 
                data=data,
                headers={
                    **headers,
                    'Content-Type': 'application/json'
                }
            )
            
            with urllib.request.urlopen(req, timeout=30) as response:
                response_text = response.read().decode('utf-8')
                logger.info(f"Respuesta del servidor: {response.status} - {response_text}")
                
                if response.status == 200:
                    self.stats["successful_sends"] += 1
                    logger.info(f"Datos enviados: {sensor.device_id} - Temp: {payload.get('temperature')}°C, Hum: {payload.get('humidity')}%")
                    return True
                else:
                    self.stats["failed_sends"] += 1
                    logger.error(f"Error enviando datos: {response.status} - {sensor.device_id} - {response_text}")
                    return False
                    
        except Exception as e:
            self.stats["failed_sends"] += 1
            logger.error(f"Error en sensor {sensor.device_id}: {e}")
            return False
    
    def start_all_sensors(self):
        logger.info("Iniciando sensores simulados...")
        self.running = True
        
        while self.running:
            for sensor in self.sensors.values():
                if isinstance(sensor, SimulatedSensor):
                    self.send_sensor_reading(sensor)
                    self.stats["total_readings"] += 1
                    time.sleep(1)  # Pequeña pausa entre sensores
            
            # Pausa entre ciclos completos
            logger.info(f"Estadísticas: {self.get_stats()}")
            time.sleep(30)  # 30 segundos entre ciclos
    
    def shutdown(self):
        logger.info("Cerrando sistema IoT...")
        self.running = False
        logger.info("Sistema IoT cerrado")
    
    def get_stats(self) -> Dict[str, Any]:
        return {
            **self.stats,
            "sensors_registered": len(self.sensors),
            "running": self.running
        }

def main():
    logger.info("=== SISTEMA IoT HÍBRIDO (Versión Simple) ===")
    logger.info("Sensores simulados: Python (este programa)")
    
    # Crear gestor IoT
    iot_manager = IoTManager()
    
    try:
        # Inicializar sistema
        if not iot_manager.initialize():
            logger.error("Error inicializando sistema IoT")
            return
        
        # Mostrar estadísticas iniciales
        logger.info(f"Estadísticas iniciales: {iot_manager.get_stats()}")
        
        # Iniciar sensores simulados
        iot_manager.start_all_sensors()
        
    except KeyboardInterrupt:
        logger.info("Sistema detenido por el usuario")
    except Exception as e:
        logger.error(f"Error en sistema IoT: {e}")
    finally:
        iot_manager.shutdown()

if __name__ == "__main__":
    main()
