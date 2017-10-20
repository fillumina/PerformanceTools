package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.PerformanceGenerator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MixedConfiguration extends PerformanceGenerator.Configuration {

    PerformanceBuilderListener getPerformanceBuilderListener();

    MixedAssertionableResult.Builder getMixedAssertionableResultBuilder();
}
