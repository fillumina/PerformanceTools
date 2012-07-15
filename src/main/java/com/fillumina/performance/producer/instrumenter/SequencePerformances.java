package com.fillumina.performance.producer.instrumenter;

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
public class SequencePerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class SerieStats {
        private String name;
        private final RunningStatistics percentageStats = new RunningStatistics();
        private final RunningStatistics elapsedNanosecondStats = new RunningStatistics();
    }

    private long iterations;
    private List<SerieStats> serieStats;

    public void addLoopPerformances(
            final LoopPerformances loopPerformances) {
        if (serieStats == null) {
            initSerieStats(loopPerformances);
        }
        iterations += loopPerformances.getIterations();
        addPerformancesToSerieStats(loopPerformances);
    }

    private void addPerformancesToSerieStats(
            final LoopPerformances loopPerformances) {
        int index = 0;
        for (final TestPerformances tp: loopPerformances.getTests()) {
            final SerieStats item = serieStats.get(index);
            item.elapsedNanosecondStats.add(tp.getElapsedNanoseconds());
            item.percentageStats.add(tp.getPercentage());
            item.name = tp.getName();
            index++;
        }
    }

    private void initSerieStats(final LoopPerformances loopPerformances) {
        final int size = loopPerformances.size();
        serieStats = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            serieStats.add(new SerieStats());
        }
    }

    public double calculateMaximumStandardDeviation() {
        final RunningStatistics stats = new RunningStatistics();
        for (final SerieStats ss: serieStats) {
            stats.add(ss.percentageStats.standardDeviation());
        }
        return stats.max();
    }

    public LoopPerformances calculateAverageLoopPerformances() {
        final RunningLoopPerformances rp = new RunningLoopPerformances(iterations);
        for (SerieStats ss: serieStats) {
            final double average = ss.elapsedNanosecondStats.average();
            rp.add(ss.name, Double.valueOf(average).longValue());
        }
        return rp.getLoopPerformances();
    }
}
