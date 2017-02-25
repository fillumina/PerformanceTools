package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unfortunately Windows Console doesn't support UTF-8 characters (!!).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CharacterTest {

    @Test
    public void shouldPlusMinusBeEqualToU00B1() {
        assertEquals("±", "\u00B1");
    }

    @Test
    public void shouldMultiplicationBeEqualToU00D7() {
        assertEquals("×", "\u00D7");
    }
}
