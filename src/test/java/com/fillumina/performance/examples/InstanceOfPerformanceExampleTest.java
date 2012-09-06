package com.fillumina.performance.examples;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.junit.JUnitPerformanceTestHelper;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class InstanceOfPerformanceExampleTest
        extends JUnitPerformanceTestHelper {

    public static void main(final String[] args) {
        new InstanceOfPerformanceExampleTest().testWithOutput();
    }

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        final Object object = new InstanceOfPerformanceExampleTest();
        PerformanceTimerBuilder.createSingleThread()

        .addTest("instanceof", new Runnable() {

            @Override
            public void run() {
                if (!(object instanceof InstanceOfPerformanceExampleTest)) {
                    throw new RuntimeException();
                }

            }
        })

        .addTest("classcheck", new Runnable() {

            @Override
            public void run() {
                if (!Object.class.isAssignableFrom(InstanceOfPerformanceExampleTest.class)) {
                    throw new RuntimeException();
                }

            }
        })

        .addPerformanceConsumer(iterationConsumer)

        .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                .setTimeout(10, TimeUnit.MINUTES)
                .setBaseAndMagnitude(1_000_000, 3)
                .setSamplePerIterations(10)
                .build()
                .addPerformanceConsumer(resultConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(5F)
                    .assertTest("classcheck").fasterThan("instanceof"));
    }
}
