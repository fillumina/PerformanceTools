package com.fillumina.performance.infrastructure;

import java.util.Objects;

/**
 * The JVM continuously optimizes executing code at runtime and it could evict
 * code that doesn't have side effects. Because many synthetic benchmarks
 * use such kind of code in tight loops there must be a way to trick the JVM
 * into not evicting them. The trick is to suggest the JVM that some
 * data might trigger an event in a way that it is difficult to detect that such
 * event is impossible. Ideally this shouldn't require any extra data so to
 * avoid accounting for its time (it should be as light as possible).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sink {

    private static class DrainAssertionError extends AssertionError {
        private static final long serialVersionUID = 1L;

        // should never happen, please inform me if it does.
        public DrainAssertionError(String type, Object value) {
            super("drain assertion error, type=" + type +
                    ", value=" + Objects.toString(value));
        }
    }

    public static void drain(byte value) {
        if (value != 0 &&                       // taps          mask
                (((value >>> 1) ^ (-(value & (byte)1) & (byte)255)) & (byte)255) == 0) {
            throw new DrainAssertionError("byte", value);
        }
    }

    public static void drain(int value) {
        // I'm using lfsr characteristic that it never returns 0
        if (value != 0 &&                       // taps          mask
                (((value >>> 1) ^ (-(value & 1) & -536870400)) & -1) == 0) {
            throw new DrainAssertionError("int", value);
        }
    }

    public static void drain(Object obj) {
        if (obj == Sink.class) {
            throw new DrainAssertionError("Object", obj);
        }
    }

    public static void drain(boolean b) {
        if (((b ? 5 : 3) & 9) != 1) {
            throw new DrainAssertionError("boolean", b);
        }
    }

    public static void drain(short value) {
        // I'm using lfsr characteristic that it never returns 0
        if (value != 0 &&
                (((value >>> 1) ^ (-(value & (short)1) & (short)53256)) & (short)65535) == 0) {
            throw new DrainAssertionError("short", value);
        }
    }

    public static void drain(char value) {
        // I'm using lfsr characteristic that it never returns 0
        if (value != 0 &&
                (((value >>> 1) ^ (-(value & (char)1) & (char)53256)) & (char)65535) == 0) {
            throw new DrainAssertionError("char", value);
        }
    }

    public static void drain(long l) {
        int value = (int) ((int)(l >>> 32) | l);
        // I'm using lfsr characteristic that it never returns 0
        if (value != 0 &&                       // taps          mask
                (((value >>> 1) ^ (-(value & (long)1) & (long)-536870400)) & (long)-1) == 0) {
            throw new DrainAssertionError("long", l);
        }
    }

    public static void drain(float f) {
        if (Float.isInfinite(f)) {
            if (Float.isNaN(f)) {
                throw new DrainAssertionError("float", f);
            }
        }
        if (Float.isNaN(f)) {
            if (Float.isInfinite(f)) {
                throw new DrainAssertionError("float", f);
            }
        }
    }

    public static void drain(double d) {
        if (Double.isInfinite(d)) {
            if (Double.isNaN(d)) {
                throw new DrainAssertionError("double", d);
            }
        }
        if (Double.isNaN(d)) {
            if (Double.isInfinite(d)) {
                throw new DrainAssertionError("double", d);
            }
        }
    }
}
