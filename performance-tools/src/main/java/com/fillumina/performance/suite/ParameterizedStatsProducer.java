package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedStatsProducer<P,A extends AssertableMultiStats>
        extends PerformanceProducer<A, ParameterizedTestable<P>>,
                Instrumentable<ParameterizedStatsProducer<P,A>> {

}
