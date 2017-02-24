package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CharacterTest {

    /** Testing console output directly. */
    @Test
    public void printCharacterOut() {
        System.out.println("TESTING plus-minus    : " + "±");
        System.out.println("TESTING multiplication: " + "×");
    }

    @Test
    public void shouldPlusMinusBeEqualToU00B1() {
        assertEquals("±", "\u00B1");
    }

    @Test
    public void shouldMultiplicationBeEqualToU00D7() {
        assertEquals("×", "\u00D7");
    }
}
