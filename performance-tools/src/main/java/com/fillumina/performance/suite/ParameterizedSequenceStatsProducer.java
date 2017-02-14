package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.type.AssertableParameterizedSequenceStats;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * @param P parameter
 * @param S sequence
 * @param A statistics
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedSequenceStatsProducer
                        <P,S,A extends AssertableParameterizedSequenceStats & Assertable>
        extends PerformanceProducer<A, ParameterizedSequenceTestable<P,S>>,
                Instrumentable<ParameterizedSequenceStatsProducer<P,S,?>> {
}
