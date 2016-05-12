package com.fillumina.performance.suite;

import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedStatsProducer<P>
        extends PerformanceProducer
                <Map<String, PerformanceStats>, ParametrizedTestable<P>>,
        Instrumentable<ParametrizedStatsProducer<P>> {

}
