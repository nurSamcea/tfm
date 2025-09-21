"""Remove QR table and functionality

Revision ID: remove_qr_functionality
Revises: fed25a80981b
Create Date: 2024-12-19 12:00:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = 'remove_qr_functionality'
down_revision: Union[str, None] = 'fed25a80981b'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Remove QR table and functionality."""
    # Eliminar la tabla qrs si existe
    op.drop_table('qrs')


def downgrade() -> None:
    """Recreate QR table (if needed for rollback)."""
    # Recrear la tabla qrs
    op.create_table('qrs',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('product_id', sa.Integer(), nullable=True),
        sa.Column('qr_hash', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(), nullable=True),
        sa.Column('qr_metadata', sa.JSON(), nullable=True),
        sa.Column('qr_type', sa.Text(), nullable=True),
        sa.ForeignKeyConstraint(['product_id'], ['products.id'], ),
        sa.PrimaryKeyConstraint('id')
    )
