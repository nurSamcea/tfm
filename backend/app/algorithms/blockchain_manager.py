from typing import Dict, List, Optional, Tuple
import json
from datetime import datetime
from web3 import Web3
from eth_account import Account
from eth_account.messages import encode_defunct
import os


class BlockchainManager:
    def __init__(self, blockchain_url: str):
        self.w3 = Web3(Web3.HTTPProvider(blockchain_url))
        self.contract_address = os.getenv("CONTRACT_ADDRESS")
        self.contract_abi = self._load_contract_abi()
        self.contract = self.w3.eth.contract(
            address=self.contract_address,
            abi=self.contract_abi
        )

    def _load_contract_abi(self) -> List[Dict]:
        """
        Carga el ABI del contrato desde un archivo JSON.
        """
        try:
            with open("backend/app/contracts/ProductTraceability.json", "r") as f:
                return json.load(f)["abi"]
        except Exception as e:
            raise Exception(f"Error loading contract ABI: {str(e)}")

    def _sign_message(self, message: str, private_key: str) -> str:
        """
        Firma un mensaje usando la clave privada.
        """
        message_hash = encode_defunct(text=message)
        signed_message = self.w3.eth.account.sign_message(
            message_hash,
            private_key=private_key
        )
        return signed_message.signature.hex()

    def create_product_record(
        self,
        product_data: Dict,
        provider_id: int,
        private_key: str
    ) -> Dict:
        """
        Registra un producto en la blockchain.
        """
        try:
            # Preparar los datos para el contrato
            product_id = product_data["id"]
            name = product_data["name"]
            category = product_data.get("category", "")
            is_eco = product_data.get("is_eco", False)
            location = json.dumps({
                "lat": product_data.get("provider_lat"),
                "lon": product_data.get("provider_lon")
            })

            # Obtener la cuenta del proveedor
            provider_account = self.w3.eth.account.from_key(private_key)
            
            # Construir la transacción
            nonce = self.w3.eth.get_transaction_count(provider_account.address)
            gas_price = self.w3.eth.gas_price
            
            # Llamar al contrato
            tx = self.contract.functions.registerProduct(
                product_id,
                name,
                category,
                is_eco,
                location
            ).build_transaction({
                'from': provider_account.address,
                'nonce': nonce,
                'gas': 2000000,
                'gasPrice': gas_price
            })

            # Firmar y enviar la transacción
            signed_tx = self.w3.eth.account.sign_transaction(tx, private_key)
            tx_hash = self.w3.eth.send_raw_transaction(signed_tx.rawTransaction)
            
            # Esperar la confirmación
            tx_receipt = self.w3.eth.wait_for_transaction_receipt(tx_hash)

            return {
                "transaction_hash": tx_hash.hex(),
                "block_number": tx_receipt.blockNumber,
                "status": tx_receipt.status,
                "timestamp": datetime.utcnow().isoformat()
            }

        except Exception as e:
            raise Exception(f"Error registering product in blockchain: {str(e)}")

    def create_transaction_record(
        self,
        transaction_data: Dict,
        user_id: int,
        private_key: str
    ) -> Dict:
        """
        Registra una transacción en la blockchain.
        """
        try:
            # Obtener la cuenta del usuario
            user_account = self.w3.eth.account.from_key(private_key)
            
            # Preparar los datos para el contrato
            transaction_id = transaction_data["id"]
            product_id = transaction_data["items"][0]["product_id"]  # Asumimos una transacción por producto
            quantity = transaction_data["items"][0]["quantity"]
            price = int(transaction_data["items"][0]["price"] * 1e18)  # Convertir a wei

            # Construir la transacción
            nonce = self.w3.eth.get_transaction_count(user_account.address)
            gas_price = self.w3.eth.gas_price
            
            # Llamar al contrato
            tx = self.contract.functions.recordTransaction(
                transaction_id,
                product_id,
                quantity,
                price
            ).build_transaction({
                'from': user_account.address,
                'nonce': nonce,
                'gas': 2000000,
                'gasPrice': gas_price
            })

            # Firmar y enviar la transacción
            signed_tx = self.w3.eth.account.sign_transaction(tx, private_key)
            tx_hash = self.w3.eth.send_raw_transaction(signed_tx.rawTransaction)
            
            # Esperar la confirmación
            tx_receipt = self.w3.eth.wait_for_transaction_receipt(tx_hash)

            return {
                "transaction_hash": tx_hash.hex(),
                "block_number": tx_receipt.blockNumber,
                "status": tx_receipt.status,
                "timestamp": datetime.utcnow().isoformat()
            }

        except Exception as e:
            raise Exception(f"Error recording transaction in blockchain: {str(e)}")

    def verify_product_authenticity(
        self,
        product_id: int,
        provider_id: int
    ) -> Tuple[bool, Dict]:
        """
        Verifica la autenticidad de un producto en la blockchain.
        """
        try:
            # Obtener la dirección del proveedor
            provider_address = self.w3.eth.accounts[provider_id]  # Esto es un ejemplo, necesitarías mapear IDs a direcciones
            
            # Llamar al contrato
            is_authentic, message = self.contract.functions.verifyProduct(
                product_id
            ).call({'from': provider_address})

            return is_authentic, {
                "message": message,
                "verification_date": datetime.utcnow().isoformat()
            }

        except Exception as e:
            return False, {"error": f"Error verifying product: {str(e)}"}

    def get_product_history(
        self,
        product_id: int
    ) -> List[Dict]:
        """
        Obtiene el historial completo de un producto en la blockchain.
        """
        try:
            # Llamar al contrato
            transaction_ids, previous_owners, timestamp = self.contract.functions.getProductHistory(
                product_id
            ).call()

            # Obtener detalles de las transacciones
            transactions = []
            for tx_id in transaction_ids:
                tx = self.contract.functions.transactions(tx_id).call()
                transactions.append({
                    "id": tx[0],
                    "buyer": tx[1],
                    "product_id": tx[2],
                    "quantity": tx[3],
                    "price": float(tx[4]) / 1e18,  # Convertir de wei a ether
                    "timestamp": datetime.fromtimestamp(tx[5]).isoformat()
                })

            return {
                "product_id": product_id,
                "creation_date": datetime.fromtimestamp(timestamp).isoformat(),
                "transactions": transactions,
                "previous_owners": previous_owners
            }

        except Exception as e:
            return [{"error": f"Error getting product history: {str(e)}"}] 