package com.fillumina.performance.time.stats.strgen;

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
public class ThroughputStatsParallelTableStringGenerator
        extends AbstractTimeStatsParallelTableStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsParallelTableStringGenerator INSTANCE =
            new ThroughputStatsParallelTableStringGenerator();

    public static final Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new ThroughputStatsParallelTableStringGenerator(confidence),
                appendable);
    }

    public ThroughputStatsParallelTableStringGenerator() {
        super();
    }

    public ThroughputStatsParallelTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Stats stats) {
        return stats.getStatsType().equals(TimeStatsType.THROUGHPUT);
    }

    @Override
    protected void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("test name")
                .cell("efficiency")
                .cell("throughput")
                .cell("stdev")
                .cell("uncertainty")
                .cell("smpl")
                .cell("iter")
                .endl();
    }

    @Override
    protected void createTableLine(TableFormatter performanceTable,
            String name,
            double efficiency,
            DimensionalMeasure measure,
            Unit<?> unit,
            double stdev,
            Ratio fractionalUncertainty,
            Ratio confidence) {
        performanceTable
                .cell(name)
                .cell(String.format(Locale.US,"%.2f %%", efficiency))
                .cell(measure
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(String.format(Locale.US,"%.6f %s", stdev, unit))
                .cell(fractionalUncertainty)
                .cell(measure.getCount())
                .endl();
    }

}
