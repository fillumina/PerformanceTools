package com.fillumina.performance.mem.stats;

//import com.fillumina.performance.infrastructure.TestContainer;
//import com.fillumina.performance.mem.MemParameterizedSequenceTemplateTest.ArrayCreator;
//import com.fillumina.performance.suite.ParameterContainer;
//import com.fillumina.performance.suite.ParameterizedSequenceTestable;
//import com.fillumina.performance.suite.SequenceContainer;
//import com.fillumina.performance.template.ParameterizedSequenceMixedAssertion;
//import com.fillumina.performance.template.ParameterizedSequencePerformanceTemplate;
//import com.fillumina.performance.template.TestConfiguration;
//import com.fillumina.performance.util.sequence.IntegerSequence;
//import com.fillumina.performance.util.stats.Ratio;
//import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParameterizedSequenceTemplateTest {
//        extends ParameterizedSequencePerformanceTemplate
//            <ArrayCreator,Integer> {
//
//    interface ArrayCreator {
//        Object create(int size);
//    }
//
//    public static void main(final String[] args) {
//        new MemParameterizedSequenceTemplateTest().executeWithFullOutput();
//    }
//
//    @Test
//    public void shouldExecuteTest() {
//        new MemParameterizedSequenceTemplateTest().executeWithoutOutput();
//    }
//
//    @Override
//    public void config(TestConfiguration configuration) {
//        configuration
//                .usedMemTestOnly().setSamples(11);
//
//    }
//
//    @Override
//    public void addParameters(ParameterContainer<ArrayCreator> parameters) {
//        parameters
//                .addParameter("byte", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new byte[size];
//                    }
//                })
//                .addParameter("char", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new char[size];
//                    }
//                })
//                .addParameter("short", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new short[size];
//                    }
//                })
//                .addParameter("int", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new char[size];
//                    }
//                })
//                .addParameter("float", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new float[size];
//                    }
//                })
//                .addParameter("double", new ArrayCreator() {
//                    @Override
//                    public Object create(int size) {
//                        return new double[size];
//                    }
//                });
//    }
//
//    @Override
//    public void addSequence(SequenceContainer<Integer> sequences) {
//        sequences.setSequence(IntegerSequence.from(0).to(10).step(5));
//    }
//
//    @Override
//    public void addAssertions(ParameterizedSequenceMixedAssertion assertion) {
//        assertion.usedMem()
//                .forAllSequences()
//                    .forAllTests()
//                        .setTolerance(Ratio.percentage(10))
//                            .assertOrder("byte").lessThan("double");
//    }
//
//    @Override
//    public void addTests(TestContainer
//                <ParameterizedSequenceTestable<ArrayCreator, Integer>> tests) {
//        tests.addTest("test",
//                new ParameterizedSequenceTestable<ArrayCreator, Integer>() {
//            @Override
//            public Object test(ArrayCreator creator, Integer size) {
//                return creator.create(size);
//            }
//        });
//    }
}
