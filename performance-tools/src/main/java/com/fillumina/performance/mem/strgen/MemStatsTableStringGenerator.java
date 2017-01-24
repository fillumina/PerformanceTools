package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemPerformance;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.MemUnit;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsTableStringGenerator
        implements StringGenerator<MemStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemStatsTableStringGenerator INSTANCE =
            new MemStatsTableStringGenerator("");

    public static final MemStatsTableStringGenerator USED_INSTANCE =
            new MemStatsTableStringGenerator("Used Memory:" +
                    System.lineSeparator());

    public static final MemStatsTableStringGenerator ALLOCATED_INSTANCE =
            new MemStatsTableStringGenerator("Allocated Memory:" +
                    System.lineSeparator());

    public static final PerformanceConsumer<MemStats> appendTo(
            Appendable appendable) {
        return new PerformanceViewer<>(INSTANCE, appendable);
    }

    public static final PerformanceConsumer<MemStats> appendUsedMemTo(
            Appendable appendable) {
        return new PerformanceViewer<>(USED_INSTANCE, appendable);
    }

    public static final PerformanceConsumer<MemStats> appendAllocatedMemTo(
            Appendable appendable) {
        return new PerformanceViewer<>(ALLOCATED_INSTANCE, appendable);
    }


    private final String title;
    private final PerformanceViewer<MemStats> viewer;

    protected MemStatsTableStringGenerator(String title) {
        this.title = title;
        this.viewer = new PerformanceViewer<>(this);
    }

    public PerformanceViewer<MemStats> viewer() {
        return viewer;
    }

    @Override
    public String toString(ComposedName name, MemStats stats) {
        StringBuilder buf = new StringBuilder();
        if (name != null && !name.isEmpty()) {
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
        TableFormatter memoryTable = createMemoryTable(stats, unit);
        if (!memoryTable.isEmpty()) {
            return title + memoryTable.toString();
        }
        return null;
    }

    private TableFormatter createMemoryTable(final MemStats stats,
            MemUnit unit) {
        TableFormatter memoryTable = new TableFormatter("  ");
        memoryTable
            .cell("test name")
            .cell("mean (samples used)")
            .cell("conf")
            .cell("stdev")
            .cell("min")
            .cell("max")
            .endl();
        for (final MemPerformance mp : stats.getPerformances().values()) {
            Measure mem = mp.getUsedMemory();
            memoryTable
                .cell(mp.getTestName())
                .cell(MemUnit.FORMATTER.toString(mem, 0.99, unit))
                .cell("99 %")
                .cell(MemUnit.FORMATTER.toString(
                        mem.getUnbiasedStandardDeviation(), unit))
                .cell(MemUnit.FORMATTER.toString(mem.getMin(), unit))
                .cell(MemUnit.FORMATTER.toString(mem.getMax(), unit))
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
