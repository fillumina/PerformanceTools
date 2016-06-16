package com.fillumina.performance.examples.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import java.util.concurrent.TimeUnit;
import com.fillumina.performance.assertion.StatsAssertion;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new MultiplicationByTwoPerformanceTemplateTest().executeWithFullOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setName("Multiplication By Two - template")
                .setMinConfidence(0.4)
                .setTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {

        tests.addTest("math", new AbstractTestable() {
            final LinearFeedbackShiftRegister lfsr =
                    new LinearFeedbackShiftRegister();

            @Override
            public Object test() {
                return lfsr.next() * 2;
            }
        });

        tests.addTest("binary", new AbstractTestable() {
            final LinearFeedbackShiftRegister lfsr =
                    new LinearFeedbackShiftRegister();

            @Override
            public Object test() {
                return lfsr.next() << 1;
            }
        });
    }

    @Override
    public void addAssertions(StatsAssertion assertion) {
        assertion.withPercentageTolerance(10)
                .assertOrder("binary").sameAs("math");
    }
}
