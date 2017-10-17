package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.time.stats.SingleTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.CamelCaseUtils;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ThroughputUnit;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTimeStatsBaseStringGenerator<A extends TimeStats>
        implements
            StringGenerator<A>,
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

    protected Unit<?> calculateUnit(TimeStats stats) {
        final Map<TName, SingleTimeStats> testMap = stats.getSingleStatsMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        Units<?> units = null;
        for (SingleTimeStats tp : testMap.values()) {
            DimensionalMeasure measure = tp.getMeasure();
            units = measure.getUnit().units();
            times[counter] = measure.getMean();
            counter++;
        }
        return units.calculateAppropriatedUnitFrom(times);
    }

    protected void appendTitle(Appendable appendable, TimeStats stats)
            throws IOException {
        TName testPrefix = TName.extractCommonPrefix(stats.getNames());
        String statsType = CamelCaseUtils.camelCaseToSentence(
                stats.getClass().getSimpleName());
        appendable.append(statsType);
        if (testPrefix != null && !testPrefix.isEmpty()) {
            appendable.append(" '")
                    .append(testPrefix.toString())
                    .append('\'');
        }
        appendable
                .append(':')
                .append(System.lineSeparator());
    }

    static String throughputToaverageTime(ConfidenceInterval ci) {
        double freq = 1E9 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit<?> unit = AverageTimeUnit.UNITS
                .calculateAppropriatedUnitFrom(freq);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());
    }

    static String averageTimeToThroghput(ConfidenceInterval ci) {
        double freq = 1E9 / ci.getValue();
        double error = freq * ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit<?> unit = ThroughputUnit.UNITS
                .calculateAppropriatedUnitFrom(freq);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());
    }

}
