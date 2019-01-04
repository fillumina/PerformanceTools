package com.fillumina.performance.executor.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Sinker that protects against JVM optimizations of repeating and static values.
 * <br>
 * It allows to iterate on invariant code like:
 * <ul>
 * <li>{@code Sink.drain(5);}
 * <li>{@code int x = 5; Sink.drain(x); }
 * </ul>
 * This features comes at a cost of some extra speed lost, but the time used
 * is constant.
 * <br>
 * The JVM continuously optimizes executing code at run-time and it tries hard
 * to evict code that doesn't have side effects. Because many synthetic benchmarks
 * use such kind of code in tight loops there must be a way to trick the JVM
 * into not evicting them. The trick is to tell the JVM that some input
 * might trigger an event in a way that it is difficult to detect that such
 * event is impossible, and that trick must change its internal state so that
 * JVM doesn't memoize it in case of recurring or static inputs.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sink {

    /** Defines a pseudo-random odd value. */
    private static int incrementer = ThreadLocalRandom.current().nextInt() | 1;

    /** Assures the value is not optimized out. */
    public static void drain(Object obj) {
        drainInt(System.identityHashCode(obj));
    }

    /** Assures the value is not optimized out. */
    public static void drain(byte value) {
        drainInt(Byte.hashCode(value));
    }

    /** Assures the value is not optimized out. */
    public static void drain(int value) {
        drainInt(Integer.hashCode(value));
    }

    /** Assures the value is not optimized out. */
    public static void drain(boolean b) {
        drainInt(Boolean.hashCode(b));
    }

    /** Assures the value is not optimized out. */
    public static void drain(short value) {
        drainInt(Short.hashCode(value));
    }

    /** Assures the value is not optimized out. */
    public static void drain(char value) {
        drainInt(Character.hashCode(value));
    }

    /** Assures the value is not optimized out. */
    public static void drain(long l) {
        drainInt(Long.hashCode(l));
    }

    /** Assures the value is not optimized out. */
    public static void drain(float f) {
        drainInt(Float.hashCode(f));
    }

    /** Assures the value is not optimized out. */
    public static void drain(double d) {
        drainInt(Double.hashCode(d));
    }

    // protects against 0 and repeating values
    private static void drainInt(final int value) {
        // makes the inner state change so that the method will not
        // be memoized out.
        if (impossible(value)) {
            // this codepath never happens but this is hard to predict
            throw new DrainAssertionError(value);
        }
    }

    private static boolean impossible(final int value) {
        // lfsr never returns 0, it's always false
        // it is incremented by 2 so that when it will overload it will not
        // be 0.
        return lfsrNext(value | (incrementer += 2)) == 0;
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
            return ThreadLocalRandom.current().nextBoolean();
        }
        return value;
    }

    /** @return the given value with the guarantee that it's not optimized out. */
    public static short pass(short value) {
        if (impossible(Short.hashCode(value))) {
            return (short) ThreadLocalRandom.current().nextInt();
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
            return ThreadLocalRandom.current().nextLong();
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
