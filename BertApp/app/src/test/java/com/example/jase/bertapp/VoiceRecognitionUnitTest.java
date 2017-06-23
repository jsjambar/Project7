package com.example.jase.bertapp;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;


/**
 * Created by jase on 23/06/2017.
 */

public class VoiceRecognitionUnitTest {

    String loc = "Rotterdam";
    String loc2 = "Amsterdam";

    @Test
    public void findLocationSuccess(){
        assertSame(loc, loc);
    }

    public void findLocationFail(){
        assertSame(loc, loc2);
    }
}
