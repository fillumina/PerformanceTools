package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputStatsParallelTableStringGenerator
        extends AbstractTimeStatsParallelTableStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsParallelTableStringGenerator INSTANCE =
            new ThroughputStatsParallelTableStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
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
                .cell("avg time")
                .cell("samples")
                .cell("iterations")
                .cell("stdev")
                .cell("accuracy")
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
            double accuracy,
            Ratio confidence) {
        performanceTable
                .cell(name)
                .cell(String.format(Locale.US,"%.2f %%", efficiency))
                .cell(measure
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(throughputToaverageTime(
                        measure.getConfidenceInterval(confidence)))
                .cell(tp.getOriginalSamples())
                .cell(tp.getIterationsPerSample())
                .cell(String.format(Locale.US,"%.6f", stdev))
                .cell(String.format(Locale.US,"%.3f %%", accuracy * 100.0))
                .endl();
    }

}
