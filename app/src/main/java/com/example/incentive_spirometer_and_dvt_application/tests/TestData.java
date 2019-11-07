package com.example.incentive_spirometer_and_dvt_application.tests;

import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.Doctor;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;

public class TestData {

    public void populatePatient(DatabaseHelper databaseHelper) {
        Patient p1 = new Patient(1, "John", "Johnson", 6, 4, 182, 25, "Male", 0, 0);
        Patient p2 = new Patient(2, "Lucy", "Riley", 5, 5, 125, 30, "Female", 0, 0);
        Patient p3 = new Patient(3, "Sean", "Wilson", 5, 10, 170, 42, "Other", 0, 0);
        Patient p4 = new Patient(4, "Sean", "Fred", 6, 0, 180, 34, "Male", 0, 0);
        Patient p5 = new Patient(5, "Sammy", "Martinez", 5, 2, 115, 27, "Female", 0, 0);
        Patient p6 = new Patient(6, "Nicole", "Meyers", 5, 8, 140, 36, "Female", 0, 0);

        databaseHelper.insertPatient(p1);
        databaseHelper.insertPatient(p2);
        databaseHelper.insertPatient(p3);
        databaseHelper.insertPatient(p4);
        databaseHelper.insertPatient(p5);
        databaseHelper.insertPatient(p6);
    }

    public void populateDoctorPatient(DatabaseHelper databaseHelper) {
        //databaseHelper.insertDoctorPatient(1, 1);
        //databaseHelper.insertDoctorPatient(2, 1);
        //databaseHelper.insertDoctorPatient(3, 1);
        //databaseHelper.insertDoctorPatient(4, 2);
        //databaseHelper.insertDoctorPatient(5, 2);
        //databaseHelper.insertDoctorPatient(6, 2);
    }

    public void populateDoctor(DatabaseHelper databaseHelper) {

    }

    public void populateLogin(DatabaseHelper databaseHelper) {

    }

    public void populateIncentiveSpirometer(DatabaseHelper databaseHelper) {

    }

    public void populateDvt(DatabaseHelper databaseHelper) {

    }

    public void populateIncentiveSpirometerData(DatabaseHelper databaseHelper) {

    }

    public void populateDvtData(DatabaseHelper databaseHelper) {

    }

}
