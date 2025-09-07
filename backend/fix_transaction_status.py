#!/usr/bin/env python3
"""
Script para arreglar el estado 'completed' en las transacciones.
Actualiza el enum en la base de datos para incluir 'completed'.
"""

import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from app.database import SessionLocal, engine
from sqlalchemy import text

def fix_transaction_status():
    """Añade 'completed' al enum de estados de transacción."""
    db = SessionLocal()
    try:
        # Verificar qué valores existen actualmente
        result = db.execute(text("SELECT DISTINCT status FROM transactions")).fetchall()
        print("Estados actuales en la BD:", [r[0] for r in result])
        
        # Añadir 'completed' al enum si no existe
        db.execute(text("""
            ALTER TYPE transactionstatusenum ADD VALUE IF NOT EXISTS 'completed';
        """))
        
        db.commit()
        print("✅ Estado 'completed' añadido al enum TransactionStatusEnum")
        
        # Verificar que se añadió correctamente
        result = db.execute(text("SELECT unnest(enum_range(NULL::transactionstatusenum))")).fetchall()
        print("Estados disponibles ahora:", [r[0] for r in result])
        
    except Exception as e:
        print(f"❌ Error: {e}")
        db.rollback()
    finally:
        db.close()

if __name__ == "__main__":
    fix_transaction_status()
