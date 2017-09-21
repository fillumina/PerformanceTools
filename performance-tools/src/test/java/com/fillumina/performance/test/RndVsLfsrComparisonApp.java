package com.fillumina.performance.test;

import com.fillumina.performance.test.LfsrRunnable;
import com.fillumina.performance.test.RndRunnable;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndVsLfsrComparisonApp {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertion<?> assertions) {
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
