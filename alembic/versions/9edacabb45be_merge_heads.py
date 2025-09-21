"""merge heads

Revision ID: 9edacabb45be
Revises: c59fdf3d54e3, remove_qr_functionality
Create Date: 2025-09-21 12:14:45.947059

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '9edacabb45be'
down_revision: Union[str, None] = ('c59fdf3d54e3', 'remove_qr_functionality')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
