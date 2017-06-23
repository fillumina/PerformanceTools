package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.time.stats.SingleSpeedStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleTestSpeedStatsTableStringGenerator
        extends AbstractSpeedStatsStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final SingleTestSpeedStatsTableStringGenerator INSTANCE =
            new SingleTestSpeedStatsTableStringGenerator();

    public static final PerformanceViewer<TimeStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<TimeStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    private final Ratio confidence;

    public SingleTestSpeedStatsTableStringGenerator() {
        this.confidence = DEFAULT_CONFIDENCE;
    }

    public SingleTestSpeedStatsTableStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    public boolean isCompatible(final TimeStats stats) {
        return stats.getSingleStatsMap().size() == 1;
    }

    @Override
    protected String getString(TimeStats stats, IntervalUnit unit) {
        if (!isCompatible(stats)) {
            throw new RuntimeException("cannot show given stats.");
        }
        SingleSpeedStats tp = stats.getSingleStatsMap().values().iterator().next();

        final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
        final double stdev = unit.convertFromBase(
                elapsed.getUnbiasedStandardDeviation());

        final double accuracy =
                elapsed.getMarginOfError(confidence) / elapsed.getMean();

        TableFormatter header = new TableFormatter("  ")
        .param("Speed test time",
                IntervalUnit.getHelper().toString(stats.getTotalTimeNs()) );

        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("samples")
                .cell("iter/smpl")
                .cell("speed")
                .cell("stdev")
                .cell("frequency")
                .cell("accuracy")
                .cell("conf")
                .endl()
                .cell(elapsed.getCount())
                .cell(tp.getIterationsPerSample())
                .cell(elapsed.toStringForConfidenceWitoutSamples(confidence,unit))
                .cell(String.format(Locale.US, "%.6f", stdev))
                .cell(frequencyToString(elapsed.getConfidenceInterval(confidence)))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .cell(confidence)
                .endl();

        return header.toString() + System.lineSeparator() +
                performanceTable.toString();
    }
}
