package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class
        AbstractTimeStatsParallelTableStringGenerator
        extends AbstractTimeStatsBaseStringGenerator {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsParallelTableStringGenerator() {
        super();
    }

    public AbstractTimeStatsParallelTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    public int selectableRank(Stats stats) {
        if (! isStatsAssignableFrom(stats)) {
            return -1;
        }
        List<PathName> list = new ArrayList<>(stats.getMeasureMap().keySet());
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
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        if (selectableRank(stats) < 0) {
            throw new RuntimeException("cannot show given stats.");
        }
        appendTitle(appendable, stats);
        TableFormatter header = new TableFormatter("  ")
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
        for (Map.Entry<PathName,DimensionalMeasure> e :
                stats.getMeasureMap().entrySet()) {
            PathName n = e.getKey();
            DimensionalMeasure elapsed = e.getValue();
            Unit<?> unit = elapsed.getUnit();
            double stdev = elapsed.getUnbiasedStandardDeviation();
            // http://www.webassign.net/question_assets/unccolphysmechl1/measurements/manual.html
            Ratio fractionalUncertainty =
                    elapsed.getFractionalUncertainty(confidence);
            String lastName = n.toString();
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
                    stdev, fractionalUncertainty, confidence);
        }
        appendable.append(performanceTable.toString());
        appendable.append(System.lineSeparator());
    }

    protected abstract void createHeaderLine(TableFormatter performanceTable);

    protected abstract void createTableLine(TableFormatter performanceTable,
            String name,
            double efficiency,
            DimensionalMeasure elapsed,
            Unit<?> unit,
            double stdev,
            Ratio fractionalUncertainty,
            Ratio confidence);

}
