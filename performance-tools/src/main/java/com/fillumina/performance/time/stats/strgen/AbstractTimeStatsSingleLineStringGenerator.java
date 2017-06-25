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
public abstract class AbstractTimeStatsSingleLineStringGenerator
        extends AbstractTimeStatsBaseStringGenerator {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsSingleLineStringGenerator() {
        super();
    }

    public AbstractTimeStatsSingleLineStringGenerator(Ratio confidence) {
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
        return 0;
    }

    @Override
    public void appendTo(Appendable appendable, TimeStats stats)
            throws IOException {
        if (selectableRank(stats) < 0) {
            throw new RuntimeException("cannot show given stats.");
        }
        SingleTimeStats tp =
                stats.getSingleStatsMap().values().iterator().next();
        final DimensionalMeasure measure = tp.getMeasure();
        final Unit unit = calculateUnit(stats);
        final double stdev =
                unit.convertFromBase(measure.getUnbiasedStandardDeviation());
        final double accuracy =
                measure.getMarginOfError(confidence) / measure.getMean();
        TableFormatter header =
                new TableFormatter("  ").param("Speed test time",
                IntervalUnit.getHelper().toString(stats.getTotalTimeNs()));
        TableFormatter performanceTable = new TableFormatter("  ");
        long iterationPerSample = tp.getIterationsPerSample();
        createTable(performanceTable, measure, iterationPerSample, unit, stdev,
                accuracy, confidence);
        appendable.append(header.toString()).append(System.lineSeparator());
    }

    protected abstract void createTable(
            TableFormatter performanceTable,
            DimensionalMeasure measure,
            long iterationPerSample,
            Unit unit,
            double stdev,
            double accuracy,
            Ratio confidence);
}
