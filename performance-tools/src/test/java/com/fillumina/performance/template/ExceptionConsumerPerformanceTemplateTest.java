package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.testable.LfsrTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
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
        assertions.speedWithTolerance(5)
                .assertOrder("fast").lessThan("slow");
    }

    @Override
    public void config(TestConfiguration config) {
        config.setTestListener(new TestListener() {
                @Override
                public boolean notify(TestConfiguration config,
                        MixedAssertion<?, ?> assertion,
                        PHolder<SpeedStats> speedStats,
                        PHolder<MemStats> usedMemStats,
                        PHolder<MemStats> allocatedMemStats,
                        Throwable exception) {
                            assertionErrorConsumerCaptured = true;
                            return false;
                }
            })
            .speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("slow", new LfsrTestable());
        tests.addTest("fast", new AbstractTestable() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test() {
                for (int i=0; i<1_000; i++) {
                    lfsr.next();
                }
                return lfsr.next();
            }
        });
    }

}
