"""Update pending transactions to in_progress

Revision ID: update_pending_status
Revises: 
Create Date: 2024-01-01 00:00:00.000000

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = 'update_pending_status'
down_revision = None
branch_labels = None
depends_on = None

def upgrade():
    """Update all pending transactions to in_progress status"""
    # Actualizar todas las transacciones con estado 'pending' a 'in_progress'
    op.execute("UPDATE transactions SET status = 'in_progress' WHERE status = 'pending'")
    
    # Verificar que no queden transacciones con estado 'pending'
    result = op.get_bind().execute("SELECT COUNT(*) FROM transactions WHERE status = 'pending'")
    pending_count = result.scalar()
    
    if pending_count > 0:
        print(f"Warning: {pending_count} transactions still have 'pending' status")
    else:
        print("All pending transactions successfully updated to in_progress")

def downgrade():
    """This migration cannot be easily reversed"""
    # No se puede revertir fácilmente porque no sabemos cuáles eran originalmente 'pending'
    # vs cuáles eran 'in_progress'
    pass
