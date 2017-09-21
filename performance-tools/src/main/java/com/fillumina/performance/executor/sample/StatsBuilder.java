package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.filter.ListFilter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsBuilder<S extends Stats<?>,
                              A extends AbstractSample<A, ?, S>> {

    void addSample(A sample);

    /**
     * Builds a {@link TimeStats} out of the collected samples.
     *
     * @param message       The message to addSample to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    S createStats(ListFilter<Double> filter);

}
