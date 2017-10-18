package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.util.StringGenerator;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedProducerConfiguration extends ProducerConfiguration {

    <A extends Assertable> Map<Class<A>,StringGenerator<A>> getStringGenerators();

    void setVerbosity(Verbosity verbostiy);
}
