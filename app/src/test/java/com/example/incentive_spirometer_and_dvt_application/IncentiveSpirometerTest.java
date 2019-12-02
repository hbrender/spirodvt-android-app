package com.example.incentive_spirometer_and_dvt_application;

import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IncentiveSpirometerTest {
    private IncentiveSpirometer ic;

    @Before
    public void beforeTest() {
        ic = new IncentiveSpirometer(1, 2000, 10);
    }

    @Test
    public void getId() {
        assertEquals(ic.getId(), 1);
    }

    @Test
    public void setId() {
        ic.setId(2);
        assertEquals(ic.getId(), 2);
    }

    @Test
    public void getLungVolume() {
        assertEquals(ic.getLungVolume(), 2000);
    }

    @Test
    public void setLungVolume() {
        ic.setLungVolume(5000);
        assertEquals(ic.getLungVolume(), 5000);
    }

    @Test
    public void getNumberOfInhalations() {
        assertEquals(ic.getNumberOfInhalations(), 10);
    }

    @Test
    public void setNumberOfInhalations() {
        ic.setNumberOfInhalations(15);
        assertEquals(ic.getNumberOfInhalations(), 15);
    }
}

