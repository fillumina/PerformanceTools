package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.AverageTimeStats;
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
public class AverageTimeStatsSingleTestStringGenerator
        extends AbstractTimeStatsSingleTestStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsSingleTestStringGenerator INSTANCE =
            new AverageTimeStatsSingleTestStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                new AverageTimeStatsSingleTestStringGenerator(confidence),
                appendable);
    }

    public AverageTimeStatsSingleTestStringGenerator() {
        super();
    }

    public AverageTimeStatsSingleTestStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof AverageTimeStats;
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
                .cell("average time")
                .cell("stdev")
                .cell("throughput")
                .cell("accuracy")
                .cell("confidence")
                .endl()
                .cell(elapsed.getCount())
                .cell(iterationPerSample)
                .cell(elapsed
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(String.format(Locale.US, "%.6f %s", stdev, unit))
                .cell(averageTimeToThroghput(
                        elapsed.getConfidenceInterval(confidence)))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .cell(confidence)
                .endl();
    }
}
