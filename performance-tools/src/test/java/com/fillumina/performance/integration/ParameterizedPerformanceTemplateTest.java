package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.sequence.IntegerSequence;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 * Compares the speed and memory usage of two tests using also a sequence
 * to test different loads.
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
                    .setSamples(5)
                .end()
                .usedMemConfig()
                    .setSamples(1)
                .end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest(FIRST, new Runnable() {
            private final Lfsr lfsr = new Lfsr();

            @Param
            private int param;

            @Override
            public void run() {
                final int[] array = new int[10 * param];
                for (int i=0; i<array.length; i++) {
                    array[i] = lfsr.next();
                }
                Sink.drain(array);
            }
        })
        .addTest(SECOND, new Runnable() {
            @Param
            private int param;

            @Override
            public void run() {
                Sink.drain(new int[5 * param]);
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
                    .forTest(FIRST).order("param_1").lessThan("param_2")
                .end()
                .usedMemory()
                    .value(SECOND, "param_1").equalsTo(16 + 5 * 4 + 4)
                .end();
    }

}
