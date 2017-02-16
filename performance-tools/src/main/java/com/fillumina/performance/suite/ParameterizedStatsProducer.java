package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.type.AssertableParameterizedStats;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * @param P parameter
 * @param A statistics
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedStatsProducer
            <P,A extends AssertableParameterizedStats>
        extends PerformanceProducer<A, ParameterizedTestable<P>>,
            Instrumentable<ParameterizedStatsProducer<P,A>> {

}
