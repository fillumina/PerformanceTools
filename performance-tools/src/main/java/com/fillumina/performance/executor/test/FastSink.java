package com.fillumina.performance.executor.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Sinker that does not protect against repeating values but it's about
 * twice as faster than {@link SafeSink}. Be warned that a series of
 * repeating values might be optimized out by
 * <a href='https://en.wikipedia.org/wiki/Memoization'>memoization</a>.
 * <br>
 * The JVM continuously optimizes executing code at runtime and it could evict
 * code that doesn't have side effects. Because many synthetic benchmarks
 * use such kind of code in tight loops there must be a way to trick the JVM
 * into not evicting them. The trick is to instruct the JVM that some input
 * might trigger an event in a way that it is difficult to detect that such
 * event is impossible.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FastSink {

    public static void drain(Object obj) {
        drainInt(System.identityHashCode(obj));
    }

    public static void drain(byte value) {
        drainInt(Byte.hashCode(value));
    }

    public static void drain(int value) {
        // calls drainInt() to be as slow as the others
        drainInt(Integer.hashCode(value));
    }

    public static void drain(boolean b) {
        drainInt(Boolean.hashCode(b));
    }

    public static void drain(short value) {
        drainInt(Short.hashCode(value));
    }

    public static void drain(char value) {
        drainInt(Character.hashCode(value));
    }

    public static void drain(long l) {
        drainInt(Long.hashCode(l));
    }

    public static void drain(float f) {
        drainInt(Float.hashCode(f));
    }

    public static void drain(double d) {
        drainInt(Double.hashCode(d));
    }

    private static void drainInt(final int value) {
        if (impossible(value)) {
            throw new DrainAssertionError(value);
        }
    }

    private static boolean impossible(final int value) {
        // lfsr never returns 0, it's always false
        return lfsrNext(value | 1) == 0;
    }

    private static int lfsrNext(final int value) {
        return (((value >>> 1) ^ (-(value & 1) & -536870400)) & -1);
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static Object pass(Object obj) {
        if (impossible(System.identityHashCode(obj))) {
            return new Object();
        }
        return obj;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static byte pass(byte value) {
        if (impossible(Byte.hashCode(value))) {
            return (byte) ThreadLocalRandom.current().nextInt();
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static int pass(int value) {
        if (impossible(Integer.hashCode(value))) {
            return ThreadLocalRandom.current().nextInt();
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static boolean pass(boolean value) {
        if (impossible(Boolean.hashCode(value))) {
            return false;
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static short pass(short value) {
        if (impossible(Short.hashCode(value))) {
            return 0;
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static char pass(char value) {
        if (impossible(Character.hashCode(value))) {
            return (char) ThreadLocalRandom.current().nextInt();
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static long pass(long value) {
        if (impossible(Long.hashCode(value))) {
            return 0;
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static float pass(float value) {
        if (impossible(Float.hashCode(value))) {
            return 0;
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static double pass(double value) {
        if (impossible(Double.hashCode(value))) {
            return ThreadLocalRandom.current().nextDouble();
        }
        return value;
    }

}
