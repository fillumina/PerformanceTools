package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.Ratio;
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
public final class SignificanceMatrixStringGenerator
        implements StringGenerator<Stats>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final SignificanceMatrixStringGenerator INSTANCE =
            new SignificanceMatrixStringGenerator();

    public static final Consumer<Stats> appendOnlyEqualTestTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new SignificanceMatrixStringGenerator(confidence, true),
                        appendable);
    }

    public static final Consumer<Stats> appendTo(
            Appendable appendable, Ratio confidence) {
        return new Viewer<>(
                new SignificanceMatrixStringGenerator(confidence, false),
                        appendable);
    }

    private final Ratio confidence;
    private final boolean onlyEqualTest;

    public SignificanceMatrixStringGenerator() {
        this(Ratio.P_99, true);
    }

    public SignificanceMatrixStringGenerator(Ratio confidence,
            boolean onlyEqualTest) {
        this.confidence = confidence;
        this.onlyEqualTest = onlyEqualTest;
    }

    @Override
    public void appendTo(Appendable appendable, Stats stats)
            throws IOException {
        if (stats.isEmpty() || stats.getNames().size() < 2 ||
                ! isEqualTestsPresent(stats)) {
            return;
        }
        appendable.append("Ratio Matrix (confidence= ")
            .append(String.format(Locale.US,"%.3f %%",
                confidence.getPercentage()))
            .append(") using Games-Howell Significance Test:")
            .append(System.lineSeparator());

        TableFormatter table = createSignificanceTable(stats, confidence);
        appendable.append(table.toString());

        appendable.append(System.lineSeparator());
    }

    private TableFormatter createSignificanceTable(Stats stats, Ratio confidence) {
        TableFormatter table = new TableFormatter("  ");
        table
                .cell("test names").span(3)
                .cell("percentage")
                .cell("inverse")
                .cell("significance")
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
                    addTukey(table, stats, iname, jname, confidence);
                } else {
                    addTukey(table, stats, jname, iname, confidence);
                }
            }
        }
        return table;
    }

    private void addTukey(TableFormatter table,
            final Stats stats, PathName iname, PathName jname, Ratio confidence) {
        double significance = stats.getSignificance(iname, jname);
        if (onlyEqualTest && areEquals(significance)) {
            table
                    .cell(iname.toString())
                    .cell("&")
                    .cell(jname.toString())
                    .cell(stats.getRatio(iname, jname, confidence)
                            .toAlternativeString())
                    .cell("(", stats.getRatio(jname, iname, confidence)
                            .toAlternativeString(), ")")
                    .cell(String.format(Locale.US,"%.3f", significance));
            if (significance >= 0.9) {
                table.cell("different");
            } else if (areEquals(significance)) {
                table.cell("equals");
            } else {
                table.cell("uncertain");
            }
            table.endl();
        }
    }

    private boolean isEqualTestsPresent(Stats stats) {
        List<PathName> list = new ArrayList<>(stats.getNames());
        int size = list.size();
        for (int i=0; i<size; i++) {
            PathName iname = list.get(i);
            for (int j=i + 1; j<size; j++) {
                PathName jname = list.get(j);
                double significance = stats.getSignificance(iname, jname);
                if (areEquals(significance)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areEquals(double significance) {
        return significance <= 0.3;
    }
}
