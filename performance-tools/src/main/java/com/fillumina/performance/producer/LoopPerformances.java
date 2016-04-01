package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.stats.Measure;
import java.io.Serializable;
import java.util.*;

/**
 * Takes the raw tests execution time and number of iterations performed
 * and elaborates some useful statistics.
 * The returned lists follow the ordering given by {@code timeMap.values()}
 * so if ordering is important be sure to use a
 * {@link LinkedHashMap} to pass tests times.
 * The class is immutable.
 *
 * @author Francesco Illuminati
 */
public class LoopPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final LoopPerformances EMPTY = new LoopPerformances();

    private final long iterations;
    private final Map<String, TestPerformances> map;
    private final Measure stats;

    @SuppressWarnings("unchecked")
    private LoopPerformances() {
        this.iterations = 0;
        this.stats = Measure.EMPTY;
        this.map = (Map<String, TestPerformances>) Collections.EMPTY_MAP;
    }

    /**
     * Compute performance statistics based on iterations and a map
     * of tests and time elapsed.
     *
     * @param iterations number of iterations performed (it's used to extract
     *                      the time per cycle).
     * @param timeMap   a map between tests and number of nanoseconds elapsed
     *                  executing them. If the order is important use a
     *                  {@link LinkedHashMap}.
     */
    public LoopPerformances(final long iterations,
            final Map<String, Long> timeMap) {
        this.iterations = iterations;
        this.stats = createStatistics(timeMap);
        this.map = createMap(timeMap);
    }

    private Measure createStatistics(final Map<String, Long> timeMap) {
        final Collection<Long> values = timeMap.values();
        return new Measure(values);
    }

    private Map<String, TestPerformances> createMap(
            final Map<String, Long> timeMap) {
        final Map<String,TestPerformances> localMap =
                new LinkedHashMap<>(timeMap.size());
        final long slowestTime = Math.round(stats.max());

        for (Map.Entry<String, Long> entry: timeMap.entrySet()) {
            final String name = entry.getKey();
            final Long elapsed = entry.getValue();

            final float percentage = elapsed * 100F / slowestTime;
            final double elapsedNanosecondsPerCycle = elapsed * 1.0D / iterations;

            final TestPerformances testPerformances = new TestPerformances(
                    name, elapsed, percentage, elapsedNanosecondsPerCycle);

            localMap.put(name, testPerformances);
        }
        return Collections.unmodifiableMap(localMap);
    }

    public int getNumberOfTests() {
        return map.size();
    }

    /** Get {@link TestPerformances} by test name. */
    public Map<String, TestPerformances> getPerformances() {
        return map;
    }

    public Collection<TestPerformances> getTests() {
        return map.values();
    }

    public long getIterations() {
        return iterations;
    }

    public Measure getStatistics() {
        return stats;
    }

    public String toString(final String message) {
        return message + ":\n" + toString();
    }

    @Override
    public String toString() {
        return StringTableViewer.INSTANCE.getTable(this).toString();
    }
}
