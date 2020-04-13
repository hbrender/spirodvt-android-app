package com.example.incentive_spirometer_and_dvt_application.models;

public class Dvt {
    private int id;
    private String uuid;
    private String resistance; // units = easy, medium, hard
    private int numberOfReps;

    public Dvt() {

    }

    public Dvt(int id, String uuid, String resistance, int numberOfReps) {
        this.id = id;
        this.uuid = uuid;
        this.resistance = resistance;
        this.numberOfReps = numberOfReps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getResistance() {
        return resistance;
    }

    public void setResistance(String resistance) {
        this.resistance = resistance;
    }

    public int getNumberOfReps() {
        return numberOfReps;
    }

    public void setNumberOfReps(int numberOfReps) {
        this.numberOfReps = numberOfReps;
    }
}
