package com.example.incentive_spirometer_and_dvt_application.models;

public class Patient {
    private int id;
    private String patientId;
    private String firstName;
    private String lastName;
    private int heightFeet; // units = ft.
    private double heightInches; // units = in.
    private double weight; // units = lb.
    private int age; // units = years
    private String sex; // male, female, other
    private int incentiveSpirometerId;
    private int dvtId;
    private String incentiveSpirometerUuid;
    private String dvtUuid;

    public Patient() {

    }

    public Patient(String patientId, String firstName, String lastName, int heightFeet, double heightInches, double weight, int age, String sex, int incentiveSpirometerId, int dvtId, String incentiveSpirometerUuid, String dvtUuid) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.heightFeet = heightFeet;
        this.heightInches = heightInches;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.incentiveSpirometerId = incentiveSpirometerId;
        this.dvtId = dvtId;
        this.incentiveSpirometerUuid = incentiveSpirometerUuid;
        this.dvtUuid = dvtUuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getHeightFeet() {
        return heightFeet;
    }

    public void setHeightFeet(int heightFeet) {
        this.heightFeet = heightFeet;
    }

    public double getHeightInches() {
        return heightInches;
    }

    public void setHeightInches(double heightInches) {
        this.heightInches = heightInches;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getIncentiveSpirometerId() {
        return incentiveSpirometerId;
    }

    public void setIncentiveSpirometerId(int incentiveSpirometerId) {
        this.incentiveSpirometerId = incentiveSpirometerId;
    }

    public int getDvtId() {
        return dvtId;
    }

    public void setDvtId(int dvtId) {
        this.dvtId = dvtId;
    }

    public String getIncentiveSpirometerUuid() {
        return incentiveSpirometerUuid;
    }

    public void setIncentiveSpirometerUuid(String incentiveSpirometerUuid) {
        this.incentiveSpirometerUuid = incentiveSpirometerUuid;
    }

    public String getDvtUuid() {
        return dvtUuid;
    }

    public void setDvtUuid(String dvtUuid) {
        this.dvtUuid = dvtUuid;
    }

    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }
}
