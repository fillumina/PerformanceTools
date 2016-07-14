package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;
import com.fillumina.performance.assertion.AssertableMultiStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedSequenceStatsProducer<P,S,A extends AssertableMultiStats>
        extends PerformanceProducer
                <Map<ComposedName, Map<ComposedName, A>>,
                ParametrizedSequenceTestable<P,S>>,
        Instrumentable<ParametrizedSequenceStatsProducer<P,S,A>> {

}
