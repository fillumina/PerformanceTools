package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.Configuration;

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
            public void config(Configuration config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestContainer<Runnable> tests) {
                tests.addTest("lfsr", new LfsrRunnable());
                tests.addTest("xsp", new RndRunnable());
            }
        }.executeWithFullOutput();
    }

}
