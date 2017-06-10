package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndTestableTest {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertion assertions) {
            }

            @Override
            public void config(MixedConfigurationBuilder config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
                tests.addTest("lfsr", new LfsrRunnable());
                tests.addTest("xsp", new RndRunnable());
            }
        }.executeWithFullOutput();
    }

}
