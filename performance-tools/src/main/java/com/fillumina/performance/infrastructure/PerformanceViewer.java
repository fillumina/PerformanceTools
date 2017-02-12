package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiStats;
import java.io.IOException;

/**
 * A {@link PerformanceConsumer} that prints out
 * performances using the specified {@link StringGenerator}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceViewer<A extends AssertableMultiStats>
        implements PerformanceConsumer<A> {

    private final StringGenerator<A> formatter;
    private final Appendable appendable;

    /**
     * @param formatter used to format the performance to print out.
     */
    public PerformanceViewer(StringGenerator<A> formatter) {
        this(formatter, System.out);
    }

    /**
     * @param appendable to append string to
     * @param formatter used to format the performance to print out.
     */
    public PerformanceViewer(StringGenerator<A> formatter,
            Appendable appendable) {
        this.appendable = appendable;
        this.formatter = formatter;
    }

    /** Prints out the named performance. */
    @Override
    public void consume(PerformanceHolder<A> sample) {
        if (appendable != null) {
            try {
                appendable
                        .append(formatter.toString(sample))
                        .append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
