package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedSequenceStatsProducer<P,S>
        extends PerformanceProducer
                <Map<ComposedName, Map<ComposedName, PerformanceStats>>,
                ParametrizedSequenceTestable<P,S>>,
        Instrumentable<ParametrizedSequenceStatsProducer<P,S>> {

}
