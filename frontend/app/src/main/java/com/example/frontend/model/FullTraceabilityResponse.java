package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FullTraceabilityResponse {
    @SerializedName("product")
    private Product product;
    
    @SerializedName("traceability_chain")
    private ProductTraceabilityChain traceabilityChain;
    
    @SerializedName("events")
    private List<TraceabilityEvent> events;
    
    @SerializedName("summary")
    private TraceabilitySummary summary;
    
    // Getters y Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public ProductTraceabilityChain getTraceabilityChain() { return traceabilityChain; }
    public void setTraceabilityChain(ProductTraceabilityChain traceabilityChain) { this.traceabilityChain = traceabilityChain; }
    
    public List<TraceabilityEvent> getEvents() { return events; }
    public void setEvents(List<TraceabilityEvent> events) { this.events = events; }
    
    public TraceabilitySummary getSummary() { return summary; }
    public void setSummary(TraceabilitySummary summary) { this.summary = summary; }
    
    // Clase interna para el resumen
    public static class TraceabilitySummary {
        @SerializedName("total_events")
        private Integer totalEvents;
        
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
        
        // Getters y Setters
        public Integer getTotalEvents() { return totalEvents; }
        public void setTotalEvents(Integer totalEvents) { this.totalEvents = totalEvents; }
        
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
    }
}
