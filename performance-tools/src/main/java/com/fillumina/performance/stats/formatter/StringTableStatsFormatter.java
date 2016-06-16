package com.fillumina.performance.stats.formatter;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringFormatter;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.TimeUnitFormatter;
import static com.fillumina.performance.util.TimeUnitFormatter.*;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class StringTableStatsFormatter
        implements StringFormatter<PerformanceStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableStatsFormatter INSTANCE =
            new StringTableStatsFormatter();

    public static final PerformanceViewer<PerformanceStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected StringTableStatsFormatter() {}

    @Override
    public String toString(ComposedName name, PerformanceStats stats) {
        StringBuilder buf = new StringBuilder();
        if (!name.isEmpty()) {
            buf.append(TableFormatter.title(name.toString(), '-'));
        }
        return buf.append(toString(stats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(PerformanceStats stats) {
        final Map<String, TestPerformance> testMap = stats.getTestPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformance tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final TimeUnit unit = minTimeUnit(times);
        return getTable(stats, unit);
    }

    /**
     * Display a human readable text only multi line string with the
     * passed performances.
     *
     * @param title             The title of the table.
     * @param stats  The performances to display.
     * @param unit              The unit of time to use.
     * @return uses a {@link StringOutputHolder} for an easier manipulation
     *          using the
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public String getTable(final PerformanceStats stats,
            final TimeUnit unit) {
        StringBuilder buf = new StringBuilder();

        TableFormatter header = creteHeader(stats);
        buf.append(header.toString());

        buf.append("\nPerformances:\n");
        TableFormatter performanceTable = createPerformanceTable(stats, unit);
        buf.append(performanceTable.toString());

        if (stats.getTestPerformances().size() > 1) {
            buf.append("\nTukey HSD Matrix:").append(System.lineSeparator());
            TableFormatter tukeyTable = createTukeyTable(stats);
            buf.append(tukeyTable.toString());
        }

//        TableFormatter memoryTable = createMemoryTable(stats);
//        if (!memoryTable.isEmpty()) {
//
//            buf.append("\nMemory Usage (")
//               .append(MemoryAnalyzer.MEMORY_GRANULARITY)
//               .append(" byte granularity):\n")
//               .append(memoryTable.toString());
//        }

        return buf.append('\n').toString();
    }

    private TableFormatter creteHeader(final PerformanceStats stats) {
        TableFormatter header = new TableFormatter("  ");
        add(header, "Total Time",
                TimeUnitFormatter.prettyPrint(stats.getTotalTime()));
        add(header, "Measure confidence", "95 %");
        add(header, "Max ratio percentage margin",
                String.format("%2.3f", stats.getMaximumPercentageMargin()));
        add(header, "Statistical significance matrix prob",
                String.format("%2.3f",
                        stats.getStatisticalSignificanceMatrixProbability(0.9)));
        add(header, "ANOVA", stats.getAnova());
        add(header, "Minimum Tukey HSD accuracy for ratio",
                String.format("%2.3f",
                        stats.getMinTukeyHsdEvaluationPercentage()));
        return header;
    }

    private TableFormatter createTukeyTable(final PerformanceStats stats) {
        TableFormatter tukeyTable = new TableFormatter("  ");
        int size = stats.getTestPerformances().size();
        double tukey;
        for (int i=0; i<size; i++) {
            for (int j=i+1; j<size; j++) {
                tukey = stats.getTukeyKramerHsdConfidenceProbability(i,j);
                tukeyTable
                        .cell(stats.getName(i))
                        .cell("vs")
                        .cell(stats.getName(j))
                        .cell(tukey);
                if (tukey > 0.8) {
                    tukeyTable.cell("different");
                } else if (tukey < 0.4) {
                    tukeyTable.cell("equals");
                } else {
                    tukeyTable.cell("uncertain");
                }
                tukeyTable.endl();
            }
        }
        return tukeyTable;
    }

    private TableFormatter createPerformanceTable(final PerformanceStats stats,
            final TimeUnit unit) {
        String unitSymbol = " " + TimeUnitFormatter.printSymbol(unit);
        TableFormatter performanceTable = new TableFormatter("  ");
        int index = 0;
        for (final TestPerformance tp : stats.getTestPerformances().values()) {
            final Measure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = elapsed.getUnbiasedStandardDeviation();

            performanceTable
                    .cell(index)
                    .cell(tp.getName())
                    .cell("stdev = " + String.format("%.3f", stdev) +
                            unitSymbol)
                    .cell(elapsed.toString()+ unitSymbol)
                    .cell("from " + tp.getOriginalTotalSamples() + " samples")
                    .cell(tp.getPercentage().toStringAsPercentageWithConfidence())
                    //.cell("TukeyHSD = " + tp.getTukeyHsd())
                    .endl();

            index++;
        }
        return performanceTable;
    }

//    private TableFormatter createMemoryTable(final PerformanceStats stats) {
//        TableFormatter memoryTable = new TableFormatter("  ");
//        for (final TestPerformance tp : stats.getTestPerformances().values()) {
//            Measure memoryUsed = tp.getMemoryUsed();
//            if (memoryUsed != null) {
//                memoryTable
//                        .cell(tp.getName())
//                        .cell(MemoryUnit.prettyPrint(memoryUsed))
//                        .cell("stdev = ", MemoryUnit.prettyPrint(
//                                memoryUsed.getUnbiasedStandardDeviation()))
//                        .endl();
//            }
//        }
//        return memoryTable;
//    }

    private void add(TableFormatter tf, String message, Object... values) {
        if (values[0] != null) {
            String msg = values[0].toString() +
                    ((values.length == 1) ? "" : " " + values[1].toString());
            tf.cell(message).cell("=").cell(msg).endl();
        }
    }
}
