package com.fillumina.performance;

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
        return String.format("\t%,10.2f ", converted) +
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
}
