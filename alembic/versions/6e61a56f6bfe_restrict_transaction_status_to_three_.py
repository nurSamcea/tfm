"""restrict_transaction_status_to_three_states

Revision ID: 6e61a56f6bfe
Revises: cd93520d39db
Create Date: 2025-09-07 10:10:42.087589

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '6e61a56f6bfe'
down_revision: Union[str, None] = 'cd93520d39db'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
