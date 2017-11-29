package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.MemStatsType;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
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
        implements StringGenerator<Stats>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StringGenerator<Stats> INSTANCE =
            new StringGenerator<Stats>() {
        @Override
        public void appendTo(Appendable appendable, Stats stats)
                throws IOException {
            if (stats.getStatsType().equals(MemStatsType.USED)) {
                USED_INSTANCE.appendTo(appendable, stats);
            } else if (stats.getStatsType().equals(MemStatsType.ALLOCATED)) {
                ALLOCATED_INSTANCE.appendTo(appendable, stats);
            }
        }
    };

    public static final StringGenerator<Stats> USED_INSTANCE =
            new MemStatsTableStringGenerator("Used");

    public static final StringGenerator<Stats> ALLOCATED_INSTANCE =
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

    public Viewer<Stats> viewer() {
        return new Viewer<>(this);
    }

    @Override
    public void appendTo(Appendable appendable, Stats memStats)
            throws IOException {
        appendable.append(toString(memStats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(Stats stats) {
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
    public String getTable(final Stats stats, final MemUnit unit) {
        String name = stats.getMeasureMap().keySet().iterator().next()
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

    private TableFormatter createMemoryTable(final Stats stats,
            MemUnit unit) {
        final TableFormatter memoryTable = new TableFormatter("  ");
        memoryTable
            .cell("test name")
            .cell("mean (samples used)")
            .cell("conf")
            .cell("stdev")
            .cell("min")
            .cell("max")
            .endl();
        stats.getMeasureMap().forEach( (TName n, DimensionalMeasure m) -> {
            memoryTable
                .cell(n.getLastName())
                .cell(Units.toString(m, confidence, unit))
                .cell(confidence.toString())
                .cell(Units.toString(
                        m.getUnbiasedStandardDeviation(), 0, unit))
                .cell(Units.toString(m.getMin(), 0, unit))
                .cell(Units.toString(m.getMax(), 0, unit))
                .endl();
        });
        return memoryTable;
    }

    private MemUnit calculateMinUnit(Stats stats) {
        final Map<TName, DimensionalMeasure> testMap = stats.getMeasureMap();
        double[] memory = new double[testMap.size()];
        int counter = 0;
        for (DimensionalMeasure m : testMap.values()) {
            memory[counter] = m.getMean();
            counter++;
        }
        return MemUnit.UNITS.calculateAppropriatedUnitFrom(memory);
    }
}
