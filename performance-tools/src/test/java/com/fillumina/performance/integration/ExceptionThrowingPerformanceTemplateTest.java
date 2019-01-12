package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.ThroughputUnit;
import org.junit.Test;


/**
 * Check for unsatisfied assertions and throws an {@link AssertionError}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionThrowingPerformanceTemplateTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new ExceptionThrowingPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test(expected = AssertionError.class)
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig().setFixedSamples(5);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("test", new Runnable() {

                @Param("one")
                private int param;

                private final Lfsr lfsr = new Lfsr();

                @Override
                public void run() {
                    for (int i=0; i<param; i++) {
                        Sink.drain(lfsr.next());
                    }
                }
            })
            .addParameter("one").value(1).end().build();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        // these assertions aren't satisfied (they refers to the same test)
        assertions.avgTime()
                .value("test", "one_1")
                .equalsTo(AverageTimeUnit.SECONDS.quantity(-1)).end();
        assertions.throughput()
                .forTest("test")
                .value("one_1")
                .equalsTo(ThroughputUnit.OP.quantity(-2)).end();
    }

}
