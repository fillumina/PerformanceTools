package com.fillumina.performance.executor.test;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndVsLfsrComparisonApp {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertionBuilder<?> assertions) {
            }

            @Override
            public void config(MixedConfigurationBuilder<?> config) {
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
                tests.addTest("lfsr", new LfsrRunnable());
                tests.addTest("rnd", new RndRunnable());
            }
        }.executeWithFullOutput();
    }

}
