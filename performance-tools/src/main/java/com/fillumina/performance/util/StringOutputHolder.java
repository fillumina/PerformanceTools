package com.fillumina.performance.util;

import java.io.Serializable;

/**
 * Allows to print out to console without having to enclose
 * a long code into {@code System.out.print()}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringOutputHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String output;

    public StringOutputHolder(final String output) {
        this.output = output;
    }

    public void print() {
        System.out.println(output);
    }

    @Override
    public String toString() {
        return output;
    }
}
