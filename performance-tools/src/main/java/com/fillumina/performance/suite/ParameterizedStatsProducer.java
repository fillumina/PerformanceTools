package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 * @param P parameter
 * @param A statistics
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedStatsProducer
            <P,A extends Assertable>
        extends PerformanceProducer<PHolder<A>,
                                    ParameterizedTestable<P>>,
                Instrumentable<ParameterizedStatsProducer<P,A>> {

}
