package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JUnitSimplePerformanceTemplateTest
        extends JUnitSimplePerformanceTemplate {

    /**
     * By this way the test can be executed directly
     * providing useful logs and messages.
     */
    public static void main(final String[] args) {
        new JUnitConditionalPerformanceTestHelperTest()
                .testWithOutput();
    }

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        assertTrue(iterationConsumer instanceof NullPerformanceConsumer);
        assertTrue(resultConsumer instanceof NullPerformanceConsumer);
    }
}
