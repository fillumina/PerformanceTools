package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class
        AbstractTimeStatsSingleTestStringGenerator
        extends AbstractTimeStatsBaseStringGenerator {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsSingleTestStringGenerator() {
        super();
    }

    public AbstractTimeStatsSingleTestStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    public int selectableRank(Stats stats) {
        if (! isStatsAssignableFrom(stats)) {
            return -1;
        }
        if (stats.getSingleStatsMap().size() == 1) {
            return 2;
        }
        return -1;
    }

    @Override
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        if (selectableRank(stats) < 0) {
            throw new RuntimeException("cannot show given stats.");
        }
        SingleStats single =
                stats.getSingleStatsMap().values().iterator().next();
        final DimensionalMeasure measure = single.getMeasure();
        final Unit<?> unit = calculateUnit(stats);
//        TableFormatter header =
//                new TableFormatter("  ").param("Speed test time",
//                IntervalUnit.UNITS.toPrettyString(stats.getTotalTimeNs()));
//        appendable.append(header.toString()).append(System.lineSeparator());

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
            Unit<?> unit,
            double stdev,
            Ratio confidence);
}
