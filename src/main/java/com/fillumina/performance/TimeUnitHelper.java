package com.fillumina.performance;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class TimeUnitHelper {

    public static String formatUnit(final double value, final TimeUnit unit) {
        final long hundredNano = Math.round(value * 100);
        final double converted =
                unit.convert(hundredNano, TimeUnit.NANOSECONDS) / 100d;
        return String.format("%,10.2f ", converted) +
                TimeUnitHelper.printSymbol(unit);
    }

    public static String printSymbol(final TimeUnit unit) {
        switch (unit) {
            case DAYS: return "d";
            case HOURS: return "h";
            case MICROSECONDS: return "us";
            case MILLISECONDS: return "ms";
            case MINUTES: return "min";
            case NANOSECONDS: return "ns";
            case SECONDS: return "s";
            default:
                return unit.toString();
        }
    }

    /** @return the right {@link TimeUnit} depending on the given magnitude
     *           of nanoseconds.
     */
    public static TimeUnit minTimeUnit(final int magnitude) {
        switch (magnitude) {
            case 0:
            case 1:
            case 2: return TimeUnit.NANOSECONDS;
            case 3:
            case 4:
            case 5: return TimeUnit.MICROSECONDS;
            case 6:
            case 7:
            case 8: return TimeUnit.MILLISECONDS;
            case 9:
            case 10:
            case 11: return TimeUnit.SECONDS;
            case 12: return TimeUnit.MINUTES;
            case 13: return TimeUnit.HOURS;
            default: return TimeUnit.DAYS;
        }
    }

    public static TimeUnit minTimeUnit(final Collection<Double> values) {
        return minTimeUnit(minMagnitude(values));
    }

    public static int minMagnitude(final Collection<Double> values) {
        int min = Integer.MAX_VALUE;
        for (double v : values) {
            int magnitude = magnitude(v);
            if (magnitude < min) {
                min = magnitude;
            }
        }
        return min;
    }

    public static int magnitude(final double value) {
        for (int i=0; i<100; i++) {
            if (value < Math.pow(10, i)) {
                return i;
            }
        }
        throw new RuntimeException("number too big: " + value);
    }
}
