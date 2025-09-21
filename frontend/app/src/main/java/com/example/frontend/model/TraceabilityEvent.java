package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

public class TraceabilityEvent {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("product_id")
    private Integer productId;
    
    @SerializedName("event_type")
    private String eventType;
    
    @SerializedName("timestamp")
    private Date timestamp;
    
    @SerializedName("location_lat")
    private Double locationLat;
    
    @SerializedName("location_lon")
    private Double locationLon;
    
    @SerializedName("location_description")
    private String locationDescription;
    
    @SerializedName("actor_id")
    private Integer actorId;
    
    @SerializedName("actor_type")
    private String actorType;
    
    @SerializedName("event_data")
    private Map<String, Object> eventData;
    
    @SerializedName("blockchain_hash")
    private String blockchainHash;
    
    @SerializedName("blockchain_block_number")
    private Integer blockchainBlockNumber;
    
    @SerializedName("blockchain_tx_hash")
    private String blockchainTxHash;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("is_verified")
    private Boolean isVerified;
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    
    public Double getLocationLon() { return locationLon; }
    public void setLocationLon(Double locationLon) { this.locationLon = locationLon; }
    
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    
    public Integer getActorId() { return actorId; }
    public void setActorId(Integer actorId) { this.actorId = actorId; }
    
    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }
    
    public Map<String, Object> getEventData() { return eventData; }
    public void setEventData(Map<String, Object> eventData) { this.eventData = eventData; }
    
    public String getBlockchainHash() { return blockchainHash; }
    public void setBlockchainHash(String blockchainHash) { this.blockchainHash = blockchainHash; }
    
    public Integer getBlockchainBlockNumber() { return blockchainBlockNumber; }
    public void setBlockchainBlockNumber(Integer blockchainBlockNumber) { this.blockchainBlockNumber = blockchainBlockNumber; }
    
    public String getBlockchainTxHash() { return blockchainTxHash; }
    public void setBlockchainTxHash(String blockchainTxHash) { this.blockchainTxHash = blockchainTxHash; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
}
