package com.fillumina.performance.suite;

import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedStatsProducer<P,A extends AssertableMultiTest>
        extends PerformanceProducer<Map<ComposedName, A>, ParametrizedTestable<P>>,
                Instrumentable<ParametrizedStatsProducer<P,A>> {

}
