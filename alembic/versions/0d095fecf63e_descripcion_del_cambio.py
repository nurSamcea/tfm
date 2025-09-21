"""Descripcion del cambio

Revision ID: 0d095fecf63e
Revises: 
Create Date: 2025-06-07 19:07:16.928400

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = '0d095fecf63e'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    # 1. Crear todos los tipos ENUM antes de usarlos
    sa.Enum('product', 'transaction', 'sensor', name='blockchainentitytypeenum').create(op.get_bind(), checkfirst=True)
    sa.Enum('temperature', 'humidity', 'gas', 'light', 'shock', 'gps', name='sensortypeenum').create(op.get_bind(), checkfirst=True)
    sa.Enum('draft', 'pending', 'paid', 'delivered', 'cancelled', name='shoppingliststatusenum').create(op.get_bind(), checkfirst=True)
    sa.Enum('pending', 'paid', 'delivered', 'cancelled', name='transactionstatusenum').create(op.get_bind(), checkfirst=True)
    sa.Enum('consumer', 'farmer', 'retailer', 'supermarket', 'admin', name='userroleenum').create(op.get_bind(), checkfirst=True)

    # 2. Eliminar tablas dependientes
    op.execute('DROP TABLE IF EXISTS nutritional_info CASCADE')
    op.execute('DROP TABLE IF EXISTS logistics_route_stops CASCADE')

    # 3. Convertir columnas a TEXT antes de ENUM (patrón seguro)
    op.execute("ALTER TABLE blockchain_logs ALTER COLUMN entity_type TYPE text")
    op.execute("ALTER TABLE shopping_lists ALTER COLUMN status TYPE text")
    op.execute("ALTER TABLE transactions ALTER COLUMN status TYPE text")
    op.execute("ALTER TABLE users ALTER COLUMN role TYPE text")

    # 4. Convertir columnas a ENUM usando CASE
    op.execute("""
        ALTER TABLE blockchain_logs
        ALTER COLUMN entity_type TYPE blockchainentitytypeenum
        USING CASE entity_type
            WHEN 'product' THEN 'product'::blockchainentitytypeenum
            WHEN 'transaction' THEN 'transaction'::blockchainentitytypeenum
            WHEN 'sensor' THEN 'sensor'::blockchainentitytypeenum
            ELSE NULL
        END
    """)
    op.execute("""
        ALTER TABLE shopping_lists
        ALTER COLUMN status TYPE shoppingliststatusenum
        USING CASE status
            WHEN 'draft' THEN 'draft'::shoppingliststatusenum
            WHEN 'pending' THEN 'pending'::shoppingliststatusenum
            WHEN 'paid' THEN 'paid'::shoppingliststatusenum
            WHEN 'delivered' THEN 'delivered'::shoppingliststatusenum
            WHEN 'cancelled' THEN 'cancelled'::shoppingliststatusenum
            ELSE NULL
        END
    """)
    op.execute("""
        ALTER TABLE transactions
        ALTER COLUMN status TYPE transactionstatusenum
        USING CASE status
            WHEN 'pending' THEN 'pending'::transactionstatusenum
            WHEN 'paid' THEN 'paid'::transactionstatusenum
            WHEN 'delivered' THEN 'delivered'::transactionstatusenum
            WHEN 'cancelled' THEN 'cancelled'::transactionstatusenum
            ELSE NULL
        END
    """)

    # 5. Para users.role, usar columna temporal (patrón robusto)
    op.add_column('users', sa.Column('role_temp', sa.Enum('consumer', 'farmer', 'retailer', 'supermarket', 'admin', name='userroleenum'), nullable=True))
    op.execute("""
        UPDATE users SET role_temp =
            CASE role
                WHEN 'consumer' THEN 'consumer'::userroleenum
                WHEN 'farmer' THEN 'farmer'::userroleenum
                WHEN 'retailer' THEN 'retailer'::userroleenum
                WHEN 'supermarket' THEN 'supermarket'::userroleenum
                WHEN 'admin' THEN 'admin'::userroleenum
                ELSE NULL
            END
    """)
    op.drop_column('users', 'role')
    op.alter_column('users', 'role_temp', new_column_name='role')

    op.alter_column('drivers', 'name',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=False)
    op.alter_column('drivers', 'phone',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.alter_column('drivers', 'email',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.alter_column('drivers', 'license_number',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.alter_column('drivers', 'location_lat',
               existing_type=sa.NUMERIC(),
               type_=sa.Float(),
               existing_nullable=True)
    op.alter_column('drivers', 'location_lon',
               existing_type=sa.NUMERIC(),
               type_=sa.Float(),
               existing_nullable=True)
    op.alter_column('drivers', 'working_hours',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.create_index(op.f('ix_drivers_id'), 'drivers', ['id'], unique=False)
    op.create_foreign_key(None, 'drivers', 'vehicles', ['vehicle_id'], ['id'])
    op.add_column('impact_metrics', sa.Column('nutritional_consumption', sa.Float(), nullable=True))
    op.alter_column('logistics_routes', 'orders_ids',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.drop_constraint(op.f('logistics_routes_vehicle_id_fkey'), 'logistics_routes', type_='foreignkey')
    op.drop_constraint(op.f('logistics_routes_driver_id_fkey'), 'logistics_routes', type_='foreignkey')
    op.drop_column('logistics_routes', 'status')
    op.drop_column('logistics_routes', 'vehicle_id')
    op.drop_column('logistics_routes', 'driver_id')
    op.drop_column('logistics_routes', 'date')
    op.add_column('products', sa.Column('certifications', sa.JSON(), nullable=True))
    op.alter_column('products', 'nutritional_info',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.drop_column('products', 'generic_name')
    op.add_column('qrs', sa.Column('qr_type', sa.Text(), nullable=True))
    op.alter_column('qrs', 'qr_metadata',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('recipe_ingredients', 'nutritional_info',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('recipes', 'steps',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('recipes', 'tags',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('recipes', 'nutrition_total',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.add_column('sensor_readings', sa.Column('sensor_type', sa.Enum('temperature', 'humidity', 'gas', 'light', 'shock', 'gps', name='sensortypeenum'), nullable=True))
    op.drop_column('shopping_list_groups', 'delivery_status')
    op.drop_column('shopping_list_groups', 'delivery_window')
    op.drop_column('shopping_list_groups', 'logistics_stop_order')
    op.alter_column('shopping_list_items', 'nutritional_info',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('users', 'preferences',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    op.drop_constraint(op.f('users_email_key'), 'users', type_='unique')
    op.create_index('ix_users_email', 'users', ['email'], unique=False)
    op.alter_column('vehicles', 'type',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.alter_column('vehicles', 'name',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.alter_column('vehicles', 'plate_number',
               existing_type=sa.TEXT(),
               type_=sa.String(),
               existing_nullable=True)
    op.create_index(op.f('ix_vehicles_id'), 'vehicles', ['id'], unique=False)
    op.alter_column('weekly_plan_items', 'nutrition_total',
               existing_type=postgresql.JSONB(astext_type=sa.Text()),
               type_=sa.JSON(),
               existing_nullable=True)
    # ### end Alembic commands ###


def downgrade() -> None:
    """Downgrade schema."""
    # 1. Revertir columnas ENUM a TEXT (patrón seguro)
    # Para users.role, usar columna temporal
    op.add_column('users', sa.Column('role_temp', sa.Text(), nullable=True))
    op.execute("""
        UPDATE users SET role_temp =
            CASE role
                WHEN 'consumer' THEN 'consumer'
                WHEN 'farmer' THEN 'farmer'
                WHEN 'retailer' THEN 'retailer'
                WHEN 'supermarket' THEN 'supermarket'
                WHEN 'admin' THEN 'admin'
                ELSE NULL
            END
    """)
    op.drop_column('users', 'role')
    op.alter_column('users', 'role_temp', new_column_name='role')

    # Para las demás columnas ENUM, convertir directamente a TEXT
    op.execute("ALTER TABLE blockchain_logs ALTER COLUMN entity_type TYPE text USING entity_type::text")
    op.execute("ALTER TABLE shopping_lists ALTER COLUMN status TYPE text USING status::text")
    op.execute("ALTER TABLE transactions ALTER COLUMN status TYPE text USING status::text")

    op.alter_column('weekly_plan_items', 'nutrition_total',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.drop_index(op.f('ix_vehicles_id'), table_name='vehicles')
    op.alter_column('vehicles', 'plate_number',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.alter_column('vehicles', 'name',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.alter_column('vehicles', 'type',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.drop_index('ix_users_email', table_name='users')
    op.create_unique_constraint(op.f('users_email_key'), 'users', ['email'], postgresql_nulls_not_distinct=False)
    op.alter_column('users', 'preferences',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.alter_column('transactions', 'status',
               existing_type=sa.Enum('pending', 'paid', 'delivered', 'cancelled', name='transactionstatusenum'),
               type_=sa.TEXT(),
               nullable=True)
    op.alter_column('shopping_lists', 'status',
               existing_type=sa.Enum('draft', 'pending', 'paid', 'delivered', 'cancelled', name='shoppingliststatusenum'),
               type_=sa.TEXT(),
               nullable=True)
    op.alter_column('shopping_list_items', 'nutritional_info',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.add_column('shopping_list_groups', sa.Column('logistics_stop_order', sa.INTEGER(), autoincrement=False, nullable=True))
    op.add_column('shopping_list_groups', sa.Column('delivery_window', postgresql.JSONB(astext_type=sa.Text()), autoincrement=False, nullable=True))
    op.add_column('shopping_list_groups', sa.Column('delivery_status', sa.TEXT(), server_default=sa.text("'not_delivered'::text"), autoincrement=False, nullable=True))
    op.drop_column('sensor_readings', 'sensor_type')
    op.alter_column('recipes', 'nutrition_total',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.alter_column('recipes', 'tags',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.alter_column('recipes', 'steps',
               existing_type=sa.JSON(),
               type_=sa.JSON(),
               existing_nullable=True)
    op.alter_column('recipe_ingredients', 'nutritional_info',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.alter_column('qrs', 'qr_metadata',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.drop_column('qrs', 'qr_type')
    op.add_column('products', sa.Column('generic_name', sa.TEXT(), autoincrement=False, nullable=True))
    op.create_foreign_key(op.f('fk_generic_name'), 'products', 'nutritional_info', ['generic_name'], ['generic_name'])
    op.alter_column('products', 'nutritional_info',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.drop_column('products', 'certifications')
    op.add_column('logistics_routes', sa.Column('date', sa.DATE(), autoincrement=False, nullable=True))
    op.add_column('logistics_routes', sa.Column('driver_id', sa.INTEGER(), autoincrement=False, nullable=True))
    op.add_column('logistics_routes', sa.Column('vehicle_id', sa.INTEGER(), autoincrement=False, nullable=True))
    op.add_column('logistics_routes', sa.Column('status', sa.TEXT(), server_default=sa.text("'pending'::text"), autoincrement=False, nullable=True))
    op.create_foreign_key(op.f('logistics_routes_driver_id_fkey'), 'logistics_routes', 'drivers', ['driver_id'], ['id'])
    op.create_foreign_key(op.f('logistics_routes_vehicle_id_fkey'), 'logistics_routes', 'vehicles', ['vehicle_id'], ['id'])
    op.alter_column('logistics_routes', 'orders_ids',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.drop_column('impact_metrics', 'nutritional_consumption')
    op.drop_constraint(None, 'drivers', type_='foreignkey')
    op.drop_index(op.f('ix_drivers_id'), table_name='drivers')
    op.alter_column('drivers', 'working_hours',
               existing_type=sa.JSON(),
               type_=postgresql.JSONB(astext_type=sa.Text()),
               existing_nullable=True)
    op.alter_column('drivers', 'location_lon',
               existing_type=sa.Float(),
               type_=sa.NUMERIC(),
               existing_nullable=True)
    op.alter_column('drivers', 'location_lat',
               existing_type=sa.Float(),
               type_=sa.NUMERIC(),
               existing_nullable=True)
    op.alter_column('drivers', 'license_number',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.alter_column('drivers', 'email',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.alter_column('drivers', 'phone',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=True)
    op.alter_column('drivers', 'name',
               existing_type=sa.String(),
               type_=sa.TEXT(),
               existing_nullable=False)
    sa.Enum('product', 'transaction', 'sensor', name='blockchainentitytypeenum').create(op.get_bind(), checkfirst=True)
    op.alter_column('blockchain_logs', 'entity_type',
               existing_type=sa.Enum('product', 'transaction', 'sensor', name='blockchainentitytypeenum'),
               type_=sa.TEXT(),
               nullable=True)
    op.create_table('nutritional_info',
    sa.Column('id', sa.INTEGER(), autoincrement=True, nullable=False),
    sa.Column('generic_name', sa.TEXT(), autoincrement=False, nullable=False),
    sa.Column('kcal', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('protein', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('carbohydrates', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('sugars', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('fat', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('saturated_fat', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('fiber', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('salt', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('cholesterol', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('calcium', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('iron', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('vitamin_c', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('vitamin_d', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('vitamin_b12', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('potassium', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('magnesium', sa.DOUBLE_PRECISION(precision=53), autoincrement=False, nullable=True),
    sa.Column('per_unit', sa.TEXT(), autoincrement=False, nullable=True),
    sa.Column('source', sa.TEXT(), autoincrement=False, nullable=True),
    sa.PrimaryKeyConstraint('id', name=op.f('nutritional_info_pkey')),
    sa.UniqueConstraint('generic_name', name=op.f('nutritional_info_generic_name_key'), postgresql_include=[], postgresql_nulls_not_distinct=False)
    )
    op.create_table('logistics_route_stops',
    sa.Column('id', sa.INTEGER(), autoincrement=True, nullable=False),
    sa.Column('logistics_route_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('stop_order', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('stop_type', sa.TEXT(), autoincrement=False, nullable=True),
    sa.Column('related_group_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('sender_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('receiver_id', sa.INTEGER(), autoincrement=False, nullable=True),
    sa.Column('location_lat', sa.NUMERIC(), autoincrement=False, nullable=True),
    sa.Column('location_lon', sa.NUMERIC(), autoincrement=False, nullable=True),
    sa.Column('eta', postgresql.TIMESTAMP(), autoincrement=False, nullable=True),
    sa.Column('status', sa.TEXT(), server_default=sa.text("'pending'::text"), autoincrement=False, nullable=True),
    sa.CheckConstraint("stop_type = ANY (ARRAY['pickup'::text, 'delivery'::text])", name=op.f('logistics_route_stops_stop_type_check')),
    sa.ForeignKeyConstraint(['logistics_route_id'], ['logistics_routes.id'], name=op.f('logistics_route_stops_logistics_route_id_fkey')),
    sa.ForeignKeyConstraint(['receiver_id'], ['users.id'], name=op.f('logistics_route_stops_receiver_id_fkey')),
    sa.ForeignKeyConstraint(['related_group_id'], ['shopping_list_groups.id'], name=op.f('logistics_route_stops_related_group_id_fkey')),
    sa.ForeignKeyConstraint(['sender_id'], ['users.id'], name=op.f('logistics_route_stops_sender_id_fkey')),
    sa.PrimaryKeyConstraint('id', name=op.f('logistics_route_stops_pkey'))
    )
    # ### end Alembic commands ###
