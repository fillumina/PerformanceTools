package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.annotation.Param;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.sequence.IntegerSequence;
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
    public void config(Configuration<PerformanceTemplate> config) {
        config
                .speedTestOnly().end()
                .usedMemTest().end()
                .allocatedMemTest();
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
                .values(IntegerSequence.from(1).to(3).step(1));
    }


    @Override
    public void addAssertions(MixedAssertion assertions) {
//        assertions
//                .speed()
//                    .forTest(FIRST)
//                        .setTolerance(Ratio.percentage(5))
//                        .assertOrder("1").lessThan("2")
//                    .end()
//                .endTests()
//                .usedMem()
//                    .forTest(SECOND)
//                        .setTolerance(Ratio.percentage(5))
//                        .assertValue("1").sameAs(16 + 5 * 4 + 4);
    }

}
