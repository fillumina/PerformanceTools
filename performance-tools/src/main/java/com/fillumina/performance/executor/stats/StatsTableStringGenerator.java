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
        implements StringGenerator<Stats>, Serializable  {
    private static final long serialVersionUID = 1L;

    public static final StatsTableStringGenerator INSTANCE =
            new StatsTableStringGenerator();

    public static final Viewer<Stats> VIEWER =
            new Viewer<>(INSTANCE);

    public static final Consumer<Stats> appendTo(
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
            Stats stats)
            throws IOException {

        appendTitle(appendable, stats);

        appendable.append(creteHeader(stats, confidence));
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
            Stats stats,
            Unit<?> unit,
            Ratio confidence) {
        TName name = stats.getNames().iterator().next();
        DimensionalMeasure m = stats.getMeasureMap().get(name);
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
            Stats stats,
            double stdev,
            Unit<?> unit,
            Ratio confidence) {
        int testPrefixSize = TName.commonPrefix(stats.getNames()).size();

        double tukeyHsd = stats.getTukeyHsdComparedToRef(name);
        String tukeyHsdStr = tukeyHsd < 0 ? "" :
                String.format(Locale.US,"%.3f", tukeyHsd);

        performanceTable
                .cell(index)
                .cell(name.toStringWithSeparatorFromIndex("_", testPrefixSize))
                .cell(stats.getRatioWithRef(name, confidence)
                        .toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s", stdev, unit))
                .cell(measure.getFractionalUncertainty(confidence))
                .cell(measure.getCount())
                .cell(tukeyHsdStr)
                .endl();
    }

    private String creteHeader(
            final Stats stats,
            Ratio confidence) {
        String header = new TableFormatter("  ")
            .param("Required measure confidence", confidence)
            .param("Max ratio percentage error",
                stats.getMaximumPercentageMargin(confidence).toString())
            .param("ANOVA", stats.getAnova())
            .toString();
        if (stats instanceof ExtendedStats) {
            ExtendedStats eStats = (ExtendedStats) stats;
            TableFormatter expr = new TableFormatter("  ")
                    .row("name", "expression");
            eStats.getExpressionsAsString().forEach((TName name, String str) ->
                    expr.row(name.toString(), str));
            header = header + System.lineSeparator() + expr.toString();
        }
        return header;
    }

    private TableFormatter createPerformanceTable(
            Stats stats, Unit<?> unit, Ratio confidence) {
        TableFormatter performanceTable = new TableFormatter("  ");
        createHeaderLine(performanceTable);
        int index = 0;
        for (Map.Entry<TName,DimensionalMeasure> e :
                stats.getMeasureMap().entrySet()) {
            TName name = e.getKey();
            DimensionalMeasure measure = e.getValue();
            final double stdev = unit.convert(
                    measure.getUnbiasedStandardDeviation(),
                    measure.getUnit());

            createTableLine(performanceTable, index, name, measure, stats, stdev,
                    unit, confidence);
            index++;
        }
        return performanceTable;
    }

    private void appendTitle(Appendable appendable,
            Stats stats)
            throws IOException {
        TName testPrefix = TName.commonPrefix(stats.getNames());
        String statsType = CamelCaseUtils.camelCaseToSentence(
                        stats.getStatsType().toString());
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

    private static  Unit<?> calculateUnit(Stats stats) {
        final Map<TName, DimensionalMeasure> testMap = stats.getMeasureMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        Units<?> units = Magnitude.UNIT.units();
        for (DimensionalMeasure measure : testMap.values()) {
            units = measure.getUnit().units();
            times[counter] = measure.getUnit().convertToBase(measure.getMean());
            counter++;
        }
        return units.calculateAppropriatedUnitFrom(times);
    }
}
