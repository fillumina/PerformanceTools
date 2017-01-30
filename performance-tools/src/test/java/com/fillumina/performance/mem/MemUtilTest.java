package com.fillumina.performance.mem;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemUtilTest {

    @Test
    public void shouldReturnValuesDivisibleByStep() {
        for (int i=0; i<128; i++) {
            long value = MemUtil.align(i, 8);
            assertTrue(value % 8 == 0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptStepNotPowerOf2() {
        MemUtil.align(3, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptStepNotPowerOf2_2() {
        MemUtil.align(3, 31);
    }
}
