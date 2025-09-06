"""Add is_hidden field to products table (simple)

Revision ID: add_is_hidden_simple
Revises: 
Create Date: 2025-01-03 19:00:00.000000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'add_is_hidden_simple'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # Añadir columna is_hidden a la tabla products si no existe
    try:
        op.add_column('products', sa.Column('is_hidden', sa.Boolean(), nullable=True, server_default='false'))
    except Exception:
        # La columna ya existe, no hacer nada
        pass


def downgrade():
    # Eliminar columna is_hidden de la tabla products
    try:
        op.drop_column('products', 'is_hidden')
    except Exception:
        # La columna no existe, no hacer nada
        pass
