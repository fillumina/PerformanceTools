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
public class AverageTimeStatsSingleTestStringGenerator
        extends AbstractTimeStatsSingleTestStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsSingleTestStringGenerator INSTANCE =
            new AverageTimeStatsSingleTestStringGenerator();

    public static Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
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
    protected boolean isStatsAssignableFrom(Stats stats) {
        return stats.getStatsType().equals(TimeStatsType.AVERAGE);
    }

    @Override
    protected void createTable(
            TableFormatter performanceTable,
            DimensionalMeasure elapsed,
            long iterationPerSample,
            Unit<?> unit,
            double stdev,
            Ratio confidence) {
        performanceTable
                .cell("average time")
                .cell("stdev")
                .cell("uncertainty")
                .cell("samples")
                .cell("iterations")
                .cell("confidence")
                .endl()
                .cell(elapsed
                        .toStringForConfidenceWitoutSamples(confidence, unit))
                .cell(String.format(Locale.US, "%.6f %s", stdev, unit))
                .cell(elapsed.getFractionalUncertainty(confidence))
                .cell(elapsed.getCount())
                .cell(iterationPerSample)
                .cell(confidence)
                .endl();
    }
}
