"""Add is_hidden field to products table

Revision ID: add_is_hidden_products
Revises: 0d095fecf63e
Create Date: 2025-01-03 19:00:00.000000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'add_is_hidden_products'
down_revision = '0d095fecf63e'
branch_labels = None
depends_on = None


def upgrade():
    # Añadir columna is_hidden a la tabla products
    op.add_column('products', sa.Column('is_hidden', sa.Boolean(), nullable=False, server_default='false'))


def downgrade():
    # Eliminar columna is_hidden de la tabla products
    op.drop_column('products', 'is_hidden')
