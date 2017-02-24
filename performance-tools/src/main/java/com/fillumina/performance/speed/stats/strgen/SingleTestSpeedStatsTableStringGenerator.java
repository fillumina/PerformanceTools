package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.formatter.TableFormatter;
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

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    public boolean isCompatible(final SpeedStats stats) {
        return stats.getPerformanceMap().size() == 1;
    }

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        if (!isCompatible(stats)) {
            throw new RuntimeException("cannot show given stats.");
        }
        TestPerformance tp = stats.getPerformanceMap().values().iterator().next();

        final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
        final double stdev = unit.convertFromBase(
                elapsed.getUnbiasedStandardDeviation());

        final double confidence = tp.getRatio().getConfidence();

        final double accuracy =
                elapsed.getMarginOfError(confidence) /
                elapsed.getMean();

        TableFormatter header = new TableFormatter("  ")
        .param("Test Time",
                IntervalUnit.getHelper().toString(stats.getTotalTime()) )
        .param("Required measure confidence", "95 %");

        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("time (samples used)")
                .cell("frequency")
                .cell("samples/it")
                .cell("stdev")
                .cell("accuracy")
                .endl()
                .cell(elapsed.toString(unit))
                .cell(frequencyToString(elapsed.getMean()))
                .cell(tp.getOriginalSamples(), "/", tp.getIterationsPerSample())
                .cell(String.format(Locale.US, "%.6f", stdev))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .endl();

        return header.toString() + System.lineSeparator() +
                performanceTable.toString();
    }


}
