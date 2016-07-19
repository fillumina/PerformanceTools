package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedSequenceStatsProducer
                        <P,S,A extends AssertableMultiStats>
        extends PerformanceProducer
                <A,
                 Map<ComposedName, Map<ComposedName, A>>,
                 ParametrizedSequenceTestable<P,S>>,
        Instrumentable<ParametrizedSequenceStatsProducer<P,S,A>> {

}
