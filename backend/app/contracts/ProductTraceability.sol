// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract ProductTraceability {
    struct Product {
        uint256 id;
        address provider;
        string name;
        string category;
        bool isEco;
        uint256 timestamp;
        string location;
        bool isActive;
    }

    struct Transaction {
        uint256 id;
        address buyer;
        uint256 productId;
        uint256 quantity;
        uint256 price;
        uint256 timestamp;
    }

    struct ProductHistory {
        uint256 productId;
        uint256[] transactionIds;
        address[] previousOwners;
    }

    // Mapeos para almacenar los datos
    mapping(uint256 => Product) public products;
    mapping(uint256 => Transaction) public transactions;
    mapping(uint256 => ProductHistory) public productHistories;
    mapping(address => uint256[]) public providerProducts;
    mapping(address => uint256[]) public userTransactions;

    // Eventos
    event ProductRegistered(uint256 indexed productId, address indexed provider);
    event TransactionRecorded(uint256 indexed transactionId, uint256 indexed productId, address indexed buyer);
    event ProductVerified(uint256 indexed productId, bool isAuthentic);

    // Modificadores
    modifier onlyProvider(uint256 productId) {
        require(products[productId].provider == msg.sender, "Only the provider can perform this action");
        _;
    }

    modifier productExists(uint256 productId) {
        require(products[productId].isActive, "Product does not exist");
        _;
    }

    // Funciones principales
    function registerProduct(
        uint256 productId,
        string memory name,
        string memory category,
        bool isEco,
        string memory location
    ) public {
        require(!products[productId].isActive, "Product already exists");
        
        products[productId] = Product({
            id: productId,
            provider: msg.sender,
            name: name,
            category: category,
            isEco: isEco,
            timestamp: block.timestamp,
            location: location,
            isActive: true
        });

        productHistories[productId] = ProductHistory({
            productId: productId,
            transactionIds: new uint256[](0),
            previousOwners: new address[](0)
        });

        providerProducts[msg.sender].push(productId);
        
        emit ProductRegistered(productId, msg.sender);
    }

    function recordTransaction(
        uint256 transactionId,
        uint256 productId,
        uint256 quantity,
        uint256 price
    ) public productExists(productId) {
        require(transactions[transactionId].timestamp == 0, "Transaction already exists");
        
        transactions[transactionId] = Transaction({
            id: transactionId,
            buyer: msg.sender,
            productId: productId,
            quantity: quantity,
            price: price,
            timestamp: block.timestamp
        });

        ProductHistory storage history = productHistories[productId];
        history.transactionIds.push(transactionId);
        history.previousOwners.push(msg.sender);

        userTransactions[msg.sender].push(transactionId);
        
        emit TransactionRecorded(transactionId, productId, msg.sender);
    }

    function verifyProduct(uint256 productId) public view returns (bool, string memory) {
        if (!products[productId].isActive) {
            return (false, "Product does not exist");
        }

        ProductHistory storage history = productHistories[productId];
        if (history.transactionIds.length == 0) {
            return (true, "Product is authentic and has not been sold yet");
        }

        // Verificar la cadena de propiedad
        address currentOwner = history.previousOwners[history.previousOwners.length - 1];
        if (currentOwner != msg.sender) {
            return (false, "You are not the current owner of this product");
        }

        return (true, "Product is authentic and verified");
    }

    function getProductHistory(uint256 productId) public view returns (
        uint256[] memory transactionIds,
        address[] memory previousOwners,
        uint256 timestamp
    ) {
        require(products[productId].isActive, "Product does not exist");
        
        ProductHistory storage history = productHistories[productId];
        return (
            history.transactionIds,
            history.previousOwners,
            products[productId].timestamp
        );
    }

    function getProviderProducts(address provider) public view returns (uint256[] memory) {
        return providerProducts[provider];
    }

    function getUserTransactions(address user) public view returns (uint256[] memory) {
        return userTransactions[user];
    }
} 