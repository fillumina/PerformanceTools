package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
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
    private static final Ratio CONFIDENCE = Ratio.P_95;

    public static final SingleTestSpeedStatsTableStringGenerator INSTANCE =
            new SingleTestSpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    // TODO create constructor with CONFIDENCE

    public boolean isCompatible(final SpeedStats stats) {
        return stats.getSingleStatsMap().size() == 1;
    }

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        if (!isCompatible(stats)) {
            throw new RuntimeException("cannot show given stats.");
        }
        SingleSpeedStats tp = stats.getSingleStatsMap().values().iterator().next();

        final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
        final double stdev = unit.convertFromBase(
                elapsed.getUnbiasedStandardDeviation());

        final double accuracy =
                elapsed.getMarginOfError(CONFIDENCE) /
                elapsed.getMean();

        TableFormatter header = new TableFormatter("  ")
        .param("Speed test time",
                IntervalUnit.getHelper().toString(stats.getTotalTimeNs()) );

        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("time (samples used)")
                .cell("frequency")
                .cell("samples/it")
                .cell("stdev")
                .cell("accuracy")
                .cell("conf")
                .endl()
                .cell(elapsed.toString(unit))
                .cell(frequencyToString(elapsed.getConfidenceInterval(Ratio.P_95)))
                .cell(tp.getOriginalSamples(), "/", tp.getIterationsPerSample())
                .cell(String.format(Locale.US, "%.6f", stdev))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .cell(CONFIDENCE)
                .endl();

        return header.toString() + System.lineSeparator() +
                performanceTable.toString();
    }


}
