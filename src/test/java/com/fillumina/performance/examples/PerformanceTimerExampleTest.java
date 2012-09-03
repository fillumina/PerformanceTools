package com.fillumina.performance.examples;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.util.JUnitPerformanceTestHelper;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class PerformanceTimerExampleTest extends JUnitPerformanceTestHelper {

    public static void main(final String[] args) {
        new PerformanceTimerExampleTest().testWithOutput();
    }

    @Override
    public void test(PerformanceConsumer iterationConsumer,
            PerformanceConsumer resultConsumer) {
        PerformanceTimerBuilder.createSingleThread()

        .addTest("Overriding getMessage()", new Runnable() {

            @Override
            public void run() {
                try {
                    throw new Exception() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public String getMessage() {
                            return buildStringSlowly();
                        }
                    };
                } catch (Exception e) {
                    assertNotNull(e);
                }
            }
        })

        .addTest("Using Constructor", new Runnable() {

            @Override
            public void run() {
                try {
                    throw new Exception(buildStringSlowly());
                } catch (Exception e) {
                    assertNotNull(e);
                }
            }
        })

        .addPerformanceConsumer(resultConsumer)

        .iterate(10_000)

        .use(AssertPerformance.withTolerance(5)
            .assertTest("Overriding getMessage()").fasterThan("Using Constructor"));
    }

    private String buildStringSlowly() {
        final StringBuilder builder = new StringBuilder();
        for (int i=0; i<100; i++) {
            builder.append(Math.sin(i/50 * Math.PI));
        }
        return builder.toString();
    }
}
