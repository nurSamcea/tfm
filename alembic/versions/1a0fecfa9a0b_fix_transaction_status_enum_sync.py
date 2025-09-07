"""fix_transaction_status_enum_sync

Revision ID: 1a0fecfa9a0b
Revises: 64e48e489227
Create Date: 2025-09-07 11:48:42.576642

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '1a0fecfa9a0b'
down_revision: Union[str, None] = '64e48e489227'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    # 1. Primero actualizar los registros con 'completed' a 'delivered'
    op.execute("""
        UPDATE transactions 
        SET status = 'delivered' 
        WHERE status = 'completed'
    """)
    
    # 2. Actualizar los registros con 'pending' a 'in_progress'
    op.execute("""
        UPDATE transactions 
        SET status = 'in_progress' 
        WHERE status = 'pending'
    """)
    
    # 3. Crear un nuevo enum con solo los valores que necesitamos
    new_enum = sa.Enum('in_progress', 'delivered', 'cancelled', name='transactionstatusenum_new')
    new_enum.create(op.get_bind())
    
    # 4. Cambiar la columna para usar el nuevo enum
    op.execute("""
        ALTER TABLE transactions 
        ALTER COLUMN status TYPE transactionstatusenum_new 
        USING status::text::transactionstatusenum_new
    """)
    
    # 5. Eliminar el enum viejo y renombrar el nuevo
    op.execute("DROP TYPE transactionstatusenum")
    op.execute("ALTER TYPE transactionstatusenum_new RENAME TO transactionstatusenum")


def downgrade() -> None:
    """Downgrade schema."""
    # 1. Crear el enum original con todos los valores
    old_enum = sa.Enum('pending', 'in_progress', 'delivered', 'cancelled', 'completed', name='transactionstatusenum_old')
    old_enum.create(op.get_bind())
    
    # 2. Cambiar la columna para usar el enum original
    op.execute("""
        ALTER TABLE transactions 
        ALTER COLUMN status TYPE transactionstatusenum_old 
        USING status::text::transactionstatusenum_old
    """)
    
    # 3. Eliminar el enum actual y renombrar el original
    op.execute("DROP TYPE transactionstatusenum")
    op.execute("ALTER TYPE transactionstatusenum_old RENAME TO transactionstatusenum")
