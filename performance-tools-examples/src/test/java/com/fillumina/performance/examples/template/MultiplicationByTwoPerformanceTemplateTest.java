package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
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
            .speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {

        tests.addTest("math", new Testable() {
            final Lfsr lfsr = new Lfsr(16);

            @Override
            public void run() {
                Sink.drain(lfsr.next() * 2);
            }
        });

        tests.addTest("binary", new Testable() {
            final Lfsr lfsr = new Lfsr(16);

            @Override
            public void run() {
                Sink.drain(lfsr.next() << 1);
            }
        });
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(Ratio.percentage(10))
                .assertOrder("binary").sameAs("math");
    }
}
