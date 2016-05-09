package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import java.io.Serializable;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a {@link PerformanceSample}
 * in place without having to use a variable or to enclose a long chain of
 * methods as a parameter. HINT: don't pass around this class but use
 * {@link PerformanceSample} instead.
 *
 * @author Francesco Illuminati
 */
public class PerformancesStatsHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PerformanceStats stats;
    private final String name;
    private boolean active = true;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    public static PerformancesStatsHolder empty() {
        return new PerformancesStatsHolder(PerformanceStats.EMPTY);
    }

    public PerformancesStatsHolder(final PerformanceStats stats) {
        this(null, stats);
    }

    public PerformancesStatsHolder(final String name,
            final PerformanceStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public boolean isEmpty() {
        return stats.getTestPerformances().isEmpty();
    }

    /** *  Use this method to get the enclosed {@link PerformanceSample}. */
    public PerformanceStats getPerformanceStats() {
        return stats;
    }

    /**
     * Pass the enclosed {@link PerformanceSample} directly to
     * the given {@link PerformanceSampleConsumer}.
     * @see #whenever(boolean)
     * @param consumers
     * @return {@code this}
     */
    public PerformancesStatsHolder use(final PerformanceStatsConsumer... consumers) {
        if (active) {
            for (PerformanceStatsConsumer consumer: consumers) {
                if (consumer != null) {
                    consumer.consume(name, stats);
                }
            }
        }
        return this;
    }

    /**
     * Modifies the execution of
     * {@link #use(com.fillumina.performance.consumer.PerformanceConsumer[]) }
     * so that if {@code false} is passed here the {@code consumer} will
     * not be called.
     * <p>
     * This is very useful for
     * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     * fluent interfaces</a></i> allowing:
     * <code>lp.whenever(printout).use(StringTableStatsViewer.INSTANCE);</code>
     */
    public PerformancesStatsHolder whenever(final boolean value) {
        this.active = value;
        return this;
    }

    /**
     * Prints the statistics to standard input if the {@code condition} is
     * true.
     */
    public PerformancesStatsHolder printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    /** Prints the statistics to standard output. */
    public PerformancesStatsHolder print() {
        System.out.println(toString());
        return this;
    }

    @Override
    public String toString() {
        return StringTableStatsViewer.getTable(name, stats).toString();
    }
}
