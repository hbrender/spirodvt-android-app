package com.example.incentive_spirometer_and_dvt_application;

import com.example.incentive_spirometer_and_dvt_application.models.Dvt;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DvtTest {
    private Dvt dvt;

    @Before
    public void beforeTest() {
        dvt = new Dvt(1, "hard", 10);
    }

    @Test
    public void getId() {
        assertEquals(dvt.getId(), 1);
    }

    @Test
    public void setId() {
        dvt.setId(2);
        assertEquals(dvt.getId(), 2);
    }

    @Test
    public void getResistance() {
        assertEquals(dvt.getResistance(), "hard");
    }

    @Test
    public void setResistance() {
        dvt.setResistance("easy");
        assertEquals(dvt.getResistance(), "easy");
    }

    @Test
    public void getNumberOfReps() {
        assertEquals(dvt.getNumberOfReps(), 10);
    }

    @Test
    public void setNumberOfReps() {
        dvt.setNumberOfReps(15);
        assertEquals(dvt.getNumberOfReps(), 15);
    }

}
