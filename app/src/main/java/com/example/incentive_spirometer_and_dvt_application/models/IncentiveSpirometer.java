package com.example.incentive_spirometer_and_dvt_application.models;

public class IncentiveSpirometer {
    private int id;
    private int lungVolume; // units = ml
    private int numberOfInhalations;

    public IncentiveSpirometer() {

    }

    public IncentiveSpirometer(int id, int lungVolume, int numberOfInhalations) {
        this.id = id;
        this.lungVolume = lungVolume;
        this.numberOfInhalations = numberOfInhalations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLungVolume() {
        return lungVolume;
    }

    public void setLungVolume(int lungVolume) {
        this.lungVolume = lungVolume;
    }

    public int getNumberOfInhalations() {
        return numberOfInhalations;
    }

    public void setNumberOfInhalations(int numberOfInhalations) {
        this.numberOfInhalations = numberOfInhalations;
    }
}
