package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
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
        List<TName> list = new ArrayList<>(stats.getSingleStatsMap().keySet());
        return !list.isEmpty() &&
                list.get(0).getLastName().equals("single") &&
                list.get(list.size() - 1).getLastName().equals("parallel");
    }

    @Override
    protected String getString(SpeedStats stats, IntervalUnit unit) {
        if (!isCompatible(stats)) {
            throw new RuntimeException("cannot show given stats.");
        }
        StringBuilder buf = new StringBuilder();

        TableFormatter header = new TableFormatter("  ")
            .param("Test name",
                    stats.getSingleStatsMap().keySet().iterator().next())
            .param("Test Time",
                    IntervalUnit.getHelper().toString(stats.getTotalTimeNs()) )
            .param("Required measure confidence", CONFIDENCE)
            .param("Max ratio percentage margin",
                    stats.getMaximumPercentageMargin(CONFIDENCE))
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

        for (final SingleSpeedStats tp : stats.getSingleStatsMap().values()) {
            final DimensionalMeasure elapsed = tp.getElapsedNanosecondsPerCycle();
            final double stdev = unit.convertFromBase(
                    elapsed.getUnbiasedStandardDeviation());

            final double accuracy =
                    elapsed.getMarginOfError(CONFIDENCE) /
                    elapsed.getMean();

            String lastName = tp.getName().getLastName();
            String name;
            if (lastName.equals("single")) {
                name = "single thread execution";
                singleTime = elapsed.getMean();
            } else if (lastName.equals("parallel")) {
                name = "parallel execution";
            } else {
                name = "worker " + lastName;
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
