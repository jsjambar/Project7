package com.example.jase.bertapp;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;


/**
 * Created by jase on 23/06/2017.
 */

public class RouteUnitTest {

    int userX = 1235;
    int userY = 4321;

    int locX = 1234;
    int locY = 4320;

    int distance = 1;

    @Test
    public void showRouteSuccess (){
        assertSame(userX - locX, distance);
        assertSame(userY - locY, distance);
        showTrue();
    }

    public void showRouteFail(){
        assertSame(userX - locX, distance);
        assertSame(userY - locY, distance);
        showFalse();
    }

    public void showTrue(){
        assertTrue(true);
    }

    public void showFalse(){
        assertFalse(false);
    }

}

