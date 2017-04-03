package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SingleTestStats;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.Serializable;
import java.util.Locale;
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
    public String toString(PHolder<SpeedStats> holder) {
        SpeedStats stats = holder.getStats();
        StringBuilder buf = new StringBuilder();
        buf.append(System.lineSeparator());
        return buf.append(toString(stats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    public String toString(SpeedStats stats) {
        final Map<String, SingleTestStats> testMap = stats.getPerformanceMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (SingleTestStats tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final IntervalUnit unit = (IntervalUnit)
                IntervalUnit.getHelper().getUnit(times);
        return getString(stats, unit);
    }

    static String frequencyToString(ConfidenceInterval ci) {
        double freq = 1E9 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());
        if (freq > 0.1) {
            return String.format(Locale.US, "%,.2f +/- %,.2f op/s", freq, error);
        }
        freq *= 60;
        error *= 60;
        if (freq > 0.1) {
            return String.format(Locale.US, "%,.2f +/- %,.2f op/m", freq, error);
        }
        freq *= 60;
        error *= 60;
        if (freq > 0.1) {
            return String.format(Locale.US, "%,.2f +/- %,.2f op/h", freq, error);
        }
        freq *= 24;
        error *= 24;
        return String.format(Locale.US, "%,.2f +/- %,.2f op/d", freq, error);
    }

}
