package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemParametrizedSequenceTemplateTest.ArrayCreator;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.AutoParametrizedSequencePerformanceTemplate;
import com.fillumina.performance.template.ParametrizedSequenceAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.interval.IntegerInterval;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParametrizedSequenceTemplateTest
        extends AutoParametrizedSequencePerformanceTemplate
            <ArrayCreator,Integer> {

    interface ArrayCreator {
        Object create(int size);
    }

    public static void main(final String[] args) {
        new MemParametrizedSequenceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new MemParametrizedSequenceTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.usedMemTest();
    }

    @Override
    public void addParameters(ParameterContainer<ArrayCreator> parameters) {
        parameters
                .addParameter("byte", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new byte[size];
                    }
                })
                .addParameter("char", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new char[size];
                    }
                })
                .addParameter("short", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new short[size];
                    }
                })
                .addParameter("int", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new char[size];
                    }
                })
                .addParameter("float", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new float[size];
                    }
                })
                .addParameter("double", new ArrayCreator() {
                    @Override
                    public Object create(int size) {
                        return new double[size];
                    }
                });
    }

    @Override
    public void addSequence(SequenceContainer<Integer> sequences) {
        sequences.setSequence(IntegerInterval.from(0).to(10).step(5));
    }

    @Override
    public void addAssertions(ParametrizedSequenceAssertion assertion) {
        assertion.usedMem()
                .forAllSequences()
                    .forAllTests(AssertPerformance.<MemStats>withTolerance(10)
                            .assertOrder("byte").lessThan("double"));
    }

    @Override
    public void addTests(TestContainer
                <ParametrizedSequenceTestable<ArrayCreator, Integer>> tests) {
        tests.addTest("test",
                new ParametrizedSequenceTestable<ArrayCreator, Integer>() {
            @Override
            public Object test(ArrayCreator creator, Integer size) {
                return creator.create(size);
            }
        });
    }
}
