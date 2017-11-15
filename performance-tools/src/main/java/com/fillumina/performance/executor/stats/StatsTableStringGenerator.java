package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.CamelCaseUtils;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Produces a human readable multi-row string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class StatsTableStringGenerator
        implements StringGenerator<Stats<? extends SingleStats>>, Serializable  {
    private static final long serialVersionUID = 1L;

    public static final StatsTableStringGenerator INSTANCE =
            new StatsTableStringGenerator();

    public static final Viewer<Stats<? extends SingleStats>> VIEWER =
            new Viewer<>(INSTANCE);

    public static final Consumer<Stats<? extends SingleStats>> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new StatsTableStringGenerator(confidence),
                appendable);
    }

    private final Ratio confidence;

    public StatsTableStringGenerator() {
        this(Ratio.P_99);
    }

    public StatsTableStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    @Override
    public void appendTo(Appendable appendable,
            Stats<? extends SingleStats> stats)
            throws IOException {

        appendTitle(appendable, stats);

        TableFormatter header = creteHeader(stats, confidence);
        appendable.append(header.toString());
        appendable.append(System.lineSeparator());

        Unit<?> unit = calculateUnit(stats);
        TableFormatter performance;
        if (stats.getNames().size() == 1) {
            performance = createTableForSingleTest(stats, unit, confidence);
        } else {
            performance = createPerformanceTable(stats, unit, confidence);
        }
        appendable.append(performance.toString());
        appendable.append(System.lineSeparator());
    }

    private void createHeaderLine(TableFormatter performanceTable) {
        performanceTable
                .cell("idx")
                .cell("name")
                .cell("ratio vs slower")
                .cell("mean")
                .cell("stdev")
                .cell("uncertainty")
                .cell("smpl")
                .cell("TukeyHSD")
                .endl();
    }

    private TableFormatter createTableForSingleTest(
            Stats<? extends SingleStats> stats,
            Unit unit,
            Ratio confidence) {
        TName name = stats.getNames().iterator().next();
        DimensionalMeasure m = stats.getSingleStatsMap().get(name).getMeasure();
        return new TableFormatter()
                .cell("name")
                .cell("mean")
                .cell("stdev")
                .cell("uncertainty")
                .cell("samples")
                .cell("confidence")
                .endl()
                .cell(name.getLastName())
                .cell(m.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s",
                        m.getStandardDeviation(), unit))
                .cell(m.getFractionalUncertainty(confidence))
                .cell(m.getCount())
                .cell(confidence)
                .endl();
    }

    private void createTableLine(
            TableFormatter performanceTable,
            int index,
            TName name,
            DimensionalMeasure measure,
            Stats<? extends SingleStats> stats,
            double stdev,
            Unit<?> unit,
            Ratio confidence) {
        int testPrefixSize = TName.commonPrefix(stats.getNames()).size();
        performanceTable
                .cell(index)
                .cell(name.toStringWithSeparatorFromIndex("_", testPrefixSize))
                .cell(stats.getRatio(name, confidence)
                        .toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s", stdev, unit))
                .cell(measure.getFractionalUncertainty(confidence))
                .cell(measure.getCount())
                .cell(String.format(Locale.US,"%.3f",
                        stats.getTukeyHsdComparedToRef(name)))
                .endl();
    }

    private TableFormatter creteHeader(
            final Stats<? extends SingleStats> stats,
            Ratio confidence) {
        TableFormatter header = new TableFormatter("  ")
            .param("Required measure confidence", confidence)
            .param("Max ratio percentage error",
                stats.getMaximumPercentageMargin(confidence).toString())
            .param("ANOVA", stats.getAnova());
        return header;
    }

    private TableFormatter createPerformanceTable(
            Stats<? extends SingleStats> stats, Unit<?> unit, Ratio confidence) {
        TableFormatter performanceTable = new TableFormatter("  ");
        createHeaderLine(performanceTable);
        int index = 0;
        for (final SingleStats tp : stats.getSingleStatsMap().values()) {
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

    private void appendTitle(Appendable appendable,
            Stats<? extends SingleStats> stats)
            throws IOException {
        TName testPrefix = TName.commonPrefix(stats.getNames());
        String statsType = CamelCaseUtils.camelCaseToSentence(
                stats.getClass().getSimpleName());
        appendable.append(statsType);
        if (testPrefix != null && !testPrefix.isEmpty()) {
            appendable.append(" '")
                    .append(testPrefix.toString())
                    .append('\'');
        }
        appendable
                .append(':')
                .append(System.lineSeparator());
    }

    private static  Unit<?> calculateUnit(Stats<? extends SingleStats> stats) {
        final Map<TName, ? extends SingleStats> testMap = stats.getSingleStatsMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        Units<?> units = Magnitude.UNIT.units();
        for (SingleStats tp : testMap.values()) {
            DimensionalMeasure measure = tp.getMeasure();
            units = measure.getUnit().units();
            times[counter] = measure.getMean();
            counter++;
        }
        return units.calculateAppropriatedUnitFrom(times);
    }
}
