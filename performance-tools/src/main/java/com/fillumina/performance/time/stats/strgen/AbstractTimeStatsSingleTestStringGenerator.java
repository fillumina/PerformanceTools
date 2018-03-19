package com.fillumina.performance.time.stats.strgen;

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
        if (stats.getMeasureMap().size() == 1) {
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
        DimensionalMeasure measure =
                stats.getMeasureMap().values().iterator().next();
        final Unit<?> unit = measure.getUnit();
        final double stdev =
                unit.convertFromBase(measure.getUnbiasedStandardDeviation());
        TableFormatter performanceTable = new TableFormatter("  ");
        long iterationPerSample = measure.getCount();
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
