import pytest
from unittest.mock import Mock, patch
from datetime import datetime

from app.algorithms.blockchain_manager import BlockchainManager


@pytest.fixture
def blockchain_manager():
    return BlockchainManager(blockchain_url="http://localhost:8545")


@pytest.fixture
def mock_product_data():
    return {
        "id": 1,
        "name": "Test Product",
        "category": "Test Category",
        "is_eco": True,
        "provider_lat": 40.4168,
        "provider_lon": -3.7038
    }


@pytest.fixture
def mock_transaction_data():
    return {
        "id": 1,
        "items": [{
            "product_id": 1,
            "quantity": 2,
            "price": 19.99
        }]
    }


def test_create_product_record(blockchain_manager, mock_product_data):
    with patch.object(blockchain_manager.contract.functions, 'registerProduct') as mock_register:
        # Configurar el mock
        mock_tx = Mock()
        mock_tx.build_transaction.return_value = {"from": "0x123", "nonce": 1}
        mock_register.return_value = mock_tx

        # Configurar el mock de Web3
        blockchain_manager.w3.eth.account.sign_transaction = Mock(return_value=Mock(rawTransaction=b"test"))
        blockchain_manager.w3.eth.send_raw_transaction = Mock(return_value=b"0x123")
        blockchain_manager.w3.eth.wait_for_transaction_receipt = Mock(return_value=Mock(
            blockNumber=1,
            status=1
        ))

        # Ejecutar la función
        result = blockchain_manager.create_product_record(
            product_data=mock_product_data,
            provider_id=1,
            private_key="test_key"
        )

        # Verificar el resultado
        assert result["transaction_hash"] == "0x123"
        assert result["block_number"] == 1
        assert result["status"] == 1


def test_create_transaction_record(blockchain_manager, mock_transaction_data):
    with patch.object(blockchain_manager.contract.functions, 'recordTransaction') as mock_record:
        # Configurar el mock
        mock_tx = Mock()
        mock_tx.build_transaction.return_value = {"from": "0x123", "nonce": 1}
        mock_record.return_value = mock_tx

        # Configurar el mock de Web3
        blockchain_manager.w3.eth.account.sign_transaction = Mock(return_value=Mock(rawTransaction=b"test"))
        blockchain_manager.w3.eth.send_raw_transaction = Mock(return_value=b"0x123")
        blockchain_manager.w3.eth.wait_for_transaction_receipt = Mock(return_value=Mock(
            blockNumber=1,
            status=1
        ))

        # Ejecutar la función
        result = blockchain_manager.create_transaction_record(
            transaction_data=mock_transaction_data,
            user_id=1,
            private_key="test_key"
        )

        # Verificar el resultado
        assert result["transaction_hash"] == "0x123"
        assert result["block_number"] == 1
        assert result["status"] == 1


def test_verify_product_authenticity(blockchain_manager):
    with patch.object(blockchain_manager.contract.functions, 'verifyProduct') as mock_verify:
        # Configurar el mock
        mock_verify.return_value.call.return_value = (True, "Product is authentic")

        # Ejecutar la función
        is_authentic, details = blockchain_manager.verify_product_authenticity(
            product_id=1,
            provider_id=1
        )

        # Verificar el resultado
        assert is_authentic is True
        assert "message" in details


def test_get_product_history(blockchain_manager):
    with patch.object(blockchain_manager.contract.functions, 'getProductHistory') as mock_history:
        # Configurar el mock
        mock_history.return_value.call.return_value = (
            [1, 2],  # transaction_ids
            ["0x123", "0x456"],  # previous_owners
            1234567890  # timestamp
        )

        # Configurar el mock de transactions
        mock_tx = Mock()
        mock_tx.call.return_value = (1, "0x123", 1, 2, 1000000000000000000, 1234567890)
        blockchain_manager.contract.functions.transactions = Mock(return_value=mock_tx)

        # Ejecutar la función
        history = blockchain_manager.get_product_history(product_id=1)

        # Verificar el resultado
        assert history["product_id"] == 1
        assert len(history["transactions"]) == 2
        assert len(history["previous_owners"]) == 2


def test_error_handling(blockchain_manager, mock_product_data):
    with patch.object(blockchain_manager.contract.functions, 'registerProduct') as mock_register:
        # Configurar el mock para lanzar una excepción
        mock_register.side_effect = Exception("Test error")

        # Verificar que la excepción se propaga correctamente
        with pytest.raises(Exception) as exc_info:
            blockchain_manager.create_product_record(
                product_data=mock_product_data,
                provider_id=1,
                private_key="test_key"
            )
        assert str(exc_info.value) == "Test error" 