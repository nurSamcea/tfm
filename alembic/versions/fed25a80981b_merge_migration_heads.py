"""merge_migration_heads

Revision ID: fed25a80981b
Revises: add_is_hidden_simple, add_is_hidden_products
Create Date: 2025-09-06 15:51:41.467484

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'fed25a80981b'
down_revision: Union[str, None] = ('add_is_hidden_simple', 'add_is_hidden_products')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
