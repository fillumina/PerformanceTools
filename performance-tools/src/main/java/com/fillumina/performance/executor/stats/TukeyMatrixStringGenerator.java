package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.pathname.PathName;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Produces a human readable multi-row string of statistics.
 *
 * @author Francesco Illuminati
 */
public final class TukeyMatrixStringGenerator
        implements StringGenerator<Stats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final TukeyMatrixStringGenerator INSTANCE =
            new TukeyMatrixStringGenerator();

    public static final Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new TukeyMatrixStringGenerator(confidence), appendable);
    }

    private final Ratio confidence;

    public TukeyMatrixStringGenerator() {
        this(Ratio.P_99);
    }

    public TukeyMatrixStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    @Override
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        if (stats.isEmpty() || stats.getNames().size() < 2) {
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

    private TableFormatter createTukeyTable(Stats stats, Ratio confidence) {
        TableFormatter tukeyTable = new TableFormatter("  ");
        tukeyTable
                .cell("test names").span(3)
                .cell("percentage")
                .cell("inverse")
                .cell("tukeyHSD")
                .cell("equality")
                .endl();
        List<PathName> list = new ArrayList<>(stats.getNames());
        int size = list.size();
        for (int i=0; i<size; i++) {
            PathName iname = list.get(i);
            for (int j=i + 1; j<size; j++) {
                PathName jname = list.get(j);
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

    private void addTukey(final Stats stats,
            PathName iname, PathName jname,
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
