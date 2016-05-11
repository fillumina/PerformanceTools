package com.fillumina.performance.examples.template;

import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceTest
        extends JUnitAutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new MultiplicationByTwoPerformanceTest().executeWithFullOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setMessage("Multiplication By Two - template")
                .setTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        final LinearFeedbackShiftRegister lfsr =
                new LinearFeedbackShiftRegister();

        tests.addTest("math", new AbstractTestable() {

            @Override
            public Object test() {
                return lfsr.next() * 2;
            }
        });

        tests.addTest("binary", new AbstractTestable() {

            @Override
            public Object test() {
                return lfsr.next() << 1;
            }
        });
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(10)
                .assertSpeed("binary").sameAs("math");
    }
}
