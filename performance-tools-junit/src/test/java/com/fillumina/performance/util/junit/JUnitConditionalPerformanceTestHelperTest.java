package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class JUnitConditionalPerformanceTestHelperTest
        extends JUnitConditionalPerformanceTestHelper {

    @Override
    public void test(PerformanceConsumer iterationConsumer,
            PerformanceConsumer resultConsumer) {
        fail("Should never been executed (or the -DJUnitPerformanceTestRunner " +
                "has been set).");
    }
}
