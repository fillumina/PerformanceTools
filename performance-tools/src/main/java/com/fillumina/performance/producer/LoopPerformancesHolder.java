package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.io.Serializable;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a {@link LoopPerformances}
 * in place without having to use a variable or to enclose a long chain of
 * methods as a parameter. HINT: don't pass around this class but use
 * {@link LoopPerformances} instead.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LoopPerformancesHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LoopPerformances loopPerformances;
    private final String name;
    private boolean active = true;

    public LoopPerformancesHolder(final LoopPerformances loopPerformances) {
        this(null, loopPerformances);
    }

    public LoopPerformancesHolder(final String name,
            final LoopPerformances loopPerformances) {
        this.name = name;
        this.loopPerformances = loopPerformances;
    }

    /** Use this method to get the enclosed {@link LoopPerformances}. */
    public LoopPerformances getLoopPerformances() {
        return loopPerformances;
    }

    /**
     * Pass the enclosed {@link LoopPerformances} directly to
     * the given {@link PerformanceConsumer}.
     * @see #whenever(boolean)
     * @param consumer
     * @return {@code this}
     */
    public LoopPerformancesHolder use(final PerformanceConsumer consumer) {
        if (active) {
            consumer.consume(name, loopPerformances);
        }
        return this;
    }

    /**
     * Modifies the execution of
     * {@link #use(com.fillumina.performance.consumer.PerformanceConsumer) }
     * so that if {@code false} is passed here the {@code consumer} will
     * not be called in {@code use(consumer)}.
     * <p>
     * This is very useful for
     * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     * fluent interfaces</a></i> allowing stuff like:<br />
     * <code>lp.whenever(printout).use(StringTableViewer.INSTANCE);</code>
     */
    public LoopPerformancesHolder whenever(final boolean value) {
        this.active = value;
        return this;
    }

    /** Prints the statistics to standard output. */
    public void println() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return StringTableViewer.INSTANCE
                .getTable(name, loopPerformances).toString();
    }
}
