package com.example.incentive_spirometer_and_dvt_application;

import com.example.incentive_spirometer_and_dvt_application.models.Patient;

import org.junit.Test;

import static org.junit.Assert.*;

public class PatientTest {
    private Patient patient = new Patient(1, "cole", "desilva", 6, 0.0, 205.0, 21, "male", 1000, 2000);

    @Test
    public void getId() {
        assertEquals(patient.getId(), 1);
    }

    @Test
    public void getName() {
        assertEquals(patient.getFirstName(), "cole");
        assertEquals(patient.getLastName(), "desilva");
    }

    @Test
    public void seName() {
        patient.setFirstName("isak");
        patient.setLastName("bjornson");
        assertEquals(patient.getFirstName(), "isak");
        assertEquals(patient.getLastName(), "bjornson");
    }

    @Test
    public void getHeightFeetandInches() {
        assertEquals(patient.getHeightFeet(), 6);
        assertEquals(patient.getHeightInches(), 0.0, 0);
    }

    @Test
    public void setHeightFeetandInches() {
        patient.setHeightFeet(7);
        patient.setHeightInches(2.1);
        assertEquals(patient.getHeightFeet(), 7);
        assertEquals(patient.getHeightInches(), 2.1, 0);
    }

    @Test
    public void getWeight() {
        assertEquals(patient.getWeight(), 205, 0);
    }

    @Test
    public void setWeight() {
        patient.setWeight(190.56);
        assertEquals(patient.getWeight(), 120.56, 0);
    }

    @Test
    public void getAge() {
        assertEquals(patient.getAge(), 21);
    }

    @Test
    public void setAge() {
        patient.setAge(24);
        assertEquals(patient.getAge(), 24);
    }


    @Test
    public void getSex() {
        assertEquals(patient.getSex(), "male");
        assertNotEquals(patient.getSex(), "Male");
        assertNotEquals(patient.getSex(), "MaLE");
    }

    @Test
    public void setSex() {
        patient.setSex("female");
        assertEquals(patient.getSex(), "female");
        assertNotEquals(patient.getSex(), "male");
    }

    @Test
    public void getIncentiveSpirometerId() { assertEquals(patient.getIncentiveSpirometerId(), 1000); }

    @Test
    public void seIncentiveSpirometerId() {
        patient.setIncentiveSpirometerId(1);
        assertEquals(patient.getIncentiveSpirometerId(), 1);
    }

    @Test
    public void getDVTId() {
        assertEquals(patient.getDvtId(), 2000);
    }

    @Test
    public void setDVTId() {
        patient.setDvtId(2);
        assertEquals(patient.getDvtId(), 2);
    }

}
