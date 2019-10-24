package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Timestamp;

public class IncentiveSpirometer {
    private int id;
    private Timestamp timestamp;
    private int lungVolume; // units = ml
    private int numberOfInhalations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getLung_volume() {
        return lungVolume;
    }

    public void setLungVolume(int lungVolume) {
        this.lungVolume = lungVolume;
    }

    public int getNumberOfInhalationss() {
        return numberOfInhalations;
    }

    public void setNumberOfInhalations(int numberOfInhalations) {
        this.numberOfInhalations = numberOfInhalations;
    }
}
