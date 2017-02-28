package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.AutoParameterizedSequenceTemplatePerformanceTest.Creator;
import com.fillumina.performance.util.interval.IntegerInterval;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParameterizedSequenceTemplatePerformanceTest
        extends ParameterizedSequencePerformanceTemplate<Creator, Integer> {
    private static final String TEST = "test";

    public interface Creator {
        Object create(int size);
    }

    public static void main(final String[] args) {
        new AutoParameterizedSequenceTemplatePerformanceTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.speedTest()
                .setMaxPercentageMargin(10);
    }

    @Override
    public void addParameters(ParameterContainer<Creator> parameters) {
        parameters.addParameter("byte", new Creator() {
            @Override
            public Object create(int size) {
                return new byte[100 * size];
            }
        });
        parameters.addParameter("double", new Creator() {
            @Override
            public Object create(int size) {
                return new double[100 * size];
            }
        });
    }

    @Override
    public void addSequence(SequenceContainer<Integer> sequence) {
        sequence.setSequence(IntegerInterval.from(1).to(3).step(1));
    }

    @Override
    public void addTests(
            TestContainer<ParameterizedSequenceTestable<Creator, Integer>> tests) {
        tests.addTest(TEST, new ParameterizedSequenceTestable<Creator, Integer>() {
            @Override
            public Object test(Creator creator, Integer size) {
                return creator.create(size);
            }
        });
    }

    @Override
    public void addAssertions(ParameterizedSequenceMixedAssertion assertions) {
        assertions
            .speed()
                .forSequenceValue("1")
                    .forTest(TEST)
                        .setTolerance(Ratio.percentage(5))
                            .assertOrder("byte").lessThan("double")
                        .end()
                    .endTests()
                .endSequences()
            .usedMem()
                .forSequenceValue("1")
                    .forTest(TEST)
                        .setTolerance(Ratio.percentage(5))
                            .assertOrder("byte").lessThan("double")
                            .assertValue("byte").sameAs(120)
                        .end()
                    .endTests()
                .forSequenceValue("2")
                    .forTest(TEST)
                        .setTolerance(Ratio.percentage(5))
                            .assertOrder("byte").lessThan("double")
                        .end()
                    .endTests()
                .endSequences()
            .allocatedMem()
                .forAllSequences()
                    .forAllTests()
                        .setTolerance(Ratio.percentage(5))
                            .assertValue("byte").sameAs(0)
                            .assertValue("double").sameAs(0);
    }
}
