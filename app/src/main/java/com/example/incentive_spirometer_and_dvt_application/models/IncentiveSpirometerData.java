package com.example.incentive_spirometer_and_dvt_application.models;

import java.util.Date;

import androidx.annotation.NonNull;

public class IncentiveSpirometerData implements Comparable <IncentiveSpirometerData>{
    private int id;
    private Date startTime;
    private Date endTime;
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

    public String getStringTime(String flag) {
        String strTime = " ";
        if (flag.equalsIgnoreCase("start")){
            strTime = startTime.toString();
        }
        else if(flag.equalsIgnoreCase("end")){
            strTime = endTime.toString();
        }

        StringBuilder result = new StringBuilder();
        String[] timeInfo = strTime.split("[ ]+");

        result.append(timeInfo[1] + " ");
        result.append(timeInfo[2] + ", " + timeInfo[5] + "   ");

        String[] noSeconds = timeInfo[3].split("[:]+");
        result.append(noSeconds[0] + ":");
        result.append(noSeconds[1]);

        return result.toString();
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
