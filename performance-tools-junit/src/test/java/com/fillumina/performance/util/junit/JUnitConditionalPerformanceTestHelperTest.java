package com.fillumina.performance.util.junit;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@RunWith(JUnitConditionalPerformanceTestRunner.class)
public class JUnitConditionalPerformanceTestHelperTest {

    @Test
    public void shouldNotExecuteThisTest() {
        fail("Should never been executed unless the " +
                "-DJUnitPerformanceTestRunner has been set.");
    }
}
