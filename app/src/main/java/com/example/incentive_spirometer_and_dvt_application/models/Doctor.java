package com.example.incentive_spirometer_and_dvt_application.models;

public class Doctor {
    private int id;
    private String username;

    public Doctor() {

    }

    public Doctor(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
