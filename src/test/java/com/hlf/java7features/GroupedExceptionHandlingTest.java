package com.hlf.java7features;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by howard.fackrell on 11/6/15.
 */
public class GroupedExceptionHandlingTest {

    class AException extends Exception {};
    class EException extends Exception {};
    class IException extends Exception {};
    class OException extends Exception {};
    class UException extends Exception {};

    public void checkForVowel(String s) throws AException, EException, IException, OException, UException {
        if (s.toLowerCase().contains("a")) throw new AException();
        if (s.toLowerCase().contains("e")) throw new EException();
        if (s.toLowerCase().contains("i")) throw new IException();
        if (s.toLowerCase().contains("o")) throw new OException();
        if (s.toLowerCase().contains("u")) throw new UException();
    }

    @Test
    public void testGroupedExceptionHandling() {

        try{
            checkForVowel("Howard");
            fail("Shouldn't get this far without an exception");
        } catch (EException | IException | UException e) {
            e.printStackTrace();
            fail("Didn't expect one of these...");
        } catch (AException | OException e) {
            //handle these different for some reason
            assertTrue(true);
        }
    }
}
