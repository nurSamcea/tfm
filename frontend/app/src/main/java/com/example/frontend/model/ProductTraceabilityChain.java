package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ProductTraceabilityChain {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("product_id")
    private Integer productId;
    
    @SerializedName("chain_score")
    private Double chainScore;
    
    @SerializedName("total_distance_km")
    private Double totalDistanceKm;
    
    @SerializedName("total_time_hours")
    private Double totalTimeHours;
    
    @SerializedName("quality_score")
    private Double qualityScore;
    
    @SerializedName("sustainability_score")
    private Double sustainabilityScore;
    
    @SerializedName("blockchain_verified")
    private Boolean blockchainVerified;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("updated_at")
    private Date updatedAt;
    
    @SerializedName("events")
    private List<TraceabilityEvent> events;
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public Double getChainScore() { return chainScore; }
    public void setChainScore(Double chainScore) { this.chainScore = chainScore; }
    
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    
    public Double getTotalTimeHours() { return totalTimeHours; }
    public void setTotalTimeHours(Double totalTimeHours) { this.totalTimeHours = totalTimeHours; }
    
    public Double getQualityScore() { return qualityScore; }
    public void setQualityScore(Double qualityScore) { this.qualityScore = qualityScore; }
    
    public Double getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(Double sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
    
    public Boolean getBlockchainVerified() { return blockchainVerified; }
    public void setBlockchainVerified(Boolean blockchainVerified) { this.blockchainVerified = blockchainVerified; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public List<TraceabilityEvent> getEvents() { return events; }
    public void setEvents(List<TraceabilityEvent> events) { this.events = events; }
}
