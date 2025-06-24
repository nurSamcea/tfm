package com.example.frontend.model;

public class StatsItem {
    public enum Type { GRAPH, KPI }
    private final Type type;
    private final String title;
    private final String value;

    private StatsItem(Type type, String title, String value) {
        this.type = type;
        this.title = title;
        this.value = value;
    }

    public static StatsItem createGraph() {
        return new StatsItem(Type.GRAPH, null, null);
    }
    public static StatsItem createKpi(String title, String value) {
        return new StatsItem(Type.KPI, title, value);
    }
    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getValue() { return value; }
} 