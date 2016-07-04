package com.fillumina.performance.examples;

/**
 * Useful to force output on maven test too.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PrintOut {
    private final boolean printOut;

    public PrintOut() {
        this(false);
    }

    public PrintOut(boolean printOut) {
        this.printOut = printOut;
    }

    public boolean isPrintOut() {
        return printOut;
//        return true;
    }
}
