package com.example.frontend.model;

import java.util.List;

public class Consumer extends User {
    private List<Purchase> purchaseHistory;
    private Preferences preferences;

    public Consumer(String id, String name, String email, Location location, float sustainabilityScore,
                    List<Purchase> purchaseHistory, Preferences preferences) {
        super(id, name, email, location, sustainabilityScore);
        this.purchaseHistory = purchaseHistory;
        this.preferences = preferences;
    }

    public List<Purchase> getPurchaseHistory() { return purchaseHistory; }
    public void setPurchaseHistory(List<Purchase> purchaseHistory) { this.purchaseHistory = purchaseHistory; }
    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }
}