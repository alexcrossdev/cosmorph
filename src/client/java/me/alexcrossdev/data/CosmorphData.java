package me.alexcrossdev.data;

public class CosmorphData {
    private String name;
    private String model;

    public CosmorphData(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public String getName() { return name; }
    public String getModel() { return model; }

    public void setName(String name) { this.name = name; }
    public void setModel(String model) { this.model = model; }
}
