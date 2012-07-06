package com.fillumina.performance.examples;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class SpeedUpExceptionsByOverridingGetMessageTest {

    @Test
    public void shouldOverrideGetMessageBeFasterThanConstructor() {
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
                }
            }
        })

        .addTest("Using Constructor", new Runnable() {

            @Override
            public void run() {
                try {
                    throw new Exception(buildStringSlowly());
                } catch (Exception e) {
                }
            }
        })

        .iterate(10_000)
        .use(new AssertPerformance())
        .setPercentageTolerance(5)
        .assertPercentageFor("Overriding getMessage()").lessThan(10)
        .assertTest("Overriding getMessage()").fasterThan("Using Constructor");

    }

    private String buildStringSlowly() {
        final StringBuilder builder = new StringBuilder();
        for (int i=0; i<100; i++) {
            builder.append(Math.sin(i/50 * Math.PI));
        }
        return builder.toString();
    }
}
