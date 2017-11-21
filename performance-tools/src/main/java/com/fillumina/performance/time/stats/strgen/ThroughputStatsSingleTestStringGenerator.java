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
public class ThroughputStatsSingleTestStringGenerator
        extends AbstractTimeStatsSingleTestStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsSingleTestStringGenerator INSTANCE =
            new ThroughputStatsSingleTestStringGenerator();

    public static final Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
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
    protected boolean isStatsAssignableFrom(Stats stats) {
        return stats.getStatsType().equals(TimeStatsType.THROUGHPUT);
    }

    @Override
    protected void createTable(
            TableFormatter performanceTable,
            DimensionalMeasure throughput,
            long iterationPerSample,
            Unit<?> unit,
            double stdev,
            Ratio confidence) {
        performanceTable
                .cell("throughput")
                .cell("stdev")
                .cell("uncertainty")
                .cell("average time")
                .cell("samples")
                .cell("iterations")
                .cell("confidence")
                .endl()
                .cell(throughput.toStringForConfidenceWitoutSamples(confidence,unit))
                .cell(String.format(Locale.US, "%.6f %s", stdev, unit))
                .cell(throughput.getFractionalUncertainty(confidence))
                .cell(throughputToaverageTime(
                        throughput.getConfidenceInterval(confidence)))
                .cell(throughput.getCount())
                .cell(iterationPerSample)
                .cell(confidence)
                .endl();
    }
}
