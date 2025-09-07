"""merge blockchain traceability heads

Revision ID: f08a0278ad68
Revises: d8c72689f3f7
Create Date: 2025-09-07 22:05:27.865779

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'f08a0278ad68'
down_revision: Union[str, None] = 'd8c72689f3f7'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
