package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Measure;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public class TimeUnitFormatter {

    public static String prettyPrint(final Measure m) {
        final double mean = m.getMean();
        final TimeUnit unit = minTimeUnit(magnitude(mean));
        final double cMean = convert(mean, unit);
        final double cMargin = convert(m.getMarginOfError(0.99), unit);
        return String.format("%.4f ± %.4f ", cMean, cMargin)  + unit.toString();
    }

    /** @param value time in nanoseconds. */
    public static String prettyPrint(final long value) {
        final TimeUnit result = minTimeUnit(magnitude(value));
        return print(value, result);
    }

    public static String print(final double value, final TimeUnit unit) {
        return convert(value, unit) + " " + TimeUnitFormatter.printSymbol(unit);
    }

    public static double convert(final double value, final TimeUnit unit) {
        final long hundredNano = Math.round(value * 100);
        return unit.convert(hundredNano, TimeUnit.NANOSECONDS) / 100d;
    }

    public static String formatUnit(final double value, final TimeUnit unit) {
        return formatUnit("%,10.2f ", value, unit);
    }

    public static String formatUnit(final String format,
            final double value, final TimeUnit unit) {
        return String.format(format, convert(value, unit)) + " " +
                TimeUnitFormatter.printSymbol(unit);
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
            case -2:
            case -1:
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
            case 10: return TimeUnit.SECONDS;
            case 11: return TimeUnit.MINUTES;
            case 12: return TimeUnit.HOURS;
            default: return TimeUnit.DAYS;
        }
    }

    public static TimeUnit minTimeUnit(final double[] values) {
        return minTimeUnit(minMagnitude(values));
    }

    public static int minMagnitude(final double[] values) {
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
        return (int) Math.round(Math.log10(Math.abs(value)));
    }
}
