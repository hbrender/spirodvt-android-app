package com.example.incentive_spirometer_and_dvt_application;

import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;

import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.*;

public class IncentiveSpirometerDataTest {
    
    private IncentiveSpirometerData spiroData;
    private DateFormat dateFormat;
    private String startTime;
    private String endTime;

    @Before
    public void beforeTest() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        startTime = "2019-07-27 08:00:00";
        endTime = "2019-07-27 09:00:00";

        try {
            spiroData = new IncentiveSpirometerData(1, dateFormat.parse(startTime), dateFormat.parse(endTime), 2000, 10, 9);
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getId() {
        assertEquals(spiroData.getId(), 1);
    }

    @Test
    public void setId() {
        spiroData.setId(2);
        assertEquals(spiroData.getId(), 2);
    }

    @Test
    public void getStartTime(){
        try {
            assertEquals(spiroData.getStartTime(), dateFormat.parse(startTime));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setStartTime(){
        try {
            startTime = "2019-01-01 12:00:00";
            spiroData.setStartTime(dateFormat.parse(startTime));
            assertEquals(spiroData.getStartTime(), dateFormat.parse(startTime));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEndTime(){
        try {
            assertEquals(spiroData.getEndTime(), dateFormat.parse(endTime));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setEndTime(){
        try {
            endTime = "2019-01-01 13:00:00";
            spiroData.setEndTime(dateFormat.parse(endTime));
            assertEquals(spiroData.getEndTime(), dateFormat.parse(endTime));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLungVolume() {
        assertEquals(spiroData.getLungVolume(), 2000);
    }

    @Test
    public void setLungVolume() {
        spiroData.setLungVolume(5000);
        assertEquals(spiroData.getLungVolume(), 5000);
    }

    @Test
    public void getNumberOfInhalations() {
        assertEquals(spiroData.getNumberOfInhalations(), 10);
    }

    @Test
    public void seNumberOfInhalations() {
        spiroData.setNumberOfInhalations(12);
        assertEquals(spiroData.getNumberOfInhalations(), 12);
    }

    @Test
    public void getInhalationsCompleted() {
        assertEquals(spiroData.getInhalationsCompleted(), 9);
    }

    @Test
    public void setInhalationsCompleted() {
        spiroData.setInhalationsCompleted(11);
        assertEquals(spiroData.getInhalationsCompleted(), 11);
    }

}
