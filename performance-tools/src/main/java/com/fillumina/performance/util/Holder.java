package com.fillumina.performance.util;

/**
 * Used to pass values out of an inner class.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Holder<T> {

    public static class Double {
        private double value;
        public Double() {}
        public Double(double value) { this.value = value; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }

    public static class Float {
        private float value;
        public Float() {}
        public Float(float value) { this.value = value; }
        public float getValue() { return value; }
        public void setValue(float value) { this.value = value; }
    }

    public static class Integer {
        private int value;
        public Integer() {}
        public Integer(int value) { this.value = value; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    public static class Short {
        private short value;
        public Short() {}
        public Short(short value) { this.value = value; }
        public short getValue() { return value; }
        public void setValue(short value) { this.value = value; }
    }

    public static class Char {
        private char value;
        public Char() {}
        public Char(char value) { this.value = value; }
        public char getValue() { return value; }
        public void setValue(char value) { this.value = value; }
    }

    public static class Byte {
        private byte value;
        public Byte() {}
        public Byte(byte value) { this.value = value; }
        public byte getValue() { return value; }
        public void setValue(byte value) { this.value = value; }
    }

    public static class Boolean {
        private boolean value;
        public Boolean() {}
        public Boolean(boolean value) { this.value = value; }
        public boolean getValue() { return value; }
        public void setValue(boolean value) { this.value = value; }
    }

    private T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
