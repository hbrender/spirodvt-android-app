package com.example.incentive_spirometer_and_dvt_application.helpers;
/**
 * Authentication class which is used in the login activity
 * stores the input of the users and then validates it with the user info stored in the database
 *
 * @author(s) Cole deSilva, Isak Bjornson
 */

import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Authenticate {

    private static String TAG = "AuthenticateClass";
    private static final int SALT_LENGTH = 10;
    private String salt;
    private String username;
    private String password;

    public Authenticate(String username, String password) {

        this.username = username;
        this.password = password;
        // this.salt is set to the users' salt so that we can prepend the users salt to the inputted password and then hash it
        // if the real user's salt + the password hash is equal to the user's hashed password then we know they got the password right.
        this.salt = saltIt();
    }

    public String getSalt() { return this.salt; }
    public String getHashedPassword(String salt) { return getHash(salt); }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    private String saltIt() {

        Random rand = new Random();
        StringBuilder saltBuilder = new StringBuilder();
        for(int i = 0; i < SALT_LENGTH; i++) {
            saltBuilder.append(rand.nextInt(9));
        }
        return saltBuilder.toString();
    }


    // these three functions below are from GeeksForGeeks.com where they explained how to implement SHA-256 hashing algorithm
    // since we are not experts in hashing algorithms/computer security this is the way we chose to implement our hashing capabilities
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

    private String getHash(String salt) {

        try {
            return toHexString(getSHA(salt + this.password));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
