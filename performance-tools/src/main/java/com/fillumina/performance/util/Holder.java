package com.fillumina.performance.util;

import java.util.Objects;

/**
 * Used to pass values out of an inner class or in a scope where an external
 * final variable is needed to be accessed but its value must be mutable
 * (most notably in lambdas or anonymous classes).
 * They mimics AtomicXX classes without the unnecessary burden of
 * synchronization.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Holder<T> {

    public static class Double {
        private double value;
        public Double() {}
        public Double(double value) { this.value = value; }
        public double getValue() { return value; }
        public double get() { return value; }
        public void setValue(double value) { this.value = value; }
        public void add(double value) { this.value += value; }
        public void subtract(double value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Float {
        private float value;
        public Float() {}
        public Float(float value) { this.value = value; }
        public float getValue() { return value; }
        public double get() { return value; }
        public void setValue(float value) { this.value = value; }
        public void add(float value) { this.value += value; }
        public void subtract(float value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Integer {
        private int value;
        public Integer() {}
        public Integer(int value) { this.value = value; }
        public int getValue() { return value; }
        public int get() { return value; }
        public int getAndIncrement() { return value++; }
        public int getAndDecrement() { return value--; }
        public int incrementAndGet() { return ++value; }
        public int decrementAndGet() { return --value; }
        public void setValue(int value) { this.value = value; }
        public void add(int value) { this.value += value; }
        public void subtract(int value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Long {
        private long value;
        public Long() {}
        public Long(long value) { this.value = value; }
        public long getValue() { return value; }
        public long get() { return value; }
        public long getAndIncrement() { return value++; }
        public long getAndDecrement() { return value--; }
        public long incrementAndGet() { return ++value; }
        public long decrementAndGet() { return --value; }
        public void setValue(long value) { this.value = value; }
        public void add(long value) { this.value += value; }
        public void subtract(long value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Short {
        private short value;
        public Short() {}
        public Short(short value) { this.value = value; }
        public short getValue() { return value; }
        public short get() { return value; }
        public long getAndIncrement() { return value++; }
        public long getAndDecrement() { return value--; }
        public long incrementAndGet() { return ++value; }
        public long decrementAndGet() { return --value; }
        public void setValue(short value) { this.value = value; }
        public void add(short value) { this.value += value; }
        public void subtract(short value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Char {
        private char value;
        public Char() {}
        public Char(char value) { this.value = value; }
        public char getValue() { return value; }
        public char get() { return value; }
        public void setValue(char value) { this.value = value; }
        public char getAndIncrement() { return value++; }
        public char getAndDecrement() { return value--; }
        public char incrementAndGet() { return ++value; }
        public char decrementAndGet() { return --value; }
        public void add(char value) { this.value += value; }
        public void subtract(char value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Byte {
        private byte value;
        public Byte() {}
        public Byte(byte value) { this.value = value; }
        public byte getValue() { return value; }
        public void setValue(byte value) { this.value = value; }
        public byte get() { return value; }
        public byte getAndIncrement() { return value++; }
        public byte getAndDecrement() { return value--; }
        public byte incrementAndGet() { return ++value; }
        public byte decrementAndGet() { return --value; }
        public void add(byte value) { this.value += value; }
        public void subtract(byte value) { this.value -= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    public static class Boolean {
        private boolean value;
        public Boolean() {}
        public Boolean(boolean value) { this.value = value; }
        public boolean get() { return value; }
        public boolean getValue() { return value; }
        public void setValue(boolean value) { this.value = value; }
        public void or(boolean value) { this.value |= value; }
        public void and(boolean value) { this.value &= value; }
        @Override public String toString() { return Objects.toString(value); }
    }

    private T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }

    public boolean isNull() {
        return value == null;
    }

    public T getValue() {
        return value;
    }

    public T get() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
