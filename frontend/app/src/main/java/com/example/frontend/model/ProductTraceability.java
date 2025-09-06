package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductTraceability {
    @SerializedName("product_id")
    private int productId;
    
    @SerializedName("product_name")
    private String productName;
    
    @SerializedName("qr_hash")
    private String qrHash;
    
    @SerializedName("farmer_name")
    private String farmerName;
    
    @SerializedName("farmer_location")
    private String farmerLocation;
    
    @SerializedName("harvest_date")
    private String harvestDate;
    
    @SerializedName("processing_date")
    private String processingDate;
    
    @SerializedName("distribution_date")
    private String distributionDate;
    
    @SerializedName("sustainability_score")
    private double sustainabilityScore;
    
    @SerializedName("certifications")
    private List<String> certifications;
    
    @SerializedName("transport_method")
    private String transportMethod;
    
    @SerializedName("storage_conditions")
    private String storageConditions;
    
    @SerializedName("blockchain_hash")
    private String blockchainHash;
    
    // Constructor por defecto para Gson
    public ProductTraceability() {}
    
    // Constructor completo
    public ProductTraceability(int productId, String productName, String qrHash, String farmerName, 
                              String farmerLocation, String harvestDate, String processingDate, 
                              String distributionDate, double sustainabilityScore, 
                              List<String> certifications, String transportMethod, 
                              String storageConditions, String blockchainHash) {
        this.productId = productId;
        this.productName = productName;
        this.qrHash = qrHash;
        this.farmerName = farmerName;
        this.farmerLocation = farmerLocation;
        this.harvestDate = harvestDate;
        this.processingDate = processingDate;
        this.distributionDate = distributionDate;
        this.sustainabilityScore = sustainabilityScore;
        this.certifications = certifications;
        this.transportMethod = transportMethod;
        this.storageConditions = storageConditions;
        this.blockchainHash = blockchainHash;
    }
    
    // Getters y Setters
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getQrHash() {
        return qrHash;
    }
    
    public void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }
    
    public String getFarmerName() {
        return farmerName;
    }
    
    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }
    
    public String getFarmerLocation() {
        return farmerLocation;
    }
    
    public void setFarmerLocation(String farmerLocation) {
        this.farmerLocation = farmerLocation;
    }
    
    public String getHarvestDate() {
        return harvestDate;
    }
    
    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }
    
    public String getProcessingDate() {
        return processingDate;
    }
    
    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }
    
    public String getDistributionDate() {
        return distributionDate;
    }
    
    public void setDistributionDate(String distributionDate) {
        this.distributionDate = distributionDate;
    }
    
    public double getSustainabilityScore() {
        return sustainabilityScore;
    }
    
    public void setSustainabilityScore(double sustainabilityScore) {
        this.sustainabilityScore = sustainabilityScore;
    }
    
    public List<String> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }
    
    public String getTransportMethod() {
        return transportMethod;
    }
    
    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }
    
    public String getStorageConditions() {
        return storageConditions;
    }
    
    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }
    
    public String getBlockchainHash() {
        return blockchainHash;
    }
    
    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }
}



