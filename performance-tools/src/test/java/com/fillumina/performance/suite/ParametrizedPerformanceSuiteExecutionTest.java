package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.viewer.StringTableParametrizedStatsViewer;
import com.fillumina.performance.util.Bag;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * It isn't common to run parametrized tests along a simple (non instrumented)
 * one but it isn't at all forbidden and it may prove useful.
 *
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuiteExecutionTest {

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedPerformanceSuiteExecutionTest test =
                new ParametrizedPerformanceSuiteExecutionTest();
        test.printout = true;
        test.shouldExecuteTestAddedToPerformanceTimerToo();
    }

    @Test
    public void shouldExecuteTestAddedToPerformanceTimerToo() {
        final Bag<Integer> bag = new Bag<>();

        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded();

        // this is the parametrized test
        Map<String,PerformanceStats> stats =
            pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(10)
                    .build())
                    .instrumentedBy(
                            new ParametrizedPerformanceSuite<Integer>())
                    .addParameter("one", 1)
                    .addParameter("two", 2)
                    .addTest("parametrized",
                            new ParametrizedTestable<Integer>() {
                        @Override
                        public Object test(final Integer param) {
                            bag.add(param);
                            return null;
                        }
                    })
                    .execute()
                    .getPerformance();

        if (printout) {
            StringTableParametrizedStatsViewer.INSTANCE.consume("test", stats);
        }

        assertTrue(bag.getCount(1) > 0);
        assertTrue(bag.getCount(2) > 0);
    }
}
