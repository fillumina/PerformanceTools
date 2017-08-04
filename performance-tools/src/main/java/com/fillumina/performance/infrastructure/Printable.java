package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class Printable<I extends Printable<I>> {

    public abstract I appendTo(final Appendable appendable);

    /**
     * Prints the statistics to standard output if the {@code condition} is
     * true.
     */
    @SuppressWarnings("unchecked")
    public I printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return (I) this;
    }

    /**
     * Prints the statistics to standard output.
     */
    @SuppressWarnings("unchecked")
    public I print() {
        appendTo(System.out);
        return (I) this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        appendTo(buf);
        return buf.toString();
    }

    @SuppressWarnings("unchecked")
    public I appendToIf(boolean condition, final Appendable appendable) {
        if (condition) {
            appendTo(appendable);
        }
        return (I) this;
    }

}
