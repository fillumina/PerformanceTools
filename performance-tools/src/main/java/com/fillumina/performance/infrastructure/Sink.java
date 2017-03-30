package com.fillumina.performance.infrastructure;

/**
 * The JVM optimizes its code at runtime and it could evict code that doesn't
 * have side effects. Because many synthetic benchmark tests use such
 * kind of code there must be a way to trick JAVA into not evicting them.
 * This class tries to do that.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO should be vastly improved see BlackHoles...
public class Sink {
    private static final Sink INSTANCE = new Sink();

    public static void drain(Object obj) {
        if (obj == INSTANCE) {
            throw new AssertionError();
        }
    }

    public static void drain(boolean b) {
        if (b == !b) {
            throw new AssertionError();
        }
    }

    public static void drain(byte b) {
        if (b == b + (byte)7) {
            throw new AssertionError();
        }
    }

    public static void drain(short s) {
        if (s == s + (short)7) {
            throw new AssertionError();
        }
    }

    public static void drain(char c) {
        if (c == c + '7') {
            throw new AssertionError();
        }
    }

    public static void drain(int i) {
        if (i == i + 7) {
            throw new AssertionError();
        }
    }

    public static void drain(long l) {
        if (l == l + 7L) {
            throw new AssertionError();
        }
    }

    public static void drain(float f) {
        if (Float.isInfinite(f)) {
            if (Float.isNaN(f)) {
                throw new AssertionError(f);
            }
        }
        if (Float.isNaN(f)) {
            if (Float.isInfinite(f)) {
                throw new AssertionError(f);
            }
        }
    }

    public static void drain(double d) {
        if (Double.isInfinite(d)) {
            if (Double.isNaN(d)) {
                throw new AssertionError(d);
            }
        }
        if (Double.isNaN(d)) {
            if (Double.isInfinite(d)) {
                throw new AssertionError(d);
            }
        }
    }
}
