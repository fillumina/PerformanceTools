package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.ExpressionSolver;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTimeStatsTableStringGenerator
        extends AbstractTimeStatsBaseStringGenerator {
    private static final long serialVersionUID = 1L;

    public AbstractTimeStatsTableStringGenerator() {
        super();
    }

    public AbstractTimeStatsTableStringGenerator(Ratio confidence) {
        super(confidence);
    }

    protected abstract void createHeaderLine(TableFormatter performanceTable);

    protected abstract void createTableLine(
            TableFormatter performanceTable,
            int index,
            TName name,
            DimensionalMeasure measure,
            Stats stats,
            double stdev,
            Unit<?> unit,
            Ratio confidence);

    @Override
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        appendTitle(appendable, stats);

        String header = creteHeader(stats, confidence);
        appendable.append(header);
        appendable.append(System.lineSeparator());

        Stats filteredStats = new Stats(stats, ReferenceTestFilter.FILTER);

        TableFormatter performance =
                createPerformanceTable(filteredStats, confidence);
        appendable.append(performance.toString());
        appendable.append(System.lineSeparator());
    }

    private String creteHeader(Stats stats, Ratio confidence) {
        String header = new TableFormatter("  ")
            .param("Required measure confidence", confidence)
            .param("Max ratio percentage error",
                stats.getMaximumPercentageMargin(confidence).toString())
            .param("ANOVA", stats.getAnova())
            .toString();
        String exprStr = System.lineSeparator();
        if (stats instanceof Stats) {
            Stats eStats = (Stats) stats;
            ExpressionSolver exprSolver =
                    eStats.getPayload(ExpressionSolver.class);
            Map<TName, String> expressions =
                    exprSolver.getStringExpressions();
            if (!expressions.isEmpty()) {
                TableFormatter expr = new TableFormatter("  ")
                        .row("name", "expression");
                expressions.forEach((TName name, String str) ->
                        expr.row(name.toString(), str));
                exprStr = expr.toString() + System.lineSeparator();
            }
        }
        return header + exprStr;
    }

    protected TableFormatter createPerformanceTable(
            Stats stats, Ratio confidence) {
        TableFormatter performanceTable = new TableFormatter("  ");
        createHeaderLine(performanceTable);
        int index = 0;
        for (Map.Entry<TName,DimensionalMeasure> e :
                stats.getMeasureMap().entrySet()) {
            TName name = e.getKey();
            DimensionalMeasure measure = e.getValue();
            Unit<?> unit = measure.getUnit();
            double stdev = measure.getUnbiasedStandardDeviation();

            createTableLine(performanceTable, index, name, measure, stats, stdev,
                    unit, confidence);
            index++;
        }
        return performanceTable;
    }

}
