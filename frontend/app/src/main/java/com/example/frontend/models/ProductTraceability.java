package com.example.frontend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProductTraceability implements Serializable {
    @SerializedName("product_id")
    private Integer productId;
    
    @SerializedName("product_name")
    private String productName;
    
    @SerializedName("product_description")
    private String productDescription;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("is_eco")
    private Boolean isEco;
    
    @SerializedName("origin")
    private OriginInfo origin;
    
    @SerializedName("current_conditions")
    private CurrentConditions currentConditions;
    
    @SerializedName("certifications")
    private Certifications certifications;
    
    @SerializedName("traceability_events")
    private List<TraceabilityEvent> traceabilityEvents;
    
    @SerializedName("blockchain_verification")
    private BlockchainVerification blockchainVerification;
    
    @SerializedName("qr_info")
    private QRInfo qrInfo;

    public ProductTraceability() {
    }

    // Clases internas
    public static class OriginInfo implements Serializable {
        @SerializedName("producer_name")
        private String producerName;
        
        @SerializedName("producer_location")
        private String producerLocation;
        
        @SerializedName("entity_name")
        private String entityName;

        public String getProducerName() { return producerName; }
        public void setProducerName(String producerName) { this.producerName = producerName; }
        
        public String getProducerLocation() { return producerLocation; }
        public void setProducerLocation(String producerLocation) { this.producerLocation = producerLocation; }
        
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
    }

    public static class CurrentConditions implements Serializable {
        @SerializedName("temperature")
        private Double temperature;
        
        @SerializedName("humidity")
        private Double humidity;
        
        @SerializedName("last_updated")
        private String lastUpdated;

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        
        public Double getHumidity() { return humidity; }
        public void setHumidity(Double humidity) { this.humidity = humidity; }
        
        public String getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    public static class Certifications implements Serializable {
        @SerializedName("eco_certified")
        private Boolean ecoCertified;
        
        @SerializedName("local_product")
        private Boolean localProduct;
        
        @SerializedName("organic")
        private Boolean organic;

        public Boolean getEcoCertified() { return ecoCertified; }
        public void setEcoCertified(Boolean ecoCertified) { this.ecoCertified = ecoCertified; }
        
        public Boolean getLocalProduct() { return localProduct; }
        public void setLocalProduct(Boolean localProduct) { this.localProduct = localProduct; }
        
        public Boolean getOrganic() { return organic; }
        public void setOrganic(Boolean organic) { this.organic = organic; }
    }

    public static class TraceabilityEvent implements Serializable {
        @SerializedName("timestamp")
        private String timestamp;
        
        @SerializedName("event")
        private String event;
        
        @SerializedName("location")
        private String location;
        
        @SerializedName("hash")
        private String hash;
        
        @SerializedName("amount")
        private String amount;

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
        
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
    }

    public static class BlockchainVerification implements Serializable {
        @SerializedName("verified")
        private Boolean verified;
        
        @SerializedName("total_logs")
        private Integer totalLogs;
        
        @SerializedName("last_verification")
        private String lastVerification;

        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
        
        public Integer getTotalLogs() { return totalLogs; }
        public void setTotalLogs(Integer totalLogs) { this.totalLogs = totalLogs; }
        
        public String getLastVerification() { return lastVerification; }
        public void setLastVerification(String lastVerification) { this.lastVerification = lastVerification; }
    }

    public static class QRInfo implements Serializable {
        @SerializedName("hash")
        private String hash;
        
        @SerializedName("created_at")
        private String createdAt;
        
        @SerializedName("metadata")
        private Map<String, Object> metadata;

        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
        
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    // Getters principales
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getCategory() {
        return category;
    }

    public Boolean getIsEco() {
        return isEco;
    }

    public OriginInfo getOrigin() {
        return origin;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    public Certifications getCertifications() {
        return certifications;
    }

    public List<TraceabilityEvent> getTraceabilityEvents() {
        return traceabilityEvents;
    }

    public BlockchainVerification getBlockchainVerification() {
        return blockchainVerification;
    }

    public QRInfo getQrInfo() {
        return qrInfo;
    }

    // Setters principales
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsEco(Boolean isEco) {
        this.isEco = isEco;
    }

    public void setOrigin(OriginInfo origin) {
        this.origin = origin;
    }

    public void setCurrentConditions(CurrentConditions currentConditions) {
        this.currentConditions = currentConditions;
    }

    public void setCertifications(Certifications certifications) {
        this.certifications = certifications;
    }

    public void setTraceabilityEvents(List<TraceabilityEvent> traceabilityEvents) {
        this.traceabilityEvents = traceabilityEvents;
    }

    public void setBlockchainVerification(BlockchainVerification blockchainVerification) {
        this.blockchainVerification = blockchainVerification;
    }

    public void setQrInfo(QRInfo qrInfo) {
        this.qrInfo = qrInfo;
    }

    // Métodos de utilidad
    public boolean isEcoCertified() {
        return certifications != null && Boolean.TRUE.equals(certifications.getEcoCertified());
    }

    public boolean isLocalProduct() {
        return certifications != null && Boolean.TRUE.equals(certifications.getLocalProduct());
    }

    public boolean isOrganic() {
        return certifications != null && Boolean.TRUE.equals(certifications.getOrganic());
    }

    public String getCurrentTemperature() {
        return currentConditions != null && currentConditions.getTemperature() != null 
            ? String.format("%.1f°C", currentConditions.getTemperature()) 
            : "No disponible";
    }

    public String getCurrentHumidity() {
        return currentConditions != null && currentConditions.getHumidity() != null 
            ? String.format("%.1f%%", currentConditions.getHumidity()) 
            : "No disponible";
    }

    public String getProducerName() {
        return origin != null ? origin.getProducerName() : "Productor no disponible";
    }

    public String getOriginLocation() {
        return origin != null ? origin.getProducerLocation() : "Origen no disponible";
    }

    public List<String> getTraceabilityEventsAsStrings() {
        if (traceabilityEvents == null) return null;
        
        // Convertir eventos a strings para compatibilidad
        return traceabilityEvents.stream()
            .map(event -> event.getEvent())
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public String toString() {
        return "ProductTraceability{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", isEco=" + isEco +
                '}';
    }
}
