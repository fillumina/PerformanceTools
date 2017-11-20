package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.StringGenerator;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedProducerConfiguration extends ProducerConfiguration {

    Map<Stats.Type,StringGenerator<Stats>> getStringGenerators();

    void setVerbosity(Verbosity verbostiy);
}
