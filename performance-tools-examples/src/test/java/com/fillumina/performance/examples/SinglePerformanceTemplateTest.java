package com.fillumina.performance.examples;

import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.TimedRunnable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinglePerformanceTemplateTest
        extends PerformanceTemplate {


    public static void main(final String[] args) {
        new SinglePerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(MixedAssertion<?> assertions) {
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedTestOnly()
                .setSamples(5);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
//        tests.addTest("run", new LfsrTest());
        tests.addTest("test", new TimedRunnable(5));
    }
}
