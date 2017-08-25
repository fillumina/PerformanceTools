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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class
        AbstractTimeStatsParallelTableStringGenerator<A extends TimeStats>
        extends AbstractTimeStatsBaseStringGenerator<A> {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsParallelTableStringGenerator() {
        super();
    }

    public AbstractTimeStatsParallelTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    public int selectableRank(Assertable assertable) {
        if (! isStatsAssignableFrom(assertable)) {
            return -1;
        }
        TimeStats stats = (TimeStats) assertable;
        List<TName> list = new ArrayList<>(stats.getSingleStatsMap().keySet());
        if (list.isEmpty()) {
            return -1;
        }
        if (list.get(0).getLastName().equals("single") &&
            list.get(list.size() - 1).getLastName().equals("parallel")) {
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
        Unit unit = calculateUnit(stats);
        appendTitle(appendable, stats);
        TableFormatter header = new TableFormatter("  ")
            .param("Test Time",
                IntervalUnit.UNITS.toPrettyString(stats.getTotalTimeNs()))
            .param("Required measure confidence", confidence)
            .param("Max ratio percentage margin",
                stats.getMaximumPercentageMargin(confidence))
            .param("ANOVA", stats.getAnova())
            .param("Minimum Tukey HSD accuracy for ratio",
                String.format(Locale.US, "%2.3f", stats.getMinTukeyHsd()));

        appendable.append(header.toString());
        appendable.append(System.lineSeparator());

        TableFormatter performanceTable = new TableFormatter("  ");
        createHeaderLine(performanceTable);

        double singleTime = 0;
        for (final SingleTimeStats single : stats.getSingleStatsMap().values()) {
            DimensionalMeasure elapsed = single.getMeasure();
            double stdev =
                    unit.convertFromBase(elapsed.getUnbiasedStandardDeviation());
            // http://www.webassign.net/question_assets/unccolphysmechl1/measurements/manual.html
            Ratio fractionalUncertainty =
                    elapsed.getFractionalUncertainty(confidence);
            String lastName = single.getName().toString();
            String name;
            switch (lastName) {
                case "single":
                    name = "single thread execution";
                    singleTime = elapsed.getMean();
                    break;
                case "parallel":
                    name = "parallel execution";
                    break;
                default:
                    name = lastName;
                    break;
            }
            double efficiency = 100.0 * singleTime / elapsed.getMean();
            createTableLine(performanceTable, name, efficiency, elapsed, unit,
                    single, stdev, fractionalUncertainty, confidence);
        }
        appendable.append(performanceTable.toString());
        appendable.append(System.lineSeparator());
    }

    protected abstract void createHeaderLine(TableFormatter performanceTable);

    protected abstract void createTableLine(TableFormatter performanceTable,
            String name,
            double efficiency,
            DimensionalMeasure elapsed,
            Unit unit,
            SingleTimeStats tp,
            double stdev,
            Ratio fractionalUncertainty,
            Ratio confidence);

}
