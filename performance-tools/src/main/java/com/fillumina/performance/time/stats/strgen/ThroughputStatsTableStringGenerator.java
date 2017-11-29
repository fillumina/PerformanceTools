package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Produces a human readable multi-row string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class ThroughputStatsTableStringGenerator
        extends AbstractTimeStatsTableStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final ThroughputStatsTableStringGenerator INSTANCE =
            new ThroughputStatsTableStringGenerator();

    public static final Consumer<Stats> appendTo(
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
    protected boolean isStatsAssignableFrom(Stats stats) {
        return stats.getStatsType().equals(TimeStatsType.THROUGHPUT);
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
            Stats stats,
            double stdev,
            Unit<?> unit,
            Ratio confidence) {

        double tukeyHsd = stats.getTukeyHsdComparedToRef(name);
        String tukeyHsdStr = tukeyHsd < 0 ? "" :
                String.format(Locale.US,"%.3f", tukeyHsd);

        performanceTable
                .cell(index)
                .cell(name.getLastName())
                .cell(stats.getRatioWithRef(name, confidence).toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s", stdev, unit))
                .cell(measure.getFractionalUncertainty(confidence))
                .cell(throughputToaverageTime(
                        measure.getConfidenceInterval(confidence)))
                .cell(measure.getCount())
                .cell(tukeyHsdStr)
                .endl();
    }
}
