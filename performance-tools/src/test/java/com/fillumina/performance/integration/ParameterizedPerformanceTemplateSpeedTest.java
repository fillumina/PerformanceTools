package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.Sleeper;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 * Complex assertions on parametrized distinct tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedPerformanceTemplateSpeedTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new ParameterizedPerformanceTemplateSpeedTest()
                .executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.setName(getClass().getSimpleName())
                .speedConfig()
                    .setFixedSamples(5);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("single", new Runnable() {

            @Param
            private int sleepTime;

            @Override
            public void run() {
                Sleeper.sleepMillis(2 * sleepTime);
            }
        });

        tests.addTest("double", new Runnable() {

            @Param
            private int sleepTime;

            @Override
            public void run() {
                Sleeper.sleepMillis(sleepTime);
            }
        });

        tests.parameters()
                .name("sleepTime")
                    .value("one", 1)
                    .value("two", 2)
                    .value("three", 3)
                .end();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime()
                .with().all().end()
                    .tolerance(Ratio.percentage(10))
                    .order("one").lessThan("three")
                .forTest("single")
                    .tolerance(Ratio.percentage(10))
                    .percentage("three").equalsTo(Ratio.percentage(100))
                    .percentage("one").equalsTo(Ratio.percentage(33))
                .end();
    }
}
