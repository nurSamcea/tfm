"""update_pending_status

Revision ID: 64e48e489227
Revises: cf1bce2619b5
Create Date: 2025-09-07 10:40:41.145375

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '64e48e489227'
down_revision: Union[str, None] = 'cf1bce2619b5'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
