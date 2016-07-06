package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StringOutputHolder;
import com.fillumina.performance.util.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.MemUnit;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemTableStringGenerator
        implements StringGenerator<MemStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemTableStringGenerator INSTANCE =
            new MemTableStringGenerator();

    public static final PerformanceViewer<MemStats> VIEWER =
            new PerformanceViewer<>(INSTANCE);

    protected MemTableStringGenerator() {}

    @Override
    public String toString(ComposedName name, MemStats stats) {
        StringBuilder buf = new StringBuilder();
        if (!name.isEmpty()) {
            buf.append(TableFormatter.title(name.toString(), '-'));
        }
        return buf.append(toString(stats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(MemStats stats) {
        MemUnit unit = calculateMinUnit(stats);
        return getTable(stats, unit);
    }

    /**
     * Display a human readable text only multi line string with the
     * passed performances.
     *
     * @param title             The title of the table.
     * @param stats  The performances to display.
     * @param unit              The unit of time to use.
     * @return uses a {@link StringOutputHolder} for an easier manipulation
     *          using the
     *          <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     *          fluent interface</a></i>.
     */
    public String getTable(final MemStats stats,
            final MemUnit unit) {
        StringBuilder buf = new StringBuilder();

        TableFormatter memoryTable = createMemoryTable(stats, unit);
        if (!memoryTable.isEmpty()) {

            buf.append("\nMemory Usage:")
               .append(System.lineSeparator())
               .append(memoryTable.toString());
        }

        return buf.toString();
    }

    private TableFormatter createMemoryTable(final MemStats stats,
            MemUnit unit) {
        TableFormatter memoryTable = new TableFormatter("  ");
        for (final MemPerformance mp : stats.getPerformances().values()) {
            Measure memoryUsed = mp.getUsedMemory();
            memoryTable
                    .cell(mp.getTestName())
                    .cell(MemUnit.FORMATTER.toString(memoryUsed, 0.99, unit))
                    .cell("stdev = ", MemUnit.FORMATTER.toString(
                            memoryUsed.getUnbiasedStandardDeviation(), unit))
                    .endl();
        }
        return memoryTable;
    }

    private MemUnit calculateMinUnit(MemStats stats) {
        final Map<String, MemPerformance> testMap = stats.getPerformances();
        double[] memory = new double[testMap.size()];
        int counter = 0;
        for (MemPerformance mp : testMap.values()) {
            memory[counter] = mp.getUsedMemory().getMean();
            counter++;
        }
        final MemUnit unit = MemUnit.FORMATTER.getMinUnit(memory);
        return unit;
    }
}
