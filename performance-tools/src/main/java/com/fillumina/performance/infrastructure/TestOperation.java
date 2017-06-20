package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestOperation {

    public enum Operation {
        ADD('+'), SUBTRACT('-');

        private char sign;

        Operation(char c) {
            this.sign = c;
        }

        public char getSign() {
            return sign;
        }
    }

    private final String a;
    private final String b;
    private final Operation op;

    public TestOperation(String a, String b, Operation op) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    public String getFirstTestName() {
        return a;
    }

    public String getSecondTestName() {
        return b;
    }

    public Operation getOperation() {
        return op;
    }

    @Override
    public String toString() {
        return "(" + a + " " + op.getSign() + " " + b + ")";
    }
}
