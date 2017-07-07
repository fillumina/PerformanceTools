package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableConsumer;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Produces a human readable multi-line string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TimeStatsTukeyMatrixStringGenerator
        extends AbstractTimeStatsBaseStringGenerator<TimeStats> {
    private static final long serialVersionUID = 1L;

    public static final TimeStatsTukeyMatrixStringGenerator INSTANCE =
            new TimeStatsTukeyMatrixStringGenerator();

    public static final AssertableConsumer<TimeStats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new AssertableViewer<>(
                TimeStats.class,
                new TimeStatsTukeyMatrixStringGenerator(confidence),
                appendable);
    }

    public TimeStatsTukeyMatrixStringGenerator() {
        super();
    }

    public TimeStatsTukeyMatrixStringGenerator(Ratio confidence) {
        super(confidence);
    }

    @Override
    protected boolean isStatsAssignableFrom(Assertable assertable) {
        return assertable instanceof TimeStats &&
                ((TimeStats) assertable).getTestNames().size() > 1;
    }

    @Override
    public void appendTo(Appendable appendable, TimeStats stats)
            throws IOException {
        if (stats.isEmpty() || stats.getTestNames().size() < 2) {
            return;
        }
        appendable.append("Ratio Matrix (confidence= ")
            .append(String.format(Locale.US,"%.3f %%",
                confidence.getPercentage()))
            .append("):")
            .append(System.lineSeparator());

        TableFormatter tukeyTable = createTukeyTable(stats, confidence);
        appendable.append(tukeyTable.toString());

        appendable.append(System.lineSeparator());
    }

    private TableFormatter createTukeyTable(TimeStats stats, Ratio confidence) {
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
                    addTukey(stats, iname, jname, tukeyTable, confidence);
                } else {
                    addTukey(stats, jname, iname, tukeyTable, confidence);
                }
            }
        }
        return tukeyTable;
    }

    private void addTukey(final TimeStats stats,
            TName iname, TName jname,
            TableFormatter tukeyTable,
            Ratio confidence) {
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
        } else if (tukey <= 0.6) {
            tukeyTable.cell("equals");
        } else {
            tukeyTable.cell("uncertain");
        }
        tukeyTable.endl();
    }
}
