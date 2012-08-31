package com.fillumina.performance.producer.timer;

import com.fillumina.performance.util.RepeatingIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It operates
 * in the same way as the real one with the exception that the
 * {@link LoopPerformances} can be defined beforehand.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class FakePerformanceTimer
        extends AbstractPerformanceTimer {
    private static final long serialVersionUID = 1L;

    /**
     * Does the same steps as the real {@link PerformanceTimer} so
     * it can be used interchangeably for most tests.
     */
    @Override
    public LoopPerformancesHolder iterate(final int iterations) {
        assert iterations > 0;

        initTests();

        final LoopPerformances loopPerformances =
                executeTests(iterations, tests);

        processConsumers(null, loopPerformances);

        return new LoopPerformancesHolder(loopPerformances);
    }

    public abstract LoopPerformances getLoopPerformances(
            final int iterations);

    private LoopPerformances executeTests(final int iterations,
            final Map<String, Runnable> tests) {

        runTests(iterations, tests);

        return getLoopPerformances(iterations);
    }

    private void runTests(final int iterations, final Map<String, Runnable> tests) {
        for (Map.Entry<String, Runnable> entry: tests.entrySet()) {
            final String msg = entry.getKey();
            final Runnable runnable = entry.getValue();

            for (int t=0; t<iterations; t++) {
                runnable.run();
            }
        }
    }
}
