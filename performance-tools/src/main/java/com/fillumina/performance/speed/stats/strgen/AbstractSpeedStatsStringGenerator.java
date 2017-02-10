package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSpeedStatsStringGenerator
        implements StringGenerator<SpeedStats>, Serializable {
    private static final long serialVersionUID = 1L;

    protected abstract String getString(SpeedStats stats, IntervalUnit unit);

    @Override
    public String toString(ComposedName name, SpeedStats stats) {
        StringBuilder buf = new StringBuilder();
        buf.append(System.lineSeparator());
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
    public String toString(SpeedStats stats) {
        final Map<String, TestPerformance> testMap = stats.getPerformances();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (TestPerformance tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final IntervalUnit unit = (IntervalUnit)
                IntervalUnit.getHelper().getUnit(times);
        return getString(stats, unit);
    }

    static String frequencyToString(double value) {
        double freq = 1E9 / value;
        if (freq > 0.1) {
            return String.format("%,.6f op/s", freq);
        }
        freq *= 60;
        if (freq > 0.1) {
            return String.format("%,.6f op/m", freq);
        }
        freq *= 60;
        if (freq > 0.1) {
            return String.format("%,.6f op/h", freq);
        }
        freq *= 24;
        return String.format("%,.6f op/d", freq);
    }

}
