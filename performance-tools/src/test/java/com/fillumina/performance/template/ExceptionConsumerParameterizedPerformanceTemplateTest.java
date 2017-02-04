package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionConsumerParameterizedPerformanceTemplateTest
        extends AutoParameterizedPerformanceTemplate<Integer> {

    private boolean assertionErrorConsumerCaptured;

    public static void main(final String[] args) {
        new ExceptionConsumerParameterizedPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
        assertTrue(assertionErrorConsumerCaptured);
    }

    @Override
    public void addParameters(ParameterContainer<Integer> params) {
        params.addParameter("one", 1);
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
        // cannot possibly be
        assertion.speed().forAllTests().assertValue("one").sameAs(-1);
    }

    @Override
    public void config(TestConfiguration config) {
        config.setTestListener(new TestListener() {
                    @Override
                    public <ST, MT, SA extends Assertion<ST>,
                        MA extends Assertion<MT>> boolean notify(
                            TestConfiguration config,
                            MixedAssertion<SA, MA> assertion,
                            TreeHolder<SpeedStats, ST> speedStats,
                            TreeHolder<MemStats, MT> usedMemStats,
                            TreeHolder<MemStats, MT> allocatedMemStats,
                            Throwable exception) {
                        assertionErrorConsumerCaptured = true;
                        return false;
                    }
                })
                .speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest("test", new ParameterizedTestable<Integer>() {
            private LinearFeedbackShiftRegister lfsr =
                    new LinearFeedbackShiftRegister();
            @Override
            public Object test(Integer param) {
                for (int i=0; i<param; i++) {
                    lfsr.next();
                }
                return lfsr.next();
            }
        });
    }

}
