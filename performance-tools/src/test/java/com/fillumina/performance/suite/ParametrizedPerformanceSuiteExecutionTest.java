package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedStringGenerator;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.ComposedName;
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
        test.shouldExecuteTestForEachParameter();
    }

    @Test
    public void shouldExecuteTestForEachParameter() {
        final Bag<Integer> bag = new Bag<>();

        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded();

        // this is the parametrized test
        Map<ComposedName,SpeedStats> stats =
            pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(10)
                    .build())
                    .instrumentedBy(SpeedSuite.<Integer>parametrizedSuite())
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
            SpeedStringGenerator.parametrizedViewer()
                .consume(ComposedName.create("test"), stats);
        }

        assertTrue(bag.getCount(1) > 0);
        assertTrue(bag.getCount(2) > 0);
    }
}
