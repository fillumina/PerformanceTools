package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.annotation.Param;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.sequence.IntegerSequence;
import com.fillumina.performance.util.stats.Ratio;
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
                .speed().setSamples(5).end()
                .usedMem().end();
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
                Sink.drain(array);
            }
        })
        .addTest(SECOND, new Runnable() {
            @Param int param;

            @Override
            public void run() {
                Sink.drain(new int[5 * param]);
            }
        })

        .addParameter("param")
                .values(IntegerSequence.from(1).to(2).step(1));
    }


    @Override
    public void addAssertions(MixedAssertion<?> assertions) {
        assertions
                .tolerance(Ratio.percentage(5))
                .speed()
                    .assertOrder(FIRST, "1").lessThan(FIRST, "2")
                .end()
                .usedMemory()
                    .assertValue(SECOND, "1").sameAs(16 + 5 * 4 + 4);
    }

}
