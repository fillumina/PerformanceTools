package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import static org.testng.Assert.assertTrue;

/**
 *
 * @author Francesco Illuminati
 */
public class TestNgSimplePerformanceTemplateTest
        extends TestNgSimplePerformanceTemplate {

    /**
     * By this way the test can be executed directly
     * providing useful logs and messages.
     */
    public static void main(final String[] args) {
        new TestNgSimplePerformanceTemplateTest()
                .executeWithOutput();
    }

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        assertTrue(iterationConsumer instanceof NullPerformanceConsumer);
        assertTrue(resultConsumer instanceof NullPerformanceConsumer);
    }
}
