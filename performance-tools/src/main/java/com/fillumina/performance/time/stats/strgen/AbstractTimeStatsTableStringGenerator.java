package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.tname.TName;
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
        AbstractTimeStatsTableStringGenerator<A extends TimeStats>
        extends AbstractTimeStatsBaseStringGenerator<A> {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsTableStringGenerator() {
        super();
    }

    public AbstractTimeStatsTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    public int selectableRank(Assertable assertable) {
        return isStatsAssignableFrom(assertable) ? 1 : -1;
    }

    protected abstract void createHeaderLine(TableFormatter performanceTable);

    protected abstract void createTableLine(
            TableFormatter performanceTable,
            int index,
            TName name,
            DimensionalMeasure measure,
            TimeStats stats,
            double stdev,
            Unit unit,
            Ratio confidence);

    @Override
    public void appendTo(Appendable appendable, A assertable)
            throws IOException {
        TimeStats stats = (TimeStats) assertable;

        appendTitle(appendable, stats);

        TableFormatter header = creteHeader(stats, confidence);
        appendable.append(header.toString());
        appendable.append(System.lineSeparator());

        Unit unit = calculateUnit(stats);
        TableFormatter performance =
                createPerformanceTable(stats, unit, confidence);
        appendable.append(performance.toString());
        appendable.append(System.lineSeparator());
    }

    protected TableFormatter creteHeader(final TimeStats stats, Ratio confidence) {
        TableFormatter header = new TableFormatter("  ")
            .param("Test Time",
                    IntervalUnit.UNITS.toPrettyString(stats.getTotalTimeNs()))
            .param("Required measure confidence", confidence)
            .param("Max ratio percentage error",
                stats.getMaximumPercentageMargin(confidence).toString())
            .param("ANOVA", stats.getAnova());
        return header;
    }

    protected TableFormatter createPerformanceTable(
            TimeStats stats, Unit unit, Ratio confidence) {
        TableFormatter performanceTable = new TableFormatter("  ");
        createHeaderLine(performanceTable);
        int index = 0;
        for (final SingleTimeStats tp : stats.getSingleStatsMap().values()) {
            final DimensionalMeasure measure = tp.getMeasure();
            final double stdev =
                    unit.convertFromBase(measure.getUnbiasedStandardDeviation());
            TName name = tp.getName();

            createTableLine(performanceTable, index, name, measure, stats, stdev,
                    unit, confidence);
            index++;
        }
        return performanceTable;
    }

}
