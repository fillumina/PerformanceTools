package com.fillumina.performance.examples.template;

import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.TestContainer;
import com.fillumina.performance.producer.timer.RunnableSink;
import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import java.util.Random;

/**
 *
 * @author Francesco Illuminati
 */
public class DivisionByTwoPerformanceTest
        extends JUnitAutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new DivisionByTwoPerformanceTest().executeWithIntermediateOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        config.setMaxStandardDeviation(2);
    }

    @Override
    public void addTests(TestContainer tests) {
        final Random rnd = new Random(System.currentTimeMillis());

        tests.addTest("math", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() / 2;
            }
        });

        tests.addTest("binary", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() >> 1;
            }
        });
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(2)
                .assertTest("binary").fasterThan("math");
    }
}
