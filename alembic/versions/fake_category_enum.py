"""fake_category_enum

Revision ID: fake_category_enum
Revises: fed25a80981b
Create Date: 2025-09-06 16:05:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = 'fake_category_enum'
down_revision: Union[str, None] = 'fed25a80981b'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    # Esta migración es solo para marcar que el cambio ya se aplicó
    # El cambio real se hizo con el script fix_category.py
    pass


def downgrade() -> None:
    """Downgrade schema."""
    # Esta migración es solo para marcar que el cambio ya se aplicó
    pass

