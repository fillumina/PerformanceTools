package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
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
        extends AbstractTimeStatsParallelTableStringGenerator<ThroughputStats> {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsParallelTableStringGenerator INSTANCE =
            new ThroughputStatsParallelTableStringGenerator();

    public static final Consumer<ThroughputStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                ThroughputStats.class,
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
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof ThroughputStats;
    }

    @Override
    protected void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("test name")
                .cell("efficiency")
                .cell("throughput")
                .cell("stdev")
                .cell("uncertainty")
                .cell("average time")
                .cell("smpl")
                .cell("iter")
                .endl();
    }

    @Override
    protected void createTableLine(TableFormatter performanceTable,
            String name,
            double efficiency,
            DimensionalMeasure measure,
            Unit unit,
            SingleTimeStats tp,
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
                .cell(throughputToaverageTime(
                        measure.getConfidenceInterval(confidence)))
                .cell(tp.getOriginalSamples())
                .cell(tp.getIterationsPerSample())
                .endl();
    }

}
