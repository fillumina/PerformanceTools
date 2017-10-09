package com.fillumina.performance.executor.generator;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.template.AlertPlayer;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedConfiguration extends AlertPlayer.Configuration {

    TName getTestName();

    TestConfiguration<?> getTestConfig();

    Map<Class<? extends Assertable>, SampleProducer<?,?>>
        getSampleProducers();

    Map<Class<? extends Assertable>, ProducerConfiguration<?>>
        getProducerConfigurations();

    MixedAssertionableResult.Builder getMixedStatsBuilder();

    boolean isThrowExceptionOnFailingAssertion();

    Appendable getOutput();

    Verbosity getVerbosity();

    TestListener getTestListener();
}
