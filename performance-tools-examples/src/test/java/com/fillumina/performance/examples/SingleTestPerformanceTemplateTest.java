package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.TimedRunnable;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SingleTestPerformanceTemplateTest
        extends PerformanceTemplate {


    public static void main(final String[] args) {
        new SingleTestPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setSamples(5);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("test", new TimedRunnable(5));
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        // TODO what unit does it use??? fix that
        assertions.avgTime()
                .value("test")
                .lessThan(AverageTimeUnit.MILLISECONDS.quantity(8))
            .end();
    }

}
