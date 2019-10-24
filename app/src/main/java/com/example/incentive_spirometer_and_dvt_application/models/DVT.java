package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Timestamp;

public class DVT {
    private int id;
    private Timestamp timestamp;
    private int resistance; // units = ?
    private int number_of_reps;

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

    public int getNumber_of_reps() {
        return number_of_reps;
    }

    public void setNumber_of_reps(int number_of_reps) {
        this.number_of_reps = number_of_reps;
    }
}
