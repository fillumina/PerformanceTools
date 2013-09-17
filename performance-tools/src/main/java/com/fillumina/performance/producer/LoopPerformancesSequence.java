package com.fillumina.performance.producer;

import com.fillumina.performance.util.RunningStatistics;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * It reads a serie of {@link LoopPerformances} and calculates
 * average performances and maximum standard deviation. This class is final
 * and cannot be modified. To modified it use
 * {@link LoopPerformancesSequence.Running}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LoopPerformancesSequence implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * This is the running statistics that can be modified.
     */
    public static class Running extends LoopPerformancesSequence {
        private static final long serialVersionUID = 1L;

        public void addLoopPerformances(final LoopPerformances loopPerformances) {
            iterations += loopPerformances.getIterations();
            addPerformancesToSerieStats(loopPerformances);
        }
    }

    private static class SerieStats {
        public SerieStats(String name) {this.name = name;}
        private String name;
        private final RunningStatistics percentageStats =
                new RunningStatistics();
        private final RunningStatistics elapsedNanosecondStats =
                new RunningStatistics();
    }

    protected long iterations;
    private int samples;
    private Map<String, SerieStats> serieStatsMap = new LinkedHashMap<>();

    protected void addPerformancesToSerieStats(
            final LoopPerformances loopPerformances) {
        samples++;
        for (final TestPerformances tp: loopPerformances.getTests()) {
            final String name = tp.getName();
            final SerieStats item = getSerieStats(name);
            item.elapsedNanosecondStats.add(tp.getElapsedNanoseconds());
            item.percentageStats.add(tp.getPercentage());
        }
    }

    private SerieStats getSerieStats(final String name) {
        SerieStats serieStats = serieStatsMap.get(name);
        if (serieStats == null) {
            serieStats = new SerieStats(name);
            serieStatsMap.put(name, serieStats);
        }
        return serieStats;
    }

    public double calculateMaximumStandardDeviation() {
        final RunningStatistics stats = new RunningStatistics();
        for (final SerieStats ss: serieStatsMap.values()) {
            stats.add(ss.percentageStats.standardDeviation());
        }
        return stats.max();
    }

    public LoopPerformances calculateAverageLoopPerformances() {
        final long averageIterations = getAverageIterations();
        final RunningLoopPerformances rp =
                new RunningLoopPerformances(averageIterations);
        for (SerieStats ss: serieStatsMap.values()) {
            final double average = ss.elapsedNanosecondStats.average();
            rp.add(ss.name, Double.valueOf(average).longValue());
        }
        return rp.getLoopPerformances();
    }

    public long getAverageIterations() {
        return iterations / samples;
    }

    public int getSamples() {
        return samples;
    }
}
