package com.example.incentive_spirometer_and_dvt_application;

import com.example.incentive_spirometer_and_dvt_application.models.Doctor;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoctorTest {
    Doctor doctor = new Doctor(1, "username1");

    @Test
    public void getId() {
        assertEquals(doctor.getId(), 1);
    }

    @Test
    public void setId() {
        doctor.setId(2);
        assertEquals(doctor.getId(), 2);
    }

    @Test
    public void getUsername() {
        assertEquals(doctor.getUsername(), "username1");
    }

    @Test
    public void setUsername() {
        doctor.setUsername("username2");
        assertEquals(doctor.getUsername(), "username2");
    }
}