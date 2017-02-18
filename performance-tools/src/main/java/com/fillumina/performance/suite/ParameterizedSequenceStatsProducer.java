package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * @param P parameter
 * @param S sequence
 * @param A statistics
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedSequenceStatsProducer<P,S, A extends Assertable>
        extends PerformanceProducer<PHolder<PHolder<A>>,
                                    ParameterizedSequenceTestable<P,S>>,
                Instrumentable<ParameterizedSequenceStatsProducer<P,S,A>> {
}
