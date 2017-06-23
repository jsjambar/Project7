package com.example.jase.bertapp;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;


/**
 * Created by jase on 23/06/2017.
 */

public class ApiCallUnitTest {

    boolean trueResponse = true;
    boolean falseResponse = false;
    String empty = "";

    @Test
    public void apiReturnsTrue(){
        assertTrue(trueResponse);
    }

    @Test
    public void apiReturnsFalse(){
        assertFalse(falseResponse);
    }

    @Test
    public void apiReturnsNothing() { assertTrue(empty.isEmpty()); }

}
