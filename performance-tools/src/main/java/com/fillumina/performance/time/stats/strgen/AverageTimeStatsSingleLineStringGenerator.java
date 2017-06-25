package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.AverageTimeStats;
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
public class AverageTimeStatsSingleLineStringGenerator
        extends AbstractTimeStatsSingleLineStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsSingleLineStringGenerator INSTANCE =
            new AverageTimeStatsSingleLineStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                new AverageTimeStatsSingleLineStringGenerator(confidence),
                appendable);
    }

    public AverageTimeStatsSingleLineStringGenerator() {
        super();
    }

    public AverageTimeStatsSingleLineStringGenerator(Ratio confidence) {
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
                .cell("iterations/smpl")
                .cell("avgTime")
                .cell("stdev")
                .cell("throughput")
                .cell("accuracy")
                .cell("conf")
                .endl()
                .cell(elapsed.getCount())
                .cell(iterationPerSample)
                .cell(elapsed.toStringForConfidenceWitoutSamples(confidence,unit))
                .cell(String.format(Locale.US, "%.6f", stdev))
                .cell(averageTimeToThroghput(
                        elapsed.getConfidenceInterval(confidence)))
                .cell(String.format(Locale.US, "%.6f %%", accuracy * 100.0))
                .cell(confidence)
                .endl();
    }
}
