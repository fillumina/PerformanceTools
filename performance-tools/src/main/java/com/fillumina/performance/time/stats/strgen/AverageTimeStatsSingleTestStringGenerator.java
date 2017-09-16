package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.AverageTimeStats;
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
public class AverageTimeStatsSingleTestStringGenerator
        extends AbstractTimeStatsSingleTestStringGenerator<AverageTimeStats> {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsSingleTestStringGenerator INSTANCE =
            new AverageTimeStatsSingleTestStringGenerator();

    public static Consumer<AverageTimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                AverageTimeStats.class,
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
            Ratio confidence) {
        performanceTable
                .cell("average time")
                .cell("stdev")
                .cell("uncertainty")
                .cell("throughput")
                .cell("samples")
                .cell("iterations")
                .cell("confidence")
                .endl()
                .cell(elapsed
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(String.format(Locale.US, "%.6f %s", stdev, unit))
                .cell(elapsed.getFractionalUncertainty(confidence))
                .cell(averageTimeToThroghput(
                        elapsed.getConfidenceInterval(confidence)))
                .cell(elapsed.getCount())
                .cell(iterationPerSample)
                .cell(confidence)
                .endl();
    }
}
