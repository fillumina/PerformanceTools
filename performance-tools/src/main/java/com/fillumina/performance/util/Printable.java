package com.fillumina.performance.util;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Extends what it is usually done by the {@link Object#toString() } method.
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

    /** use with lambda, i.e. {@code printTo(System.out::println)} */
    @SuppressWarnings("unchecked")
    public I printTo(Consumer<String> consumer) {
        appendTo(new Appendable() {
            @Override
            public Appendable append(CharSequence csq) throws IOException {
                consumer.accept(csq.toString());
                return this;
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end)
                    throws IOException {
                consumer.accept(csq.subSequence(start, end).toString());
                return this;
            }

            @Override
            public Appendable append(char c) throws IOException {
                consumer.accept(Character.toString(c));
                return this;
            }
        });
        return (I) this;
    }
}
