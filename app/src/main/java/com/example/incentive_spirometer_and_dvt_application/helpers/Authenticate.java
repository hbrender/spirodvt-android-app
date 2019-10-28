package com.example.incentive_spirometer_and_dvt_application.helpers;

import java.util.Random;

public class Authenticate {
    private static final int SALT_LENGTH = 10;
    private String salt;
    private String username;
    private String password;

    public Authenticate(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String saltIt() {
        Random rand = new Random();
        for(int i = 0; i < SALT_LENGTH; i++) {
            salt += rand.nextInt(9);
        }
        return salt;
    }
}
