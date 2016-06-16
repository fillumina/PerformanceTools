package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 * A {@link PerformanceConsumer} that prints out ({@link System.out})
 * performances using the specified {@link StringGenerator}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceViewer<A> implements PerformanceConsumer<A> {

    private final StringGenerator<A> formatter;

    /**
     * @param formatter used to format the performance to print out.
     */
    public PerformanceViewer(StringGenerator<A> formatter) {
        this.formatter = formatter;
    }

    /** Prints out the named performance. */
    @Override
    public void consume(ComposedName testName, A sample) {
        System.out.println(formatter.toString(testName, sample));
    }
}
