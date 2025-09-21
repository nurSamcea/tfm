"""add_pending_status_to_transaction_enum

Revision ID: cf1bce2619b5
Revises: 6e61a56f6bfe
Create Date: 2025-09-07 10:26:41.715304

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'cf1bce2619b5'
down_revision: Union[str, None] = '6e61a56f6bfe'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
