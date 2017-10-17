package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
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
public class MemStatsTableStringGenerator<M extends MemStats>
        implements StringGenerator<M>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringGenerator<MemStats> INSTANCE =
            new StringGenerator<MemStats>() {
        @Override
        public void appendTo(Appendable appendable, MemStats memStats)
                throws IOException {
            if (memStats instanceof UsedMemStats) {
                USED_INSTANCE.appendTo(appendable, (UsedMemStats) memStats);
            } else {
                ALLOCATED_INSTANCE.appendTo(appendable, (AllocatedMemStats) memStats);
            }
        }
    };

    public static final StringGenerator<UsedMemStats> USED_INSTANCE =
            new MemStatsTableStringGenerator<>("Used");

    public static final StringGenerator<AllocatedMemStats> ALLOCATED_INSTANCE =
            new MemStatsTableStringGenerator<>("Allocated");

    private final String memType;
    private final Ratio confidence;

    protected MemStatsTableStringGenerator(String memType) {
        this(memType, Ratio.P_95);
    }

    protected MemStatsTableStringGenerator(String memType, Ratio confidence) {
        this.memType = memType;
        this.confidence = confidence;
    }

    public Viewer<M> viewer() {
        return new Viewer<>(this);
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
     * Display a human readable text only multi row string with the
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
        String name = stats.getSingleStatsMap().keySet().iterator().next()
                .getPrefix();

        String title;
        if (name != null && !name.isEmpty()) {
            title = " for " + name + " :";
        } else {
            title = ":";
        }

        TableFormatter memoryTable = createMemoryTable(stats, unit);
        if (!memoryTable.isEmpty()) {
            return memType +
                    " memory" + title + System.lineSeparator() +
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
        for (final SingleStats s : stats.getSingleStatsMap().values()) {
            Measure mem = s.getMeasure();
            memoryTable
                .cell(s.getName().getLastName())
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
        final Map<TName, SingleStats> testMap = stats.getSingleStatsMap();
        double[] memory = new double[testMap.size()];
        int counter = 0;
        for (SingleStats mp : testMap.values()) {
            memory[counter] = mp.getMeasure().getMean();
            counter++;
        }
        return MemUnit.UNITS.calculateAppropriatedUnitFrom(memory);
    }
}
