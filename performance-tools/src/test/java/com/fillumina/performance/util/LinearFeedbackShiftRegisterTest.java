package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearFeedbackShiftRegisterTest {

    @Test
    public void shouldTestPeriod16() {
        LinearFeedbackShiftRegister lfsr =
                new LinearFeedbackShiftRegister(4, 4, 3);
        int first = lfsr.next();
        assertNotSame(0, first);

        for (int i=1; i<15; i++) {
            assertNotSame(first, lfsr.next());
        }

        assertEquals(first, lfsr.next(), 0);
    }

}
