package com.fillumina.performance.util;

import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Implements a "what if" logic searching for the tolerance that would allow
 * the predicate to pass.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RequiredTolerance {

    private final Map<RelativeOrder, Ratio> map;

    public RequiredTolerance(BiPredicate<RelativeOrder, Ratio> predicate) {
        EnumMap<RelativeOrder, Ratio> m = new EnumMap<>(RelativeOrder.class);
        for (RelativeOrder order : RelativeOrder.values()) {
            Ratio ratio = findToleranceRequiredToSatisfyCondition(predicate, order);
            if (!ratio.isZero() && ratio.isValid()) {
                m.put(order, ratio);
            }
        }
        this.map = Collections.unmodifiableMap(m);
    }

    public Ratio getRequiredToleranceFor(RelativeOrder relativeOrder) {
        return map.get(relativeOrder);
    }

    public Map<RelativeOrder, Ratio> getMap() {
        return map;
    }

    /** What if scenario proposed as solution for the error. */
    public void appendWhatIfTolerance(StringBuilder buf) {
        buf.append(TableFormatter.title("Would have been:", '-'));
        map.entrySet().forEach((e) -> {
                    Ratio t = e.getValue();
                    buf.append(e.getKey().name().toLowerCase())
                            .append(" if tolerance >= ")
                            .append(t)
                            .append(System.lineSeparator());
        });
        buf.append(System.lineSeparator());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        appendWhatIfTolerance(buf);
        return buf.toString();
    }

    private static Ratio findToleranceRequiredToSatisfyCondition(
            final BiPredicate<RelativeOrder, Ratio> isConditionSatisfied,
            final RelativeOrder o) {

        int p = ExpBinarySearcher.searchGreaterOrEquals(0, Integer.MAX_VALUE,
                v -> isConditionSatisfied.test(o, Ratio.percentage(v)));

        if (p == -1) {
            return Ratio.INVALID;
        } else if (p == 0) {
            return Ratio.ZERO;
        }
        return Ratio.percentage(p);
    }

}
