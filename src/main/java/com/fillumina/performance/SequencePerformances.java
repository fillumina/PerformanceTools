package com.fillumina.performance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fra
 */
public class SequencePerformances implements PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    private static class SerieStats {
        private String name;
        private final RunningStatistics percentageStats = new RunningStatistics();
        private final RunningStatistics elapsedNanosecondStats = new RunningStatistics();
    }

    private String message;
    private long iterations;
    private List<SerieStats> serieStats;

    public void clear() {
        serieStats = null;
        iterations = 0;
    }

    @Override
    public PerformanceConsumer setMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public SequencePerformances setPerformances(
            final LoopPerformances loopPerformances) {
        if (serieStats == null) {
            init(loopPerformances);
        }

        int index = 0;
        for (final TestPerformances tp: loopPerformances.getTests()) {
            final SerieStats item = serieStats.get(index);
            item.elapsedNanosecondStats.add(tp.getElapsedNanoseconds());
            item.percentageStats.add(tp.getPercentage());
            item.name = tp.getName();
            index++;
        }
        return this;
    }

    private void init(final LoopPerformances loopPerformances) {
        final int size = loopPerformances.size();
        serieStats = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            serieStats.add(new SerieStats());
        }
        iterations = loopPerformances.getIterations();
    }

    @Override
    public void consume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getMaximumStandardDeviation() {
        final RunningStatistics stats = new RunningStatistics();
        for (final SerieStats ss: serieStats) {
            stats.add(ss.percentageStats.standardDeviation());
        }
        return stats.max();
    }

    public LoopPerformances getAverageLoopPerformances() {
        final RunningPerformances rp = new RunningPerformances();
        rp.incrementIterationsBy(Long.valueOf(iterations).intValue());
        for (SerieStats ss: serieStats) {
            final double average = ss.elapsedNanosecondStats.average();
            rp.add(ss.name, Double.valueOf(average).longValue());
        }
        return rp.getLoopPerformances();
    }
}
