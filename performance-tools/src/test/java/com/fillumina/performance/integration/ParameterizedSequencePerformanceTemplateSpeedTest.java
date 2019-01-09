package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.Sleeper;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 * Uses parameters and sequences to create a complex experiment and
 * checks it with an all matching assertion.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequencePerformanceTemplateSpeedTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new ParameterizedSequencePerformanceTemplateSpeedTest()
                .executeWithFullOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.setName(getClass().getSimpleName())
                .speedConfig()
                    .setSamples(5);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("test", new Runnable() {

            @Param
            private double param;

            @Sequence
            private int sequence;

            @Override
            public void run() {
                Sleeper.sleepMillis((int)(param * sequence));
            }
        })

        .sequences()
            .name("sequence")
                .values(1, 2)
            .end()
        .end()

        .parameters()
            .name("param")
                .value("half", 1.0)
                .value("unit", 2.0)
            .end();

    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime()
            .with().all().end()
                .tolerance(Ratio.percentage(5))
                    .order("half").lessThan("unit")
                .end();
    }
}
