package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParallelSingleTestSpeedStatsTableStringGenerator
        extends AbstractSpeedStatsStringGenerator {
    private static final long serialVersionUID = 1L;
    private static final Ratio CONFIDENCE = Ratio.P_95;

    public static final ParallelSingleTestSpeedStatsTableStringGenerator INSTANCE =
            new ParallelSingleTestSpeedStatsTableStringGenerator();

    public static final PerformanceViewer<SpeedStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    public static final PerformanceConsumer<SpeedStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    public boolean isCompatible(SpeedStats stats) {
        List<String> list = new ArrayList<>(stats.getPerformanceMap().keySet());
        return !list.isEmpty() &&
                list.get(0).endsWith("_single") &&
                list.get(list.size() - 1).endsWith("_parallel");
    }

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        if (!isCompatible(stats)) {
            throw new RuntimeException("cannot show given stats.");
        }
        StringBuilder buf = new StringBuilder();

        TableFormatter header = new TableFormatter("  ")
            .param("Test name",
                    stats.getPerformanceMap().keySet().iterator().next()
                            .replace("_single", ""))
            .param("Test Time",
                    IntervalUnit.getHelper().toString(stats.getTotalTimeNs()) )
            .param("Required measure confidence", CONFIDENCE)
            .param("Max ratio percentage margin",
                    String.format(Locale.US, "%2.3f %%",
                            100 * stats.getMaximumPercentageMargin(CONFIDENCE)))
            .param("ANOVA", stats.getAnova())
            .param("Minimum Tukey HSD accuracy for ratio",
                    String.format(Locale.US, "%2.3f",
                            stats.getMinTukeyHsd()));
        buf.append(header.toString());
        buf.append(System.lineSeparator());

        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("test name")
                .cell("efficiency")
                .cell("time (samples used)")
                .cell("frequency")
                .cell("samples/it")
                .cell("stdev")
                .cell("accuracy")
                .endl();

        double singleTime = 0;

        for (final SingleSpeedStats tp : stats.getPerformanceMap().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());

            final double accuracy =
                    elapsed.getMarginOfError(CONFIDENCE) /
                    elapsed.getMean();

            String name = tp.getName();
            if (name.endsWith("_single")) {
                name = "single thread execution";
                singleTime = elapsed.getMean();
            } else if (name.endsWith("_parallel")) {
                name = "parallel execution";
            } else {
                int index = name.lastIndexOf('_');
                name = "worker " + name.substring(index + 1);
            }

            double efficiency = 100.0 * singleTime / elapsed.getMean();

            performanceTable
                .cell(name)
                .cell(String.format(Locale.US,"%.2f %%", efficiency))
                .cell(elapsed.toString(unit))
                .cell(frequencyToString(elapsed.getConfidenceInterval(CONFIDENCE)))
                .cell(tp.getOriginalSamples(), "/", tp.getIterationsPerSample())
                .cell(String.format(Locale.US,"%.6f", stdev))
                .cell(String.format(Locale.US,"%.3f %%", accuracy * 100.0))
                .endl();
        }

        buf.append(performanceTable.toString());
        buf.append(System.lineSeparator());

        return buf.toString();
    }

}
