package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class JUnitPerformanceTestHelperTest
        extends JUnitPerformanceTestHelper {

    /**
     * By this way a test can be executed as a single application
     * and give useful messages.
     */
    public static void main(final String[] args) {
        new JUnitConditionalPerformanceTestHelperTest()
                .testWithOutput();
    }

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        assertTrue(iterationConsumer instanceof NullPerformanceConsumer);
        assertTrue(resultConsumer instanceof NullPerformanceConsumer);
    }
}
