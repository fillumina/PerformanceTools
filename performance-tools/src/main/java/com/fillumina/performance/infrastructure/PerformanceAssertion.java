package com.fillumina.performance.infrastructure;

/**
 * A {@link PerformanceConsumer} that checks if the statistics comply to the
 * requirements. Useful to test performance requirements.
 * It implements {@link PerformanceFormatter} so that requirements can be
 * printed out nicely if needed but assertions don't really need that and
 * can only implements {@link PerformanceConsumer} if they choose to
 * (they are not used differently than any other consumers by executors).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceAssertion<A>
        extends PerformanceConsumer<A>, PerformanceFormatter<A> {

    /**
     * It checks the given performance against its assertions.
     * It delegates to
     * {@link PerformanceSampleConsumer#consume(java.lang.String, com.fillumina.performance.producer.LoopPerformances) }.
     * @throws AssertionError if the statistics are not as required.
     */
    void check(A performance);
}
