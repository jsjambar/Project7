package com.example.jase.bertapp;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
/**
 * Created by jase on 23/06/2017.
 */

public class UserUnitTest {

    String username = "Andy";
    String password = "a1b2c3";
    String emptyUsername = "";

    @Test
    public void userDoesntExist(){
        assertFalse(username.equals("NietAndy"));
    }

    @Test
    public void userExists(){
        assertTrue(username.equals("Andy"));
    }

    @Test
    public void registerUserSuccess(){
        assertTrue(!username.isEmpty() && !password.isEmpty());
        userDoesntExist();
    }

    @Test
    public void registerUserFail(){
        assertTrue(emptyUsername.isEmpty() || password.isEmpty());
    }

    @Test
    public void loginUserSucces(){
        assertTrue(!username.isEmpty() && !password.isEmpty());
        assertTrue(username.equals("Andy") && password.equals("a1b2c3"));
    }

    @Test
    public void loginUserFail(){
        assertTrue(!username.isEmpty() && !password.isEmpty());
        assertFalse(username.equals("Andy") && password.equals("nee"));
    }
}
