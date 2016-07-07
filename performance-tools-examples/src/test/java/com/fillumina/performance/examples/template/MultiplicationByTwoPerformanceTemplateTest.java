package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    private PrintOut printOut = new PrintOut();

    @Test
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

    public static void main(final String[] args) {
        final MultiplicationByTwoPerformanceTemplateTest test =
                new MultiplicationByTwoPerformanceTemplateTest();
        test.printOut = new PrintOut(true);
        test.executeWithFullOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
            .setName("Multiplication By Two - template")
            .speed()
                .setMinConfidence(0.01)
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
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(10)
                .assertOrder("binary").sameAs("math");
    }
}
