package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.testable.LfsrTestable;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionConsumerPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {

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
                    public <ST, MT, SA extends Assertion<ST>,
                        MA extends Assertion<MT>> boolean notify(
                            TestConfiguration config,
                            MixedAssertion<SA, MA> assertion,
                            PerformanceHolder<SpeedStats, ST> speedStats,
                            PerformanceHolder<MemStats, MT> usedMemStats,
                            PerformanceHolder<MemStats, MT> allocatedMemStats,
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
