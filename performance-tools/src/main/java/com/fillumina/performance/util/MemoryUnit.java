package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Measure;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public enum MemoryUnit {

    B(1), KiB(1E3), MiB(1E6), GiB(1E9);

    final private double factor;

    MemoryUnit(double factor) {
        this.factor = factor;
    }

    public double convert(final double value) {
        final long hundredNano = Math.round(value * 100);
        return (hundredNano * 1.0 / factor) / 100d;
    }

    public static String prettyPrint(Measure m) {
        final double mean = m.getMean();
        final MemoryUnit unit = minTimeUnit(magnitude(mean));
        final double cMean = unit.convert(mean);
        final double cMargin = unit.convert(m.getMarginOfError(0.99));
        return String.format("%.3f ± %.3f ", cMean, cMargin)  + unit.toString();
    }

    public static double convert(final double value, final MemoryUnit unit) {
        final long hundredNano = Math.round(value * 100);
        return unit.convert(hundredNano, MemoryUnit.B) / 100d;
    }

    /** @param value time in nanoseconds. */
    public static String prettyPrint(final double value) {
        final MemoryUnit unit = minTimeUnit(magnitude(value));
        return String.format("%3.3f " + unit.toString(), value / unit.factor);
    }

    /** @return the right {@link TimeUnit} depending on the given magnitude
     *           of nanoseconds.
     */
    public static MemoryUnit minTimeUnit(final int magnitude) {
        switch (magnitude) {
            case -2:
            case -1:
            case 0:
            case 1:
            case 2:return B;
            case 3:
            case 4:
            case 5: return KiB;
            case 6:
            case 7:
            case 8: return MiB;
            case 9:
            case 10:
            case 11:
            case 12:
            default: return GiB;
        }
    }

    public static MemoryUnit minTimeUnit(final double[] values) {
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
