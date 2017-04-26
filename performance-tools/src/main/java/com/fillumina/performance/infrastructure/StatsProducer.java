package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * Some measures might be repeated or averaged or some other data might be
 * extracted from them. Instrumentation is just a way an producer can be
 * used by another one to provide higher order results.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProducer<A extends Assertable>
        extends PerformanceProducer<A, Runnable>,
                Instrumentable<StatsProducer<A>> {

}
