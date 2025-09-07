# Actualización Automática de Sensores

## Descripción

Se ha implementado un sistema de actualización automática para los sensores en la aplicación Android. La app ahora se actualiza automáticamente cada 30 segundos (configurable) y muestra la última hora de actualización en la pantalla.

## Características Implementadas

### 1. Actualización Automática en la App
- **Intervalo por defecto**: 30 segundos
- **Configuración flexible**: Se puede cambiar el intervalo o deshabilitar
- **Gestión del ciclo de vida**: Se pausa cuando la app no está visible
- **Indicador visual**: Muestra la última hora de actualización

### 2. Generación de Datos en Tiempo Real
- **Script mejorado**: `populate_sensors_data.py` genera datos más frecuentemente
- **Script independiente**: `generate_realtime_data.py` para generar datos continuos
- **Configuración flexible**: Intervalos y duración personalizables

## Archivos Modificados

### Frontend (Android)
- `FarmerMetricsFragment.java`: Implementación de actualización automática
- `fragment_farmer_metrics.xml`: Agregado TextView para última actualización
- `RefreshConfig.java`: Configuración de intervalos de actualización

### Backend (Scripts)
- `populate_sensors_data.py`: Mejorado para generar datos más frecuentemente
- `generate_realtime_data.py`: Nuevo script para datos en tiempo real

## Uso

### 1. Ejecutar el Script de Población de Datos

```bash
# Poblar la base de datos con sensores y datos históricos
python populate_sensors_data.py
```

### 2. Generar Datos en Tiempo Real

```bash
# Generar datos continuamente (por defecto 30 segundos de intervalo)
python generate_realtime_data.py

# Con parámetros personalizados
python generate_realtime_data.py --duration 60 --interval 15 --farmer-id 2
```

**Parámetros disponibles:**
- `--duration`: Duración en minutos (opcional, por defecto indefinida)
- `--interval`: Intervalo entre lecturas en segundos (por defecto 30)
- `--farmer-id`: ID del farmer específico (opcional, por defecto todos)

### 3. Configurar la Actualización en la App

La actualización automática está habilitada por defecto con un intervalo de 30 segundos. Para cambiar la configuración:

```java
// En el código Java
FarmerMetricsFragment fragment = (FarmerMetricsFragment) getSupportFragmentManager()
    .findFragmentByTag("farmer_metrics");

// Cambiar a 15 segundos
fragment.updateRefreshConfig(true, 15);

// Deshabilitar actualización automática
fragment.updateRefreshConfig(false, 0);
```

## Configuración de Intervalos

Los intervalos disponibles están definidos en `RefreshConfig.java`:

- **15 segundos**: `RefreshConfig.INTERVAL_15_SECONDS`
- **30 segundos**: `RefreshConfig.INTERVAL_30_SECONDS` (por defecto)
- **1 minuto**: `RefreshConfig.INTERVAL_1_MINUTE`
- **2 minutos**: `RefreshConfig.INTERVAL_2_MINUTES`
- **5 minutos**: `RefreshConfig.INTERVAL_5_MINUTES`

## Comportamiento

### En la App
1. **Al abrir la pantalla**: Se cargan los datos inmediatamente
2. **Actualización automática**: Cada 30 segundos (configurable)
3. **Al hacer swipe**: Se actualiza manualmente
4. **Al pausar la app**: Se detiene la actualización automática
5. **Al reanudar**: Se reinicia la actualización automática

### En el Script
1. **Datos históricos**: Se generan cada 5 minutos para los últimos días
2. **Datos en tiempo real**: Se generan cada 30 segundos (configurable)
3. **Progreso**: Se muestra cada 2 minutos
4. **Interrupción**: Ctrl+C para detener

## Monitoreo

### En la App
- **Timestamp**: Se muestra "Última actualización: HH:MM" en la parte superior
- **Logs**: Se registran todas las actualizaciones en el log de Android

### En el Script
- **Progreso**: Se muestra cada 2 minutos
- **Estadísticas**: Total de lecturas generadas y tiempo transcurrido
- **Errores**: Se capturan y muestran errores detallados

## Recomendaciones

1. **Para desarrollo**: Usar intervalos cortos (15-30 segundos)
2. **Para producción**: Usar intervalos más largos (1-5 minutos)
3. **Para pruebas**: Ejecutar el script de tiempo real en paralelo con la app
4. **Para monitoreo**: Revisar los logs de Android para verificar actualizaciones

## Solución de Problemas

### Error: "duplicate key value violates unique constraint"
Este error ocurre cuando intentas ejecutar el script de población y ya existen sensores con los mismos `device_id`. 

**Solución:**
1. **Opción 1 - Limpiar datos existentes:**
   ```bash
   python populate_sensors_data.py
   # Cuando pregunte, responde 's' para limpiar datos existentes
   ```

2. **Opción 2 - Solo generar lecturas (recomendado):**
   ```bash
   # Usar el script que solo genera lecturas para sensores existentes
   python generate_data_only.py --interval 30 --farmer-id 2
   ```

3. **Opción 3 - Verificar sensores existentes:**
   ```bash
   # El script modificado ahora verifica duplicados automáticamente
   python populate_sensors_data.py
   # Responde 'n' para no limpiar, el script saltará duplicados
   ```

### Error: Zonas duplicadas para María
Si ves que María tiene zonas duplicadas, el script ahora las detecta automáticamente y las evita.

**Solución:**
1. **Limpiar solo datos de María:**
   ```bash
   python clean_maria_data.py
   ```

2. **Luego ejecutar el script normal:**
   ```bash
   python populate_sensors_data.py
   # Responde 'n' para no limpiar (ya limpiaste manualmente)
   ```

3. **O usar el script de solo lecturas:**
   ```bash
   python generate_data_only.py --interval 30 --farmer-id 2
   ```

### La app no se actualiza automáticamente
1. Verificar que `RefreshConfig.isAutoRefreshEnabled()` sea `true`
2. Revisar los logs de Android para errores de red
3. Verificar que el backend esté ejecutándose

### El script no genera datos
1. Verificar que la base de datos esté configurada correctamente
2. Verificar que existan sensores en la base de datos
3. Revisar los logs del script para errores

### Datos no aparecen en la app
1. Verificar la conexión de red
2. Verificar que el farmer tenga sensores asignados
3. Revisar los logs de la API en el backend

## Scripts Disponibles

### 1. `populate_sensors_data.py` - Script completo
- Crea sensores, zonas y lecturas
- Verifica duplicados automáticamente
- Opción de limpiar datos existentes
- Incluye generación en tiempo real opcional

### 2. `generate_data_only.py` - Solo lecturas
- Solo genera lecturas para sensores existentes
- No crea sensores ni zonas
- Ideal para generar datos en tiempo real
- Más rápido y seguro

### 3. `generate_realtime_data.py` - Tiempo real avanzado
- Generación continua de datos
- Configuración flexible de intervalos
- Filtrado por farmer específico
