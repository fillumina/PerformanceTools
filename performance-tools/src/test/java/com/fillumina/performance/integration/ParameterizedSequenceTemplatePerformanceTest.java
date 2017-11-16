package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.sequence.IntegerSequence;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceTemplatePerformanceTest
        extends PerformanceTemplate {

    private static final String TEST = "test";

    public interface Creator {
        Object create(int size);
    }

    public static void main(final String[] args) {
        new ParameterizedSequenceTemplatePerformanceTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(
            MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setMaxPercentageMargin(Ratio.percentage(10))
                .end();
    }

    @Override
    public void addTests(
            TestConfiguration<?> tests) {
        tests
            .addTest(TEST, new Runnable() {
                @Param private Creator creator;
                @Sequence private int size;
                @Override public void run() {
                    creator.create(size);
                }
            })
            .addParameter("creator")
                .value("byte", new Creator() {
                    @Override
                    public Object create(int size) {
                        return new byte[100 * size];
                    }
                })
                .value("double", new Creator() {
                    @Override
                    public Object create(int size) {
                        return new double[100 * size];
                    }
                })
                .end()
            .end()
        .addSequence("size").values(IntegerSequence.from(1).to(3).step(1));
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
//        assertions
//            .avgTime()
//                .forSequenceValue("1")
//                    .forTest(TEST)
//                        .setTolerance(Ratio.percentage(5))
//                            .assertOrder("byte").lessThan("double")
//                        .end()
//                    .endTests()
//                .endSequences()
//            .usedMem()
//                .forSequenceValue("1")
//                    .forTest(TEST)
//                        .setTolerance(Ratio.percentage(5))
//                            .assertOrder("byte").lessThan("double")
//                            .assertValue("byte").sameAs(120)
//                        .end()
//                    .endTests()
//                .forSequenceValue("2")
//                    .forTest(TEST)
//                        .setTolerance(Ratio.percentage(5))
//                            .assertOrder("byte").lessThan("double")
//                        .end()
//                    .endTests()
//                .endSequences()
//            .allocatedMem()
//                .forAllSequences()
//                    .forAllTests()
//                        .setTolerance(Ratio.percentage(5))
//                            .assertValue("byte").sameAs(0)
//                            .assertValue("double").sameAs(0);

//        assertions
//            .avgTime()
//                .forSequenceValue("1")
//                    .forTest(TEST)
//                        .setTolerance(Ratio.percentage(5))
//                            .assertOrder("byte").lessThan("double")
//                        .end()
//                    .endTests()
//                .endSequences()

        assertions
                .avgTime()
                    .forTest("size_1", TEST).order("byte").lessThan("double");
    }
}
