package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.timer.RunnableSink;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DivisionByTwoPerformanceTest {

    private boolean display = false;

    public static void main(final String[] args) {
        final DivisionByTwoPerformanceTest test =
                new DivisionByTwoPerformanceTest();
        test.display = true;
        test.executeTest();
    }

    @Test
    public void executeTest() {
        final Random rnd = new Random(System.currentTimeMillis());

        PerformanceTimerFactory.createSingleThreaded()
                .addTest("math", new RunnableSink() {

                    @Override
                    public Object sink() {
                        return rnd.nextInt() / 2;
                    }
                })

                .addTest("binary", new RunnableSink() {

                    @Override
                    public Object sink() {
                        return rnd.nextInt() >> 1;
                    }
                })

                .instrumentedBy(
                        AutoProgressionPerformanceInstrumenter.builder()
                            .setMaxStandardDeviation(2)
                            .build())

                .execute()

                .use(AssertPerformance.withTolerance(7)
                    .assertTest("binary").fasterThan("math"))

                .printIf(display);
    }
}
