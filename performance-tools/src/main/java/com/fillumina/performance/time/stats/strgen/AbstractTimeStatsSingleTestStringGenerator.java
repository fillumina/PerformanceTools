package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class
        AbstractTimeStatsSingleTestStringGenerator<A extends TimeStats>
        extends AbstractTimeStatsBaseStringGenerator<A> {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsSingleTestStringGenerator() {
        super();
    }

    public AbstractTimeStatsSingleTestStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    public int selectableRank(Assertable assertable) {
        if (! isStatsAssignableFrom(assertable)) {
            return -1;
        }
        TimeStats stats = (TimeStats) assertable;
        if (stats.getSingleStatsMap().size() == 1) {
            return 2;
        }
        return -1;
    }

    @Override
    public void appendTo(Appendable appendable, A stats)
            throws IOException {
        if (selectableRank(stats) < 0) {
            throw new RuntimeException("cannot show given stats.");
        }
        SingleTimeStats single =
                stats.getSingleStatsMap().values().iterator().next();
        final DimensionalMeasure measure = single.getMeasure();
        final Unit unit = calculateUnit(stats);
        TableFormatter header =
                new TableFormatter("  ").param("Speed test time",
                IntervalUnit.UNITS.toPrettyString(stats.getTotalTimeNs()));
        appendable.append(header.toString()).append(System.lineSeparator());

        final double stdev =
                unit.convertFromBase(measure.getUnbiasedStandardDeviation());
        TableFormatter performanceTable = new TableFormatter("  ");
        long iterationPerSample = single.getIterationsPerSample();
        createTable(performanceTable, measure, iterationPerSample, unit, stdev,
                confidence);
        appendable.append(performanceTable.toString()).append(System.lineSeparator());
    }

    protected abstract void createTable(
            TableFormatter performanceTable,
            DimensionalMeasure measure,
            long iterationPerSample,
            Unit unit,
            double stdev,
            Ratio confidence);
}
