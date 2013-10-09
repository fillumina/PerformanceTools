package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@RunWith(JUnitConditionalPerformanceTestRunner.class)
public class JUnitSimplePerformanceTemplateTest
        extends JUnitSimplePerformanceTemplate {

    /**
     * By this way the test can be executed directly
     * providing useful logs and messages.
     */
    public static void main(final String[] args) {
        new JUnitSimplePerformanceTemplateTest()
                .executeWithOutput();
    }

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        assertTrue(iterationConsumer instanceof NullPerformanceConsumer);
        assertTrue(resultConsumer instanceof NullPerformanceConsumer);
    }
}
