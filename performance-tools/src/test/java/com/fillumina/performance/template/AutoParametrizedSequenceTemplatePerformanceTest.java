package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.AutoParametrizedSequenceTemplatePerformanceTest.Creator;
import com.fillumina.performance.util.interval.IntegerInterval;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedSequenceTemplatePerformanceTest
        extends AutoParametrizedSequencePerformanceTemplate<Creator, Integer> {
    private static final String TEST = "test";

    public interface Creator {
        Object create(int size);
    }

    public static void main(final String[] args) {
        new AutoParametrizedSequenceTemplatePerformanceTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new AutoParametrizedSequenceTemplatePerformanceTest()
                .executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
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
        sequence.setSequence(IntegerInterval.from(1).to(2).step(1));
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<Creator, Integer>> tests) {
        tests.addTest(TEST, new ParametrizedSequenceTestable<Creator, Integer>() {
            @Override
            public Object test(Creator creator, Integer size) {
                return creator.create(size);
            }
        });
    }

    @Override
    public void addAssertions(ParametrizedSequenceAssertion assertions) {
        assertions.speed()
                .forSequenceValue("1")
                    .forTest(TEST, AssertSpeed.withTolerance(5)
                            .assertOrder("byte").lessThan("double"));

        assertions.usedMem()
                .forSequenceValue("1")
                    .forTest(TEST, AssertMemory.withTolerance(5)
                            .assertOrder("byte").lessThan("double")
                            .assertValue("byte").sameAs(120))
                    .endTests()
                .forSequenceValue("2")
                    .forTest(TEST, AssertMemory.withTolerance(5)
                            .assertOrder("byte").lessThan("double"));

        assertions.allocatedMem()
                .forAllSequences()
                    .forAllTests(AssertMemory.withTolerance(5)
                            .assertValue("byte").sameAs(0)
                            .assertValue("double").sameAs(0));
    }
}
