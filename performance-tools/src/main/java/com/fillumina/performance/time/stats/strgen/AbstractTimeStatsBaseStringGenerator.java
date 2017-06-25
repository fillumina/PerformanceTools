package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.ThroughputUnit;
import com.fillumina.performance.util.unit.Unit;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTimeStatsBaseStringGenerator
        implements
            AssertableStringGenerator<TimeStats>,
            Selectable<Assertable>,
            Serializable {

    private static final long serialVersionUID = 1L;
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_99;

    protected final Ratio confidence;

    public AbstractTimeStatsBaseStringGenerator() {
        this(DEFAULT_CONFIDENCE);
    }

    public AbstractTimeStatsBaseStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    protected abstract boolean isStatsAssignableFrom(Assertable assertable);

    @Override
    public int selectableRank(Assertable assertable) {
        return isStatsAssignableFrom(assertable) ? 1 : -1;
    }

    protected IntervalUnit calculateUnit(TimeStats stats) {
        final Map<TName, SingleTimeStats> testMap = stats.getSingleStatsMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        for (SingleTimeStats tp : testMap.values()) {
            times[counter] = tp.getMeasure().getMean();
            counter++;
        }
        final IntervalUnit unit = (IntervalUnit)
                IntervalUnit.getHelper().getUnit(times);
        return unit;
    }

    protected void appendTitle(Appendable appendable, TimeStats stats)
            throws IOException {
        TName testPrefix = TName.extractCommonPrefix(stats.getTestNames());
        if (testPrefix != null && !testPrefix.isEmpty()) {
            appendable.append("Speed of '")
                    .append(testPrefix.toString())
                    .append("' :");
        } else {
            appendable.append("Speed:");
        }
        appendable.append(System.lineSeparator());
    }

    static String throughputToaverageTime(ConfidenceInterval ci) {
        double freq = 1.0 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit unit = IntervalUnit.getHelper().getUnit(freq, error);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());
    }

    static String averageTimeToThroghput(ConfidenceInterval ci) {
        double freq = 1.0 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit unit = ThroughputUnit.getHelper().getUnit(freq, error);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());

        // TODO remove this
//        if (freq > 1.0) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f Gop/s", freq, error);
//        }
//
//        freq *= 1_000.0;
//        error *= 1_000.0;
//        if (freq > 1.0) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f Mop/s", freq, error);
//        }
//
//        freq *= 1_000.0;
//        error *= 1_000.0;
//        if (freq > 1.0) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f Kop/s", freq, error);
//        }
//
//        freq *= 1_000.0;
//        error *= 1_000.0;
//        if (freq > 0.1) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f op/s", freq, error);
//        }
//
//        freq *= 60;
//        error *= 60;
//        if (freq > 0.1) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f op/m", freq, error);
//        }
//
//        freq *= 60;
//        error *= 60;
//        if (freq > 0.1) {
//            return String.format(Locale.US, "%,.2f +/- %,.2f op/h", freq, error);
//        }
//
//        freq *= 24;
//        error *= 24;
//        return String.format(Locale.US, "%,.2f +/- %,.2f op/d", freq, error);
    }

}
