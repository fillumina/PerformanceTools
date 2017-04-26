package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RndTestableTest {

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestContainer<Runnable> tests) {
                tests.addTest("lfsr", new LfsrTestable());
                tests.addTest("xsp", new RndTestable());
            }
        }.executeWithFullOutput();
    }

}
