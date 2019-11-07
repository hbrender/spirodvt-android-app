package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Timestamp;

public class DvtData {
    private int id;
    private Timestamp timestamp;
    private int resistance; // units = easy, medium, hard
    private int numberOfReps;

    public DvtData() {

    }

    public DvtData(int id, Timestamp timestamp, int resistance, int numberOfReps) {
        this.id = id;
        this.timestamp = timestamp;
        this.resistance = resistance;
        this.numberOfReps = numberOfReps;
    }

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

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getNumberOfReps() {
        return numberOfReps;
    }

    public void setNumberOfReps(int numberOfReps) {
        this.numberOfReps = numberOfReps;
    }
}
