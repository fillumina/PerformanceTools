package com.fillumina.performance.stats.viewer;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.TimeUnitFormatter;
import static com.fillumina.performance.util.TimeUnitFormatter.*;
import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Produces a human readable multi-line string of
 * the passed performances.
 *
 * @author Francesco Illuminati
 */
public final class StringTableStatsViewer
        implements PerformanceStatsConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringTableStatsViewer INSTANCE = new StringTableStatsViewer();

    private StringTableStatsViewer() {}

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        getTable(message, stats).print();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated and there is no title.
     */
    public static StringOutputHolder toStringOutput(final PerformanceStats stats) {
        return getTable(null, stats);
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public static StringOutputHolder getTable(final String message,
            final PerformanceStats stats) {
        final Map<String, TestPerformances> testMap = stats.getTestPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformances tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final TimeUnit unit = minTimeUnit(times);
        return getTable(message, stats, unit);
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
    public static StringOutputHolder getTable(final String title,
            final PerformanceStats stats,
            final TimeUnit unit) {
        StringBuilder buf = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            buf.append(title)
                    .append(System.lineSeparator())
                    .append(TableFormatter.repeate('-', title.length()))
                    .append(System.lineSeparator());
        }

        TableFormatter header = new TableFormatter("  ");
        String message = stats.getMessage();
        if (message != null && !message.isEmpty()) {
            header.cell("Rejection message")
                    .cell("=")
                    .cell(message)
                    .endl();
        }

        header.cell("Confidence")
                .cell("=")
                .cell(stats.getConfidence())
                .endl();
        header.cell("Max ratio percentage margin")
                .cell("=")
                .cell(stats.getMaximumPercentageMargin())
                .endl();
        header.cell("Statistical significance matrix prob")
                .cell("=")
                .cell(stats.getStatisticalSignificanceMatrixProbability())
                .endl();
        header.cell("ANOVA")
                .cell("=")
                .cell(stats.getAnova())
                .endl();
        header.cell("Minimum Tukey HSD accuracy")
                .cell("=")
                .cell(stats.getMinTukeyHsdEvaluationPercentage())
                .endl();
        final Measure baseline = stats.getBaseline();
        if (baseline != null) {
            header.cell("Baseline")
                    .cell("=")
                    .cell(baseline.toString())
                    .endl();
        }
        buf.append(header.toString());

        TableFormatter table = new TableFormatter("  ");
        int index = 0;
        for (final TestPerformances tp : stats.getTestPerformances().values()) {
            final Measure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double confidence = tp.getConfidence();
            table
                    .cell(index)
                    .cell(tp.getName())
                    .cell(elapsed.toStringForConfidence(confidence) + " " +
                            TimeUnitFormatter.printSymbol(unit))
                    .cell(tp.getPercentage().toStringAsPercentage())
                    .cell("TukeyHSD = " + tp.getTukeyHsd())
                    .endl();

            index++;
        }
        buf.append(table.toString());
        return new StringOutputHolder(buf.toString());
    }

    private static String formatPercentage(final double percentageValue) {
        return String.format("%3.4f %%", percentageValue * 100.0);
    }
}
