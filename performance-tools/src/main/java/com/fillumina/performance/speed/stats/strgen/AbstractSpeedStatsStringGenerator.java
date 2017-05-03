package com.fillumina.performance.speed.stats.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.speed.stats.SingleSpeedStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.IOException;
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
    public void toString(Appendable appendable, SpeedStats stats)
            throws IOException {
        appendable.append(toString(stats)).toString();
    }

    /**
     * Same as {@link #getTable(String, LoopPerformances, TimeUnit)} where
     * the time unit is calculated.
     */
    @Override
    public String toString(SpeedStats stats) {
        final Map<TName, SingleSpeedStats> testMap = stats.getSingleStatsMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (SingleSpeedStats tp : testMap.values()) {
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
