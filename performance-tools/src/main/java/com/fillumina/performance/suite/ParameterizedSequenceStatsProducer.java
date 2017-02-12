package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedSequenceStatsProducer
                        <P,S,A extends AssertableMultiStats>
        extends PerformanceProducer
                <A,
                 ParameterizedSequenceTestable<P,S>>,
        Instrumentable<ParameterizedSequenceStatsProducer<P,S,A>> {
}
