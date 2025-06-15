package com.example.frontend.model;

import java.util.List;

public class Preferences {
    private List<String> preferredCategories;
    private int maxDistance;
    private float minSustainabilityScore;
    private boolean notificationsEnabled;

    public Preferences(List<String> preferredCategories, int maxDistance,
                      float minSustainabilityScore, boolean notificationsEnabled) {
        this.preferredCategories = preferredCategories;
        this.maxDistance = maxDistance;
        this.minSustainabilityScore = minSustainabilityScore;
        this.notificationsEnabled = notificationsEnabled;
    }

    // Getters y setters
    public List<String> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<String> preferredCategories) { this.preferredCategories = preferredCategories; }
    public int getMaxDistance() { return maxDistance; }
    public void setMaxDistance(int maxDistance) { this.maxDistance = maxDistance; }
    public float getMinSustainabilityScore() { return minSustainabilityScore; }
    public void setMinSustainabilityScore(float minSustainabilityScore) { this.minSustainabilityScore = minSustainabilityScore; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
}
