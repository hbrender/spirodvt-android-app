package com.example.incentive_spirometer_and_dvt_application.models;

import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;

public class User {

    public static String username = "isakIsSoCool";
    private static Authenticate auth = new Authenticate(username, "heLLoworLd");
    public static String salt = auth.getSalt();
    public static String hashedPass = auth.getHashedPassword(salt);
}
