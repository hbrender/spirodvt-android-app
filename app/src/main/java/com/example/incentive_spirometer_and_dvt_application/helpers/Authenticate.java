package com.example.incentive_spirometer_and_dvt_application.helpers;

import com.example.incentive_spirometer_and_dvt_application.models.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Authenticate {
    private static final int SALT_LENGTH = 10;
    private String salt;
    private String username;
    private String password;
    private String hashedPassword;

    public Authenticate(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.salt = User.salt;
        this.hashedPassword = getHash();
    }

    public String getSalt() { return this.salt; }
    public String getHashedPassword() { return this.hashedPassword; }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    private String saltIt() {
        Random rand = new Random();
        for(int i = 0; i < SALT_LENGTH; i++) {
            salt += rand.nextInt(9);
        }
        return salt;
    }

    private byte[] getSHA(String input) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private String toHexString (byte[] hashed) {
        BigInteger num = new BigInteger(1, hashed);

        StringBuilder hexString = new StringBuilder(num.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    private String getHash() {
        try {
            return toHexString(getSHA(this.salt+this.password));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
