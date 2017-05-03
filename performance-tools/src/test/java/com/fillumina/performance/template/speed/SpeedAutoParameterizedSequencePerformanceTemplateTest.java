package com.fillumina.performance.template.speed;

//import com.fillumina.performance.infrastructure.TestContainer;
//import com.fillumina.performance.suite.ParameterContainer;
//import com.fillumina.performance.suite.ParameterizedSequenceTestable;
//import com.fillumina.performance.suite.SequenceContainer;
//import com.fillumina.performance.template.ParameterizedSequenceMixedAssertion;
//import com.fillumina.performance.template.ParameterizedSequencePerformanceTemplate;
//import com.fillumina.performance.template.TestConfiguration;
//import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
//import com.fillumina.performance.util.stats.Ratio;
//import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedAutoParameterizedSequencePerformanceTemplateTest {
//        extends ParameterizedSequencePerformanceTemplate<Double, Integer>{
//
//    public static void main(final String[] args) {
//        new SpeedAutoParameterizedSequencePerformanceTemplateTest()
//                .executeWithFullOutput();
//    }
//
//    @Test
//    public void executeTest() {
//        executeWithoutOutput();
//    }
//
//    @Override
//    public void config(TestConfiguration configuration) {
//        configuration
//                .setName("AutoParameterizedSequencePerformanceTemplateTest")
//                .speedTestOnly()
//                    .setMaxPercentageMargin(7);
//    }
//
//    @Override
//    public void addParameters(ParameterContainer<Double> parameters) {
//        parameters
//                .addParameter("half", 0.5)
//                .addParameter("unit", 1.0);
//    }
//
//    @Override
//    public void addSequence(
//            SequenceContainer<Integer> sequences) {
//        sequences.setSequence(1, 2);
//    }
//
//    @Override
//    public void addTests(
//            TestContainer<ParameterizedSequenceTestable<Double, Integer>> tests) {
//        tests.addTest("test", new ParameterizedSequenceTestable<Double, Integer>() {
//
//            @Override
//            public Object test(Double param, Integer sequence) {
//                PerformanceTimeHelper.sleepMicroseconds(
//                        (int)(50 * param * sequence));
//                return null;
//            }
//        });
//    }
//
//    @Override
//    public void addAssertions(ParameterizedSequenceMixedAssertion assertion) {
//        assertion.speed()
//            .forAllSequences()
//                .forAllTests()
//                    .setTolerance(Ratio.percentage(5))
//                        .assertOrder("half").lessThan("unit");
//    }
}
