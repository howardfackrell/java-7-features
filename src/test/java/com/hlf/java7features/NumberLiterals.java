package com.hlf.java7features;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by howard.fackrell on 11/6/15.
 */
public class NumberLiterals {

    @Test
    public void testUnderScoreInNumberLiteral() {
        assertEquals(1_000_000, 1000000);
        assertEquals(1_000_000_000, 1000000000);
        assertEquals(1_000_000_000_000L, 1000000000000L);
    }

    @Test
    public void testBinaryListeral() {
        assertEquals(5, 0b101);
        assertEquals(36, 0b0010_0100);
    }
}
