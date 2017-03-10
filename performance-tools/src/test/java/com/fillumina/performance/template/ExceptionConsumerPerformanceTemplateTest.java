package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mock.LfsrTestable;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Drain;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionConsumerPerformanceTemplateTest
        extends PerformanceTemplate {

    private boolean assertionErrorConsumerCaptured;

    public static void main(final String[] args) {
        new ExceptionConsumerPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldThrowException() {
        executeWithoutOutput();
        assertTrue(assertionErrorConsumerCaptured);
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
        assertions.speedWithTolerance(Ratio.percentage(5))
                .assertOrder("fast").lessThan("slow");
    }

    @Override
    public void config(TestConfiguration config) {
        config.setTestListener(new TestListener() {
            @Override
            public <S extends Assertable, M extends Assertable> boolean notify(
                        TestConfiguration config,
                        MixedAssertion<?, ?> assertion,
                        PHolder<S> speedStats,
                        PHolder<M> usedMemStats,
                        PHolder<M> allocatedMemStats,
                        Throwable exception) {
                    assertionErrorConsumerCaptured = true;
                    return false;
                }
            }).speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("slow", new LfsrTestable());
        tests.addTest("fast", new AbstractTestable() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public void test() {
                for (int i=0; i<1_000; i++) {
                    lfsr.next();
                }
                Drain.drain(lfsr.next());
            }
        });
    }

}
