package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParameterizedPerformanceTemplateTest
        extends ParameterizedPerformanceTemplate<Integer> {
    private static final String FIRST = "first";
    private static final String SECOND = "second";

    public static void main(final String[] args) {
        new AutoParameterizedPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config
                .speedTestOnly()
                .usedMemTest()
                .allocatedMemTest();
    }

    @Override
    public void addParameters(ParameterContainer<Integer> params) {
        params
                .addParameter("1", 1)
                .addParameter("2", 2);
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest(FIRST, new ParameterizedTestable<Integer>() {
            private Lfsr lfsr = new Lfsr();

            @Override
            public Object test(Integer param) {
                final int[] array = new int[10 * param];
                for (int i=0; i<array.length; i++) {
                    array[i] = lfsr.next();
                }
                return array;
            }
        });
        tests.addTest(SECOND, new ParameterizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                return new int[5 * param];
            }
        });
    }

    @Override
    public void addAssertions(ParameterizedMixedAssertion assertion) {
        assertion
                .speed()
                    .forTest(FIRST)
                        .setTolerance(Ratio.percentage(5))
                        .assertOrder("1").lessThan("2")
                    .end()
                .endTests()
                .usedMem()
                    .forTest(SECOND)
                        .setTolerance(Ratio.percentage(5))
                        .assertValue("1").sameAs(16 + 5 * 4 + 4);
    }

}
