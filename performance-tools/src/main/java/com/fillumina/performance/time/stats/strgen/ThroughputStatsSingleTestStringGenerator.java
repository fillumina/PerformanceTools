package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ThroughputStatsSingleTestStringGenerator
        extends AbstractTimeStatsSingleTestStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsSingleTestStringGenerator INSTANCE =
            new ThroughputStatsSingleTestStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                new ThroughputStatsSingleTestStringGenerator(confidence),
                appendable);
    }

    public ThroughputStatsSingleTestStringGenerator() {
        super();
    }

    public ThroughputStatsSingleTestStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof ThroughputStats;
    }

    @Override
    protected void createTable(
            TableFormatter performanceTable,
            DimensionalMeasure elapsed,
            long iterationPerSample,
            Unit unit,
            double stdev,
            double accuracy,
            Ratio confidence) {
        performanceTable
                .cell("samples")
                .cell("iterations")
                .cell("throughput")
                .cell("stdev")
                .cell("average time")
                .cell("accuracy")
                .cell("confidence")
                .endl()
                .cell(elapsed.getCount())
                .cell(iterationPerSample)
                .cell(elapsed.toStringForConfidenceWitoutSamples(confidence,unit))
                .cell(String.format(Locale.US, "%.6f %s", stdev, unit))
                .cell(throughputToaverageTime(
                        elapsed.getConfidenceInterval(confidence)))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .cell(confidence)
                .endl();
    }
}
