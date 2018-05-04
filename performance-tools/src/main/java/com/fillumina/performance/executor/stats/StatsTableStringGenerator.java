package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.CamelCaseUtils;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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

        TableFormatter performance;
        if (isAllTestsHaveOneSampleOnly(stats)) {
            performance = createTableForPercentage(stats);
        } else {
            appendable.append(creteHeader(stats, confidence));
            appendable.append(System.lineSeparator());
            if (stats.getNames().size() == 1) {
                performance = createTableForSingleTest(stats, confidence);
            } else {
                performance = createPerformanceTable(stats, confidence);
            }
        }
        appendable.append(performance.toString());
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
            Stats stats, Ratio confidence) {
        TableFormatter performanceTable = new TableFormatter("  ");
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
        int index = 0;
        for (Map.Entry<TName,DimensionalMeasure> e :
                stats.getMeasureMap().entrySet()) {
            TName name = e.getKey();
            DimensionalMeasure measure = e.getValue();
            Unit<?> unit = measure.getUnit();
            final double stdev = unit.convert(
                    measure.getUnbiasedStandardDeviation(),
                    measure.getUnit());

            List<TName> names = stats.getNames();
            int testPrefixSize = TName.commonPrefix(names).size();

            double tukeyHsd = stats.getTukeyHsdComparedToRef(name);
            String tukeyHsdStr = tukeyHsd < 0 ? "" :
                    String.format(Locale.US,"%.3f", tukeyHsd);

            performanceTable
                .cell(index)
                .cell(name.toStringWithSeparatorStartingFrom("_", testPrefixSize))
                .cell(stats.getRatioWithRef(name, confidence)
                        .toStringAsPercentage())
                .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                .cell(String.format(Locale.US,"%.3f %s", stdev, unit))
                .cell(measure.getFractionalUncertainty(confidence))
                .cell(measure.getCount())
                .cell(tukeyHsdStr)
                .endl();
            index++;
        }
        return performanceTable;
    }

    private TableFormatter createTableForSingleTest(
            Stats stats,
            Ratio confidence) {
        TName name = stats.getNames().iterator().next();
        DimensionalMeasure m = stats.getMeasureMap().get(name);
        Unit<?> unit = m.getUnit();
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

    private TableFormatter createTableForPercentage(Stats stats) {
        TableFormatter performanceTable = new TableFormatter("  ");
        performanceTable
                .cell("idx")
                .cell("name")
                .cell("ratio")
                .cell("value")
                .endl();
        int index = 0;
        for (Map.Entry<TName,DimensionalMeasure> e :
                stats.getMeasureMap().entrySet()) {
            TName name = e.getKey();
            DimensionalMeasure measure = e.getValue();
            Unit<?> unit = measure.getUnit();

            int testPrefixSize = TName.commonPrefix(stats.getNames()).size();

            performanceTable
                    .cell(index)
                    .cell(name.toStringWithSeparatorStartingFrom("_", testPrefixSize))
                    .cell(String.format(Locale.US, "%.2f %%",
                            stats.getRatioWithRef(name, confidence).getValue() * 100.0) )
                    .cell(measure.toStringForConfidenceWitoutSamples(
                        confidence, unit))
                    //.cell(String.format(Locale.US, "%d", (long)measure.getMean()) )
                    .endl();
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

    private static boolean isAllTestsHaveOneSampleOnly(Stats stats) {
        long maxCount = 0;
        for (Measure m : stats.getMeasureMap().values()) {
            long count = m.getCount();
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return maxCount <= 1;
    }
}
