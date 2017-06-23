package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSpeedStatsStringGenerator
        implements StringGenerator<TimeStats>, Serializable {
    private static final long serialVersionUID = 1L;
    protected static final Ratio DEFAULT_CONFIDENCE = Ratio.P_999;

    protected abstract String getString(TimeStats stats, IntervalUnit unit);

    @Override
    public void appendTo(Appendable appendable, TimeStats stats)
            throws IOException {
        appendable.append(toString(stats)).toString();
    }

    @Override
    public String toString(TimeStats stats) {
        final Map<TName, SingleTimeStats> testMap = stats.getSingleStatsMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (SingleTimeStats tp : testMap.values()) {
            times[counter] = tp.getElapsedNanosecondsPerCycle().getMean();
            counter++;
        }
        final IntervalUnit unit = (IntervalUnit)
                IntervalUnit.getHelper().getUnit(times);
        return getString(stats, unit);
    }

    protected void appendTitlePrefix(StringBuilder buf, TimeStats stats) {
        TName testPrefix = TName.extractCommonPrefix(stats.getTestNames());
        if (testPrefix != null && !testPrefix.isEmpty()) {
            buf.append("Speed of '").append(testPrefix).append("' :");
        } else {
            buf.append("Speed:");
        }
        buf.append(System.lineSeparator());
    }

    // TODO make Frequency a Unit (and provide transformations to/from speed)
    static String frequencyToString(ConfidenceInterval ci) {
        double freq = 1.0 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        if (freq > 1.0) {
            return String.format(Locale.US, "%,.2f +/- %,.2f Gop/s", freq, error);
        }

        freq *= 1_000.0;
        error *= 1_000.0;
        if (freq > 1.0) {
            return String.format(Locale.US, "%,.2f +/- %,.2f Mop/s", freq, error);
        }

        freq *= 1_000.0;
        error *= 1_000.0;
        if (freq > 1.0) {
            return String.format(Locale.US, "%,.2f +/- %,.2f Kop/s", freq, error);
        }

        freq *= 1_000.0;
        error *= 1_000.0;
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
