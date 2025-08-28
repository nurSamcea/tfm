#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Simulador IoT de lecturas de temperatura y humedad.

Envia lecturas periódicas al endpoint del backend /sensor_readings/.
Configurable por argumentos o variables de entorno.

Uso rápido (PowerShell / CMD):
  python iot/iot_simulator.py --backend http://127.0.0.1:8000 --product 1 --device ESP32_SIM_001 --interval 10

Con token JWT (si tu endpoint requiere auth):
  python iot/iot_simulator.py --backend http://127.0.0.1:8000 --product 1 --token "<JWT>"

Variables de entorno soportadas:
  IOT_BACKEND_URL, IOT_PRODUCT_ID, IOT_DEVICE_ID, IOT_INTERVAL_S, IOT_TOKEN
"""

import argparse
import os
import random
import signal
import sys
import time
from datetime import datetime
from typing import Optional

import requests


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Simulador de lecturas IoT (temperatura/humedad)")
    parser.add_argument("--backend", default=os.getenv("IOT_BACKEND_URL", "http://127.0.0.1:8000"), help="URL base del backend (por ejemplo, http://127.0.0.1:8000)")
    parser.add_argument("--endpoint", default=os.getenv("IOT_ENDPOINT_PATH", "/sensor_readings/"), help="Ruta del endpoint de lecturas")
    parser.add_argument("--product", type=int, default=int(os.getenv("IOT_PRODUCT_ID", "1")), help="ID del producto a asociar")
    parser.add_argument("--device", default=os.getenv("IOT_DEVICE_ID", "ESP32_SIM_001"), help="Identificador del dispositivo")
    parser.add_argument("--interval", type=float, default=float(os.getenv("IOT_INTERVAL_S", "10")), help="Intervalo de envío en segundos")
    parser.add_argument("--token", default=os.getenv("IOT_TOKEN"), help="Token JWT (opcional) para Authorization: Bearer <token>")
    parser.add_argument("--temp-base", type=float, default=float(os.getenv("IOT_TEMP_BASE", "6.0")), help="Temperatura base para la simulación")
    parser.add_argument("--hum-base", type=float, default=float(os.getenv("IOT_HUM_BASE", "75.0")), help="Humedad base para la simulación")
    parser.add_argument("--jitter", type=float, default=float(os.getenv("IOT_JITTER", "1.5")), help="Variación aleatoria (±) aplicada a temperatura y humedad")
    parser.add_argument("--print-only", action="store_true", help="No enviar, solo imprimir lecturas simuladas")
    return parser.parse_args()


class GracefulExit:
    def __init__(self):
        self._stop = False
        signal.signal(signal.SIGINT, self._handler)
        signal.signal(signal.SIGTERM, self._handler)

    def _handler(self, *_):
        self._stop = True

    @property
    def stop(self) -> bool:
        return self._stop


def generate_reading(temp_base: float, hum_base: float, jitter: float) -> tuple[float, float]:
    temp = round(random.uniform(temp_base - jitter, temp_base + jitter), 2)
    hum = round(random.uniform(hum_base - (jitter * 4), hum_base + (jitter * 4)), 2)
    return temp, hum


def build_headers(token: Optional[str]) -> dict:
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    return headers


def send_reading(base_url: str, endpoint: str, product_id: int, device_id: str, temperature: float, humidity: float, token: Optional[str]) -> tuple[int, Optional[str]]:
    url = base_url.rstrip("/") + "/" + endpoint.strip("/") + "/"
    payload = {
        "product_id": product_id,
        "temperature": temperature,
        "humidity": humidity,
        "sensor_type": "temperature",
        "source_device": device_id,
    }
    try:
        resp = requests.post(url, json=payload, headers=build_headers(token), timeout=15)
        text = None
        try:
            text = resp.text
        except Exception:
            pass
        return resp.status_code, text
    except requests.RequestException as e:
        return 0, str(e)


def main():
    args = parse_args()
    stopper = GracefulExit()

    print("\n=== IoT Simulator ===")
    print(f"Backend: {args.backend}")
    print(f"Endpoint: {args.endpoint}")
    print(f"Producto: {args.product}")
    print(f"Dispositivo: {args.device}")
    print(f"Intervalo: {args.interval}s")
    if args.token:
        print("Auth: Bearer <token> habilitado")
    else:
        print("Auth: sin token (endpoint público o deshabilitado)")
    print("Presiona Ctrl+C para detener.\n")

    while not stopper.stop:
        ts = datetime.utcnow().isoformat()
        temperature, humidity = generate_reading(args.temp_base, args.hum_base, args.jitter)
        print(f"[{ts}] T={temperature}°C  H={humidity}%  device={args.device}")

        if not args.print_only:
            status, body = send_reading(args.backend, args.endpoint, args.product, args.device, temperature, humidity, args.token)
            if status == 0:
                print(f"  -> Error de red: {body}")
            elif 200 <= status < 300:
                print(f"  -> Enviado OK ({status})")
            else:
                print(f"  -> Respuesta {status}: {body}")

        time.sleep(max(0.1, args.interval))

    print("\nSimulador detenido.")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nInterrumpido por el usuario.")
        sys.exit(0)
