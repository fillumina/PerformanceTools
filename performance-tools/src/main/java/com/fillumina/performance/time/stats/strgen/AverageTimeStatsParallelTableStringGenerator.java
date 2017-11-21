package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AverageTimeStatsParallelTableStringGenerator
        extends AbstractTimeStatsParallelTableStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsParallelTableStringGenerator INSTANCE =
            new AverageTimeStatsParallelTableStringGenerator();

    public static Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new AverageTimeStatsParallelTableStringGenerator(confidence),
                appendable);
    }

    public AverageTimeStatsParallelTableStringGenerator() {
        super();
    }

    public AverageTimeStatsParallelTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Stats stats) {
        return stats.getStatsType().equals(TimeStatsType.AVERAGE);
    }

    @Override
    protected void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("test name")
                .cell("efficiency")
                .cell("average time")
                .cell("stdev")
                .cell("uncertainty")
                .cell("frequency")
                .cell("smpl")
                .cell("iter")
                .endl();
    }

    @Override
    protected void createTableLine(TableFormatter performanceTable,
            String name,
            double efficiency,
            DimensionalMeasure elapsed,
            Unit<?> unit,
            SingleStats tp,
            double stdev,
            Ratio fractionalUncertainty,
            Ratio confidence) {
        performanceTable
                .cell(name)
                .cell(String.format(Locale.US,"%.2f %%", efficiency))
                .cell(elapsed
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(String.format(Locale.US,"%.6f %s", stdev, unit))
                .cell(fractionalUncertainty)
                .cell(averageTimeToThroghput(
                        elapsed.getConfidenceInterval(confidence)))
                .cell(tp.getOriginalSamples())
                .cell(tp.getIterationsPerSample())
                .endl();
    }

}
