package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Timestamp;

public class IncentiveSpirometer {
    private int id;
    private Timestamp timestamp;
    private int lung_volume; // units = ml
    private int number_of_inhalations;

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
        return lung_volume;
    }

    public void setLung_volume(int lung_volume) {
        this.lung_volume = lung_volume;
    }

    public int getNumber_of_inhalations() {
        return number_of_inhalations;
    }

    public void setNumber_of_inhalations(int number_of_inhalations) {
        this.number_of_inhalations = number_of_inhalations;
    }
}
