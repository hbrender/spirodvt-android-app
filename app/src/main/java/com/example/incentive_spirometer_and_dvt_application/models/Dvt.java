package com.example.incentive_spirometer_and_dvt_application.models;

public class Dvt {
    private int id;
    private String resistance; // units = easy, medium, hard
    private int numberOfReps;

    public Dvt() {

    }

    public Dvt(int id, String resistance, int numberOfReps) {
        this.id = id;
        this.resistance = resistance;
        this.numberOfReps = numberOfReps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
