package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.annotation.NonNull;

public class IncentiveSpirometerData implements Comparable <IncentiveSpirometerData>{
    private int id;
    private Timestamp startTime;
    private Timestamp endTime;
    //private Timestamp timestamp;
    private int lungVolume; // units = ml
    private int numberOfInhalations;
    private int inhalationsCompleted;

    public IncentiveSpirometerData() {

    }


    public IncentiveSpirometerData(int id, Timestamp startTime, Timestamp endTime, int lungVolume, int numberOfInhalations, int inhalationsCompleted) {
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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
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

    public String getDate (Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return (cal.get(Calendar.MONTH) + 1) + " / " + cal.get(Calendar.DAY_OF_MONTH); // months are 0 indexed
    }

    public String getTime (Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);

        return (cal.get(Calendar.HOUR_OF_DAY)) + " : " + String.format("%02d", cal.get(Calendar.MINUTE));
    }

    @Override
    public int compareTo(IncentiveSpirometerData isd) {
        return getStartTime().compareTo(isd.getStartTime());
    }

    @NonNull
    @Override
    public String toString() {
        Calendar cs = GregorianCalendar.getInstance();
        cs.setTime(startTime);
        Calendar ce = GregorianCalendar.getInstance();
        ce.setTime(endTime);
        return "start time: " + cs.get(Calendar.HOUR_OF_DAY) + ":"  + String.format(Locale.ENGLISH, "%02d", cs.get(Calendar.MINUTE)) + " "  +
                " end time: " + ce.get(Calendar.HOUR_OF_DAY) + ":"  + String.format(Locale.ENGLISH, "%02d", ce.get(Calendar.MINUTE)) + " "  +
                " completed " + inhalationsCompleted;
    }


}
