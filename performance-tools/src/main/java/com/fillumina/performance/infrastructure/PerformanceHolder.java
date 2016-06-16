package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiTest;
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
public class PerformanceHolder<T extends PerformanceHolder<T,A>, A>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final A performance;
    private final ComposedName name;
    private final StringGenerator<A> formatter;
    private boolean active = true;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    public static <T extends PerformanceHolder<T,A>, A extends AssertableMultiTest>
            PerformanceHolder<T,A> empty() {
        return new PerformanceHolder<>(null);
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

    protected boolean isActive() {
        return active;
    }

    /** There are no performance available. */
    public boolean isEmpty() {
        return performance == null;
    }

    /** *  Use this method to get the enclosed {@link PerformanceSample}. */
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
    @SuppressWarnings("unchecked")
    public T use(PerformanceConsumer<A> consumer) {
        if (active) {
            if (consumer != null) {
                consumer.consume(name, performance);
            }
        }
        return (T) this;
    }

    /**
     * Modifies the execution of
     * {@link #use(com.fillumina.performance.consumer.PerformanceConsumer) }
     * so that if {@code false} is passed here the {@code consumer} will
     * not be called.
     * <p>
     * This is very useful for
     * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     * fluent interfaces</a></i> allowing:
     * <code>lp.whenever(printout).use(StringTableStatsViewer.INSTANCE);</code>
     */
    @SuppressWarnings("unchecked")
    public T whenever(final boolean value) {
        this.active = value;
        return (T) this;
    }

    /**
     * Prints the statistics to standard input if the {@code condition} is
     * true.
     */
    @SuppressWarnings("unchecked")
    public T printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return (T) this;
    }

    /** Prints the statistics to standard output. */
    @SuppressWarnings("unchecked")
    public T print() {
        System.out.println(toString());
        return (T) this;
    }

    @Override
    public String toString() {
        if (formatter != null) {
            return formatter.toString(name, performance);
        }
        return performance.toString();
    }
}
