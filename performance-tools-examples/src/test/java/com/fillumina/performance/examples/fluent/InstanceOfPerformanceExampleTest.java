package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.junit.JUnitSimplePerformanceTemplate;
import java.util.concurrent.TimeUnit;

/**
 * Using fluent interface to define a test (templates are usually an
 * easier approach). It uses the simplest template.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InstanceOfPerformanceExampleTest
        extends JUnitSimplePerformanceTemplate {

    public static void main(final String[] args) {
        new InstanceOfPerformanceExampleTest().testWithDetailedOutput();
    }

    @Override
    public void executePerformanceTest(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        final Object object = new InstanceOfPerformanceExampleTest();
        PerformanceTimerFactory.createSingleThreaded()

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
                if (!Object.class.isAssignableFrom(
                        InstanceOfPerformanceExampleTest.class)) {
                    throw new RuntimeException();
                }

            }
        })

        .addPerformanceConsumer(iterationConsumer)

        .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setTimeout(10, TimeUnit.MINUTES)
                    .setBaseAndMagnitude(1_000_000, 3)
                    .setSamplesPerStep(10)
                    .build())
                .addPerformanceConsumer(resultConsumer)
                .execute()
                .use(AssertPerformance.withTolerance(10F)
                    .assertTest("classcheck").sameAs("instanceof"));
    }
}
