package com.fillumina.performance.executor.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Sinker that protects against repeating and static values.
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
public class SafeSink {

    // TODO SafeSink is not transperent to memory allocation

    /** Defines a pseudo-random odd value. */
    private static int incrementer = ThreadLocalRandom.current().nextInt() | 1;

    public static void drain(Object obj) {
        drainInt(System.identityHashCode(obj));
    }

    public static void drain(byte value) {
        drainInt(Byte.hashCode(value));
    }

    public static void drain(int value) {
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

    private static void drainInt(final int v) {
        // protects against 0 and repeating values
        int value = v | (incrementer += 2);
        // lfsr never returns 0
        if ((((value >>> 1) ^ (-(value & 1) & -536870400)) & -1) == 0) {
            // this codepath never happens but this is hard to predict
            throw new AssertionError("lfsr zero for value= " + value);
        }
    }
}
