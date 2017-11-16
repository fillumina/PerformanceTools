package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.sequence.IntegerSequence;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedPerformanceTemplateTest
        extends PerformanceTemplate {
    private static final String FIRST = "first";
    private static final String SECOND = "second";

    public static void main(final String[] args) {
        new ParameterizedPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config
                .speedConfig()
                    .setStatsTimeout(IntervalUnit.HOURS.quantity(1))
                    .setSamples(5)
                    .end()
                .usedMemConfig()
                    .end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(FIRST, new Runnable() {
            private final Lfsr lfsr = new Lfsr();

            @Param int param;

            @Override
            public void run() {
                final int[] array = new int[10 * param];
                for (int i=0; i<array.length; i++) {
                    array[i] = lfsr.next();
                }
                SafeSink.drain(array);
            }
        })
        .addTest(SECOND, new Runnable() {
            @Param int param;

            @Override
            public void run() {
                SafeSink.drain(new int[5 * param]);
            }
        })

        .addParameter("param")
                .values(IntegerSequence.from(1).to(2).step(1));
    }


    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions
                .tolerance(Ratio.percentage(5))
                .avgTime()
                    .forTest(FIRST).order("param_1").greaterThan("param_2")
                .end()
                .usedMemory().value(SECOND, "param_1").equalsTo(16 + 5 * 4 + 4);
    }

}
