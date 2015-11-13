package com.hlf.java7features;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by howard.fackrell on 11/5/15.
 */
public class StringSwitchTest {

    @Test
    public void testStringSwitch() {
        String language = "Java";

        String mantra;

        switch (language) {
            case "Java":
                mantra = "Write once, run anywhere";
                break;

            case "Erlang":
                mantra = "Let it crash";
                break;

            case "Ruby":
                mantra = "Matz is Nice And So We Are Nice";
                break;

            default:
                mantra = "?";
                break;
        }


        assertTrue(mantra.equals("Write once, run anywhere"));
    }
}
