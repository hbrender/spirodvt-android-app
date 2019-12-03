package com.example.incentive_spirometer_and_dvt_application.models;

import java.sql.Timestamp;
import java.util.Date;

public class DvtData implements Comparable <DvtData>{
    private int id;
    //private Timestamp timestamp;

    private Date startTime;
    private Date endTime;
    private String resistance; // units = easy, medium, hard
    private int numberOfReps;
    private int repsCompleted;

    public DvtData() {

    }

    public DvtData(int id, Timestamp timestamp, Date startTime, Date endTime, String resistance, int numberOfReps, int repsCompleted) {
        this.id = id;
        //this.timestamp = timestamp;
        this.startTime = startTime;
        this.endTime = endTime;
        this.resistance = resistance;
        this.numberOfReps = numberOfReps;
        this.repsCompleted = repsCompleted;
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

    public int getRepsCompleted() {
        return repsCompleted;
    }

    public void setRepsCompleted(int repsCompleted) {
        this.repsCompleted = repsCompleted;
    }

    @Override
    public int compareTo(DvtData dvtd) {
        return getStartTime().compareTo(dvtd.getStartTime());
    }
}
