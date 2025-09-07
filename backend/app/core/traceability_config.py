from pydantic import BaseSettings
from typing import Optional

class TraceabilitySettings(BaseSettings):
    """Configuración para el sistema de trazabilidad blockchain"""
    
    # Configuración de blockchain
    blockchain_url: str = "http://localhost:8545"  # URL del nodo Ethereum
    contract_address: Optional[str] = None  # Dirección del contrato inteligente
    private_key: Optional[str] = None  # Clave privada para firmar transacciones
    
    # Configuración de sensores IoT
    sensor_data_retention_days: int = 365  # Días de retención de datos de sensores
    sensor_reading_interval_seconds: int = 30  # Intervalo de lectura de sensores
    temperature_violation_threshold: float = 2.0  # Umbral de violación de temperatura
    
    # Configuración de calidad
    quality_score_weights: dict = {
        "temperature_consistency": 0.25,
        "humidity_optimal": 0.20,
        "no_shock_detected": 0.20,
        "soil_conditions": 0.20,
        "overall_reading_quality": 0.15
    }
    
    # Configuración de códigos QR
    qr_error_correction: str = "L"  # Nivel de corrección de errores (L, M, Q, H)
    qr_box_size: int = 10  # Tamaño de los cuadros del QR
    qr_border: int = 4  # Tamaño del borde del QR
    
    # Configuración de verificación
    verification_score_threshold: float = 0.8  # Umbral mínimo para verificación
    blockchain_verification_weight: float = 0.4  # Peso de verificación blockchain
    sensor_verification_weight: float = 0.3  # Peso de verificación de sensores
    quality_verification_weight: float = 0.2  # Peso de verificación de calidad
    chain_completeness_weight: float = 0.1  # Peso de completitud de cadena
    
    # Configuración de alertas
    enable_temperature_alerts: bool = True  # Habilitar alertas de temperatura
    enable_quality_alerts: bool = True  # Habilitar alertas de calidad
    enable_anomaly_alerts: bool = True  # Habilitar alertas de anomalías
    
    # Configuración de reportes
    report_generation_interval_hours: int = 24  # Intervalo de generación de reportes
    max_events_per_report: int = 1000  # Máximo de eventos por reporte
    
    # Configuración de base de datos
    traceability_events_batch_size: int = 100  # Tamaño de lote para eventos
    sensor_data_batch_size: int = 50  # Tamaño de lote para datos de sensores
    
    class Config:
        env_file = ".env"
        env_prefix = "TRACEABILITY_"

# Instancia global de configuración
traceability_settings = TraceabilitySettings()
