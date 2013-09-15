package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JUnitConditionalPerformanceTestHelperTest
        extends JUnitConditionalPerformanceTestHelper {

    @Override
    public void executePerformanceTest(PerformanceConsumer iterationConsumer,
            PerformanceConsumer resultConsumer) {
        fail("Should never been executed unless the " +
                "-DJUnitPerformanceTestRunner has been set.");
    }
}
