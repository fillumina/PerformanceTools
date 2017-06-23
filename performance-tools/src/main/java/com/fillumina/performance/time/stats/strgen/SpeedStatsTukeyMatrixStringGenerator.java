package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class SpeedStatsTukeyMatrixStringGenerator
        extends AbstractSpeedStatsStringGenerator {
    private static final long serialVersionUID = 1L;

    public static final SpeedStatsTukeyMatrixStringGenerator INSTANCE =
            new SpeedStatsTukeyMatrixStringGenerator();

    public static final PerformanceViewer<TimeStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    @SuppressWarnings("unchecked")
    public static final PerformanceConsumer<TimeStats> getViewer() {
        return (PerformanceConsumer<TimeStats>) VIEWER;
    }

    public static final PerformanceConsumer<TimeStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    private final Ratio confidence;

    public SpeedStatsTukeyMatrixStringGenerator() {
        this.confidence = DEFAULT_CONFIDENCE;
    }

    public SpeedStatsTukeyMatrixStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    @Override
    protected String getString(TimeStats stats, IntervalUnit unit) {
        if (stats.isEmpty() || stats.getTestNames().size() < 2) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        buf.append("Ratio Matrix (confidence= ")
            .append(String.format(Locale.US,"%.3f %%",
                confidence.getPercentage()))
            .append("):")
            .append(System.lineSeparator());

        TableFormatter tukeyTable = createTukeyTable(stats);
        buf.append(tukeyTable.toString());

        return buf.append(System.lineSeparator()).toString();
    }

    private TableFormatter createTukeyTable(final TimeStats stats) {
        TableFormatter tukeyTable = new TableFormatter("  ");
        tukeyTable
                .cell("test names").span(3)
                .cell("percentage")
                .cell("inverse")
                .cell("tukeyHSD")
                .cell("equality")
                .endl();
        List<TName> list = new ArrayList<>(stats.getTestNames());
        int size = list.size();
        for (int i=0; i<size; i++) {
            TName iname = list.get(i);
            for (int j=i + 1; j<size; j++) {
                TName jname = list.get(j);
                if (stats.getMeasure(iname).getMean() <
                        stats.getMeasure(jname).getMean()) {
                    addTukey(stats, iname, jname, tukeyTable);
                } else {
                    addTukey(stats, jname, iname, tukeyTable);
                }
            }
        }
        return tukeyTable;
    }

    private void addTukey(final TimeStats stats, TName iname, TName jname,
            TableFormatter tukeyTable) {
        double tukey = stats.getTukeyHsd(iname, jname);
        tukeyTable
                .cell(iname.toString())
                .cell("vs")
                .cell(jname.toString())
                .cell(stats.getRatio(iname, jname, confidence)
                        .toAlternativeString())
                .cell("(", stats.getRatio(jname, iname, confidence)
                        .toAlternativeString(), ")")
                .cell(String.format(Locale.US,"%.3f", tukey));
        if (tukey >= 0.9) {
            tukeyTable.cell("different");
        } else if (tukey <= 0.85) {
            tukeyTable.cell("equals");
        } else {
            tukeyTable.cell("uncertain");
        }
        tukeyTable.endl();
    }
}
