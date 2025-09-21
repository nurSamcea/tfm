"""add_completed_status_to_transaction_enum

Revision ID: cd93520d39db
Revises: df012ebb840d
Create Date: 2025-09-07 00:21:57.586444

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'cd93520d39db'
down_revision: Union[str, None] = 'df012ebb840d'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
