"""Add IoT sensor models simplified

Revision ID: 09e52dc1068b
Revises: 16df8107a116
Create Date: 2025-09-07 14:09:13.150077

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '09e52dc1068b'
down_revision: Union[str, None] = '16df8107a116'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
