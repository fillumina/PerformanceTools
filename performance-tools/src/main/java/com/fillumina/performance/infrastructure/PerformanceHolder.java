package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a performance
 in place without having to use a variable or to enclose a long chain of
 methods as a parameter.
 *
 * @author Francesco Illuminati
 */
public class PerformanceHolder<A>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final PerformanceHolder<?> EMPTY =
            new PerformanceHolder<>(null);

    private final A performance;
    private final ComposedName name;
    private final StringGenerator<A> formatter;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <A> PerformanceHolder<A> empty() {
        return (PerformanceHolder<A>) EMPTY;
    }

    public PerformanceHolder(final A stats) {
        this(null, stats, null);
    }

    public PerformanceHolder(final ComposedName name,
            final A stats,
            final StringGenerator<A> formatter) {
        this.name = name;
        this.performance = stats;
        this.formatter = formatter;
    }

    public PerformanceHolder<A> createWithFormatter(
            final StringGenerator<A> formatter) {
        return new PerformanceHolder<>(name, performance, formatter);
    }

    /** There are no performance available. */
    public boolean isEmpty() {
        return performance == null;
    }

    /** *  Use this method to get the enclosed {@link SpeedSample}. */
    public A getPerformance() {
        return performance;
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @see #whenever(boolean)
     * @param consumers
     * @return {@code this}
     */
    public PerformanceHolder<A> use(PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            consumer.consume(name, performance);
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PerformanceHolder<A> check(Assertion<A> assertion) {
        if (assertion != null) {
            assertion.check(getPerformance());
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PerformanceHolder<A> checkAndPrintIf(boolean condition,
            Assertion<A> assertion) {
        if (assertion != null) {
            assertion.check(getPerformance());
            if (condition) {
                System.out.println("ASSERTION:\n" +
                        assertion.toString(getPerformance()));
            }
        }
        return this;
    }

    /**
     * Prints the statistics to standard input if the {@code condition} is
     * true.
     */
    public PerformanceHolder<A> printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    /** Prints the statistics to standard output. */
    public PerformanceHolder<A> print() {
        System.out.println(toString());
        return this;
    }

    @Override
    public String toString() {
        if (formatter != null) {
            return formatter.toString(name, performance);
        }
        return performance.toString();
    }
}
