package com.example.frontend.model;

import java.util.List;

public class SustainabilityMetrics {
    private float carbonFootprint;
    private float waterUsage;
    private float energyConsumption;
    private float wasteReduction;
    private float socialImpact;
    private float wasteGenerated;
    private float recyclingRate;

    public SustainabilityMetrics(float carbonFootprint, float waterUsage, float energyConsumption,
                                 float wasteReduction, float socialImpact, float wasteGenerated,
                                 float recyclingRate) {
        this.carbonFootprint = carbonFootprint;
        this.waterUsage = waterUsage;
        this.energyConsumption = energyConsumption;
        this.wasteReduction = wasteReduction;
        this.socialImpact = socialImpact;
        this.wasteGenerated = wasteGenerated;
        this.recyclingRate = recyclingRate;
    }

    public SustainabilityMetrics() {

    }

    // Getters y setters
    public float getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(float carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public float getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(float waterUsage) {
        this.waterUsage = waterUsage;
    }

    public float getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(float energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public float getWasteReduction() {
        return wasteReduction;
    }

    public void setWasteReduction(float wasteReduction) {
        this.wasteReduction = wasteReduction;
    }

    public float getSocialImpact() {
        return socialImpact;
    }

    public void setSocialImpact(float socialImpact) {
        this.socialImpact = socialImpact;
    }

    public float getWasteGenerated() {
        return wasteGenerated;
    }

    public void setWasteGenerated(float wasteGenerated) {
        this.wasteGenerated = wasteGenerated;
    }

    public float getRecyclingRate() {
        return recyclingRate;
    }

    public void setRecyclingRate(float recyclingRate) {
        this.recyclingRate = recyclingRate;
    }

    public float getOverallScore() {
        return (waterUsage + carbonFootprint + energyConsumption + wasteGenerated + recyclingRate + wasteGenerated + recyclingRate) / 5.0f;
    }
} 