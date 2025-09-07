#!/usr/bin/env python3
"""
Script maestro para configurar completamente la base de datos.
Ejecuta todos los scripts necesarios en el orden correcto.
"""

import os
import sys
import subprocess
import time

def run_script(script_name, description):
    """Ejecutar un script de Python."""
    print(f"\n{'='*60}")
    print(f"🚀 {description}")
    print(f"{'='*60}")
    
    try:
        result = subprocess.run([sys.executable, script_name], 
                              capture_output=True, text=True, cwd=os.getcwd())
        
        if result.returncode == 0:
            print(f"✅ {description} - COMPLETADO")
            if result.stdout:
                print(result.stdout)
        else:
            print(f"❌ {description} - ERROR")
            print(f"Error: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"❌ Error ejecutando {script_name}: {e}")
        return False
    
    return True

def main():
    """Función principal."""
    print("🎯 CONFIGURACIÓN COMPLETA DE BASE DE DATOS")
    print("=" * 60)
    print("Este script configurará completamente la base de datos con:")
    print("  - Usuarios básicos (farmers, consumers, supermarkets)")
    print("  - Sensores para cada farmer")
    print("  - Zonas de sensores")
    print("  - Lecturas de sensores de ejemplo")
    print("=" * 60)
    
    # Lista de scripts a ejecutar en orden
    scripts = [
        ("populate_users_basic.py", "Creando usuarios básicos"),
        ("populate_sensors_data.py", "Creando sensores y datos de IoT"),
    ]
    
    success_count = 0
    
    for script_name, description in scripts:
        if os.path.exists(script_name):
            if run_script(script_name, description):
                success_count += 1
            else:
                print(f"\n❌ FALLO en {script_name}. Deteniendo ejecución.")
                break
        else:
            print(f"⚠️  Script {script_name} no encontrado, saltando...")
    
    print(f"\n{'='*60}")
    print("📊 RESUMEN FINAL")
    print(f"{'='*60}")
    
    if success_count == len(scripts):
        print("🎉 ¡CONFIGURACIÓN COMPLETA EXITOSA!")
        print("\n📋 Lo que se ha configurado:")
        print("   ✅ Usuarios básicos (farmers, consumers, supermarkets)")
        print("   ✅ Sensores para farmers (20, 50, 100 sensores respectivamente)")
        print("   ✅ Zonas de sensores por farmer")
        print("   ✅ Lecturas de sensores de ejemplo")
        print("\n🔧 Próximos pasos:")
        print("   1. Verificar que el backend esté ejecutándose")
        print("   2. Probar la conexión desde la aplicación Android")
        print("   3. Verificar que los sensores aparecen en la app")
    else:
        print(f"⚠️  Configuración parcial: {success_count}/{len(scripts)} scripts completados")
        print("   Revisa los errores anteriores y ejecuta manualmente los scripts fallidos")
    
    print(f"\n{'='*60}")

if __name__ == "__main__":
    main()
