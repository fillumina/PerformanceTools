package com.fillumina.performance.time.stats.strgen;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.CamelCaseUtils;
import com.fillumina.performance.util.Selectable;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
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
public abstract class AbstractTimeStatsBaseStringGenerator
        implements StringGenerator<Stats>, Selectable<Stats>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Ratio DEFAULT_CONFIDENCE = Ratio.P_99;

    protected final Ratio confidence;

    public AbstractTimeStatsBaseStringGenerator() {
        this(DEFAULT_CONFIDENCE);
    }

    public AbstractTimeStatsBaseStringGenerator(Ratio confidence) {
        this.confidence = confidence;
    }

    protected abstract boolean isStatsAssignableFrom(Stats assertable);

    @Override
    public int selectableRank(Stats assertable) {
        return isStatsAssignableFrom(assertable) ? 1 : -1;
    }

    protected Unit<?> calculateUnit(Stats stats) {
        final Map<TName, DimensionalMeasure> testMap = stats.getMeasureMap();
        double[] times = new double[testMap.size()];
        int counter = 0;
        Units<?> units = Magnitude.UNITS;
        for (DimensionalMeasure measure : testMap.values()) {
            units = measure.getUnit().units();
            times[counter] = measure.getMean();
            counter++;
        }
        return units.calculateAppropriatedUnitFrom(times);
    }

    protected void appendTitle(Appendable appendable, Stats stats)
            throws IOException {
        TName testPrefix = TName.commonPrefix(stats.getNames());
        String statsType = CamelCaseUtils.camelCaseToSentence(
                stats.getStatsType().toString());
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

    static String throughputToAverageTime(ConfidenceInterval ci) {
        double freq = 1E9 / ci.getValue();
        double error = freq *
                ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit<?> unit = AverageTimeUnit.UNITS
                .calculateAppropriatedUnitFrom(freq);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());
    }

    static String averageTimeToThroghput(ConfidenceInterval ci) {
        double freq = 1E9 / ci.getValue();
        double error = freq *
                ((ci.getUpperBound() - ci.getValue()) / ci.getValue());

        Unit<?> unit = ThroughputUnit.UNITS
                .calculateAppropriatedUnitFrom(freq);

        return String.format(Locale.US, "%,.2f +/- %,.2f %s",
                unit.convertFromBase(freq),
                unit.convertFromBase(error),
                unit.toString());
    }

}
