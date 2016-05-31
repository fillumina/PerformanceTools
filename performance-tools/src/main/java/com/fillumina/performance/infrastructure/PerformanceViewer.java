package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceViewer<A> implements PerformanceConsumer<A> {

    private final PerformanceFormatter<A> formatter;

    public PerformanceViewer(PerformanceFormatter<A> formatter) {
        this.formatter = formatter;
    }

    @Override
    public void consume(ComposedName testName, A sample) {
        System.out.println(formatter.toString(testName, sample));
    }
}
