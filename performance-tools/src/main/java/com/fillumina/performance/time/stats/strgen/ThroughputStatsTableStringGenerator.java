package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class ThroughputStatsTableStringGenerator
        extends AbstractTimeStatsTableStringGenerator<ThroughputStats> {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsTableStringGenerator INSTANCE =
            new ThroughputStatsTableStringGenerator();

    public static final Consumer<ThroughputStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new ThroughputStatsTableStringGenerator(confidence),
                appendable);
    }

    public ThroughputStatsTableStringGenerator() {
        super();
    }

    public ThroughputStatsTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof ThroughputStats;
    }

    @Override
    protected void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("idx")
                .cell("name")
                .cell("ratio vs faster")
                .cell("throughput")
                .cell("stdev")
                .cell("uncertainty")
                .cell("avgTime")
                .cell("smpl")
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
            Unit<?> unit,
            Ratio confidence) {

        performanceTable
                .cell(index)
                .cell(name.toString())
                .cell(stats.getRatio(name, confidence).toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s", stdev, unit))
                .cell(measure.getFractionalUncertainty(confidence))
                .cell(throughputToaverageTime(
                        measure.getConfidenceInterval(confidence)))
                .cell(measure.getCount())
                .cell(String.format(Locale.US,"%.3f",
                        stats.getTukeyHsdComparedToRef(name)))
                .endl();
    }
}
