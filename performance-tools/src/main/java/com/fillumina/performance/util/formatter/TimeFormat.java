package com.fillumina.performance.util.formatter;

/**
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeFormat {
    private static final long SECOND = 1_000_000_000;
    private static final long MILLIS = 1_000_000;
    private static final long MICROS = 1_000;

    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final String[] Z = {
        "",
        "0",
        "00",
        "000",
        "0000",
        "00000",
        "000000",
        "0000000",
        "00000000",
        "000000000",
    };

    /**
     * Represents time with the watch form, i.e. 10:12:03.123
     */
    public static final TimeFormat WATCH =
            new TimeFormat(true, false, ":", ":", "", ".");

    /**
     * Represents time with the textual form, i.e. 10h 12m 03.123s
     */
    public static final TimeFormat TEXT =
            new TimeFormat(true, true, "h ", "m ", "s", ".");

    public String second(long seconds) {
        return formatNanoseconds(seconds * SECOND, Precision.SECOND);
    }

    public String millis(long millis) {
        return formatNanoseconds(millis * MILLIS, Precision.MILLISECOND);
    }

    public String micros(long micros) {
        return formatNanoseconds(micros * MICROS, Precision.MICROSECOND);
    }

    public String nanos(long nanoseconds) {
        return formatNanoseconds(nanoseconds, Precision.NANOSECOND);
    }

    public static enum Precision {
        SECOND(0), MILLISECOND(3), MICROSECOND(6), NANOSECOND(9);

        private int decimal;

        private Precision(int decimal) {
            this.decimal = decimal;
        }

        public int getDecimal() {
            return decimal;
        }
    }

    private final boolean signPad;
    private final boolean removeUnusedUnit;
    private final String hourSymbol;
    private final String minSymbol;
    private final String secSymbol;
    private final String decSymbol;

    public TimeFormat(boolean signPad,
            boolean removeUnusedUnit,
            String hourSymbol,
            String minSymbol,
            String secSymbol,
            String decSymbol) {
        this.signPad = signPad;
        this.removeUnusedUnit = removeUnusedUnit;
        this.hourSymbol = hourSymbol;
        this.minSymbol = minSymbol;
        this.secSymbol = secSymbol;
        this.decSymbol = decSymbol;
    }

    public String formatNanoseconds(long ns, Precision precision) {
        return formatNanoseconds(ns, precision.getDecimal());
    }

    public String formatNanoseconds(long ns, int decimal) {
        StringBuilder buf = new StringBuilder(20);
        String sgn = signPad ? " " : "";

        if (ns < 0) {
            sgn = "-";
            ns = Math.abs(ns);
        }

        final long hour = ns / HOUR;
        final long minute = (ns % HOUR) / MINUTE;
        final long second = (ns % MINUTE) / SECOND;
        final long nanoseconds = (ns % SECOND) /
                (long)Math.pow(10, 9 - decimal);

        buf.append(sgn);
        if (!removeUnusedUnit || hour > 0) {
            append(buf, 1, hour);
            buf.append(hourSymbol);
        }
        if (!removeUnusedUnit || minute > 0) {
            append(buf, removeUnusedUnit ? 0 : 2, (minute));
            buf.append(minSymbol);
        }
        if (!removeUnusedUnit || second > 0) {
            append(buf, removeUnusedUnit ? 0 : 2, (second));
        }
        if ((!removeUnusedUnit || nanoseconds > 0) && decimal != 0) {
            buf.append(decSymbol);
            append(buf, decimal, nanoseconds);
        }
        if (!removeUnusedUnit || second > 0 || nanoseconds > 0) {
            buf.append(secSymbol);
        }
        return buf.toString();
    }

    private static void append(StringBuilder buf, int dgt, long ns) {
        if (ns == 0) {
            buf.append(Z[dgt]);
        } else {
            String s = Long.toString(ns);
            int n = dgt - s.length();
            if (n > 0) {
                buf.append(Z[n]);
            }
            buf.append(s);
        }
    }
}
