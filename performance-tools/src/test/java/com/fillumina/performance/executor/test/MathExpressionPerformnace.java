package com.fillumina.performance.executor.test;

import com.fillumina.performance.executor.generator.TestConfiguration;
import static com.fillumina.performance.executor.test.SafeSink.pass;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MathExpressionPerformnace {

    public static void main(final String[] args) {
        final double result = Math.hypot(5, 1.2);
        final double x = 5;
        final double y = 1.2;

        new PerformanceTemplate() {
            @Override
            public void addAssertions(MixedAssertionBuilder<?> assertions) {
                assertions.avgTime()
                        .order("pass").lessThan("normal");
            }

            @Override
            public void config(MixedConfigurationBuilder<?> config) {
                config.speedConfig();
            }

            @Override
            public void addTests(TestConfiguration<?> tests) {
                tests.addTest("pass", () -> {
                    double x = pass(5);
                    double y = pass(1.2);
                    FastSink.drain(Math.hypot(x, y));
                });
                tests.addTest("normal", () -> {
                    FastSink.drain(Math.hypot(x, y));
                });
                tests.addTest("static", () -> {
                    SafeSink.drain(result);
                });
            }
        }.executeWithFullOutput();

    }
}
