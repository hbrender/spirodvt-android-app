package com.example.incentive_spirometer_and_dvt_application.models;

import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.annotation.NonNull;

public class IncentiveSpirometerData implements Comparable <IncentiveSpirometerData>{
    private int id;
    private Date startTime;
    private Date endTime;
    //private Timestamp timestamp;
    private int lungVolume; // units = ml
    private int numberOfInhalations;
    private int inhalationsCompleted;

    public IncentiveSpirometerData() {

    }


    public IncentiveSpirometerData(int id, Date startTime, Date endTime, int lungVolume, int numberOfInhalations, int inhalationsCompleted) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        //this.timestamp = timestamp;
        this.lungVolume = lungVolume;
        this.numberOfInhalations = numberOfInhalations;
        this.inhalationsCompleted = inhalationsCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public int getInhalationsCompleted() {
        return inhalationsCompleted;
    }

    public void setInhalationsCompleted(int inhalationsCompleted) {
        this.inhalationsCompleted = inhalationsCompleted;
    }

    @Override
    public int compareTo(IncentiveSpirometerData isd) {
        return getStartTime().compareTo(isd.getStartTime());
    }

    @NonNull
    @Override
    public String toString() {
        return "start time: " + startTime.toString() + " end time: " + endTime.toString() + " breaths: " + inhalationsCompleted;
    }


}
