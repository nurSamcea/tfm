# Plataforma Digital para la Distribución Inteligente y Sostenible de Productos Frescos

Este proyecto forma parte del Trabajo Fin de Máster en Internet of Things de la Universidad Politécnica de Madrid.

## Descripción

Sistema inteligente para la distribución eficiente, sostenible y trazable de productos frescos, conectando directamente productores, pequeños comerciantes, supermercados y consumidores. El sistema se basa en una arquitectura modular escalable que integra IoT, Cloud Computing y Blockchain para optimizar la logística, garantizar la transparencia y promover la compra local.

## Características Principales

- Conexión de stakeholders clave en el ecosistema alimentario
- Optimización del proceso de compra mediante algoritmos de búsqueda y comparación
- Trazabilidad completa de productos usando blockchain
- Integración de sensores IoT para monitorización ambiental
- Seguimiento en tiempo real de la ubicación de consumidores

## Estructura del Proyecto

```
.
├── backend/           # API REST con FastAPI (coherente con IoT y Frontend)
├── frontend/         # Aplicación móvil Android (coherente con Backend)
├── iot/             # Sensores IoT (ESP32 real + Simulador Python)
├── database/        # Base de datos PostgreSQL (esquema unificado)
├── docs/            # Documentación del proyecto
├── tests/           # Tests unitarios y de integración
├── SISTEMA_UNIFICADO.md # Documentación de coherencia total
└── docker-compose.yml # Configuración de contenedores
```

## 🎯 Sistema Completamente Coherente

El proyecto mantiene **coherencia total** entre todos los componentes:

- **IoT**: ESP32 (María García) + Simulador Python (otros farmers)
- **Database**: Esquema unificado con tablas coherentes
- **Backend**: API REST con endpoints estándar
- **Frontend**: Android con modelos coherentes

Ver [SISTEMA_UNIFICADO.md](SISTEMA_UNIFICADO.md) para detalles completos.

## Requisitos

- Python 3.11+ (recomendado 3.11 para compatibilidad con dependencias)
- PostgreSQL 13+
- Android Studio
- Docker y Docker Compose
- Sensores IoT (DHT11/DHT22, GPS)

## Instalación

1. Clonar el repositorio:
```bash
git clone [URL_DEL_REPOSITORIO]
```

2. Configurar el entorno virtual con Python 3.11:
```bash
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
```

3. Instalar dependencias:
```bash
pip install -r backend/requirements.txt
```

4. Configurar la base de datos (después de instalar dependencias):
```bash
alembic upgrade head
```

5. Iniciar los servicios con Docker:
```bash
docker-compose up -d
```

## Uso

1. Iniciar el backend:
```bash
uvicorn backend.app.main:app --reload
```

2. Compilar y ejecutar la aplicación Android desde Android Studio

3. Configurar los sensores IoT según la documentación en `iot/`

4. Documentación técnica actualizada en `docs/` (arquitectura, flujo, API, blockchain, sensores, tests)

## Documentación

La documentación detallada se encuentra en el directorio `/docs/`, incluyendo:
- Arquitectura del sistema
- Guías de instalación
- Manuales de usuario
- Documentación técnica

## Autores

- Nuria Álvarez Río
- Tutores: Ana Belén García Hernando y Miguel Ángel Valero Duboy

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
