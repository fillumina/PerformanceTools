package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class AverageTimeStatsTableStringGenerator
        extends AbstractTimeStatsTableStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final AverageTimeStatsTableStringGenerator INSTANCE =
            new AverageTimeStatsTableStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                new AverageTimeStatsTableStringGenerator(confidence),
                appendable);
    }

    public AverageTimeStatsTableStringGenerator() {
        super();
    }

    public AverageTimeStatsTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof AverageTimeStats;
    }

    @Override
    protected void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("idx")
                .cell("name")
                .cell("samples")
                .cell("ratio vs slower")
                .cell("avgTime")
                .cell("stdev")
                .cell("throughput")
                .cell("confidence")
                .cell("TukeyHSD")
                .endl();
    }

    @Override
    protected void createTableLine(
            TableFormatter performanceTable,
            int index,
            TName name,
            DimensionalMeasure measure,
            TimeStats stats,
            double stdev,
            Unit unit,
            Ratio confidence) {

        performanceTable
                .cell(index)
                .cell(name.toString())
                .cell(measure.getCount())
                .cell(stats.getRatioWithSlowestTest(name, confidence)
                        .toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f", stdev))
                .cell(averageTimeToThroghput(
                        measure.getConfidenceInterval(confidence)))
                .cell(String.format(Locale.US,"%.3f %%",
                        confidence.getPercentage()))
                .cell(String.format(Locale.US,"%.3f",
                        stats.getTukeyHsdComparedToRef(name)))
                .endl();
    }
}
