package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.AssertableViewer;
import com.fillumina.performance.mem.MemPerformance;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;
import com.fillumina.performance.util.unit.Units;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsTableStringGenerator
        implements AssertableStringGenerator<MemStats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemStatsTableStringGenerator INSTANCE =
            new MemStatsTableStringGenerator("");

    public static final MemStatsTableStringGenerator USED_INSTANCE =
            new MemStatsTableStringGenerator("Used");

    public static final MemStatsTableStringGenerator ALLOCATED_INSTANCE =
            new MemStatsTableStringGenerator("Allocated");

    private final String memType;
    private final Ratio confidence;

    protected MemStatsTableStringGenerator(String memType) {
        this(memType, Ratio.P_95);
    }

    protected MemStatsTableStringGenerator(String memType, Ratio confidence) {
        this.memType = memType;
        this.confidence = confidence;
    }

    public AssertableViewer<MemStats> viewer() {
        return new AssertableViewer<>(MemStats.class, this);
    }

    @Override
    public void appendTo(Appendable appendable, MemStats memStats)
            throws IOException {
        appendable.append(toString(memStats)).toString();
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
        String name = stats.getPerformances().keySet().iterator().next()
                .getPrefix();

        String title;
        if (name != null && !name.isEmpty()) {
            title = " for " + name + " :";
        } else {
            title = ":";
        }

        TableFormatter memoryTable = createMemoryTable(stats, unit);
        if (!memoryTable.isEmpty()) {
            return memType + " memory" + title + System.lineSeparator() +
                    memoryTable.toString();
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
                .cell(mp.getTestName().getLastName())
                .cell(Units.toString(mem, confidence, unit))
                .cell(confidence.toString())
                .cell(Units.toString(
                        mem.getUnbiasedStandardDeviation(), 0, unit))
                .cell(Units.toString(mem.getMin(), 0, unit))
                .cell(Units.toString(mem.getMax(), 0, unit))
                .endl();
        }
        return memoryTable;
    }

    private MemUnit calculateMinUnit(MemStats stats) {
        final Map<TName, MemPerformance> testMap = stats.getPerformances();
        double[] memory = new double[testMap.size()];
        int counter = 0;
        for (MemPerformance mp : testMap.values()) {
            memory[counter] = mp.getUsedMemory().getMean();
            counter++;
        }
        return MemUnit.UNITS.calculateAppropriatedUnitFrom(memory);
    }
}
