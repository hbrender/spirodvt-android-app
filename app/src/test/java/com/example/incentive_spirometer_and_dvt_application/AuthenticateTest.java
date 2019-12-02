package com.example.incentive_spirometer_and_dvt_application;
import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;

import org.junit.Test;
import static org.junit.Assert.*;

public class AuthenticateTest {
    private String username = "cole";
    private String password = "password";
    private String salt = "1234567891";
    // hashed pass of 1234567891password
    private String hashedPass = "115097ea28d3f7d0a7323ee936d17b31d7d77a31028a30588098dbc29d2e6b17";
    private Authenticate authConst = new Authenticate(username, password);

    @Test
    public void testGetUsername() {
        assertEquals(authConst.getUsername(), "cole");
    }

    @Test
    public void testGetPassword() {
        assertEquals(authConst.getPassword(), "password");
    }

    @Test
    public void testGetHashedPassword() {
        assertEquals(authConst.getHashedPassword(salt), hashedPass);
    }




}
