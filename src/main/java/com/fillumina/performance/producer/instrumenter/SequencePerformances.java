package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.utils.RunningStatistics;
import com.fillumina.performance.producer.timer.TestPerformances;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.RunningLoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fra
 */
public class SequencePerformances implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    private static class SerieStats {
        private String name;
        private final RunningStatistics percentageStats = new RunningStatistics();
        private final RunningStatistics elapsedNanosecondStats = new RunningStatistics();
    }

    private long iterations;
    private List<SerieStats> serieStats;

    public void clear() {
        serieStats = null;
        iterations = 0;
    }

    @Override
    public PerformanceConsumer setMessage(final String message) {
        // do nothing
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
    public void process() {
        // do nothing
    }

    public double getMaximumStandardDeviation() {
        final RunningStatistics stats = new RunningStatistics();
        for (final SerieStats ss: serieStats) {
            stats.add(ss.percentageStats.standardDeviation());
        }
        return stats.max();
    }

    public LoopPerformances getAverageLoopPerformances() {
        final RunningLoopPerformances rp = new RunningLoopPerformances();
        rp.incrementIterationsBy(Long.valueOf(iterations).intValue());
        for (SerieStats ss: serieStats) {
            final double average = ss.elapsedNanosecondStats.average();
            rp.add(ss.name, Double.valueOf(average).longValue());
        }
        return rp.getLoopPerformances();
    }
}
