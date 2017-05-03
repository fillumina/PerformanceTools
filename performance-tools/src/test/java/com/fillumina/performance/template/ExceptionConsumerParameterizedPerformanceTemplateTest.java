package com.fillumina.performance.template;

//import com.fillumina.performance.assertion.Assertable;
//import com.fillumina.performance.infrastructure.PHolder;
//import com.fillumina.performance.infrastructure.TestContainer;
//import com.fillumina.performance.suite.ParameterContainer;
//import com.fillumina.performance.suite.ParameterizedTestable;
//import com.fillumina.performance.util.rnd.Lfsr;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExceptionConsumerParameterizedPerformanceTemplateTest {
//        extends ParameterizedPerformanceTemplate<Integer> {
//
//    private boolean assertionErrorConsumerCaptured;
//
//    public static void main(final String[] args) {
//        new ExceptionConsumerParameterizedPerformanceTemplateTest()
//                .executeWithFullOutput();
//    }
//
//    @Test
//    public void shouldExecuteTest() {
//        executeWithoutOutput();
//        assertTrue(assertionErrorConsumerCaptured);
//    }
//
//    @Override
//    public void addParameters(ParameterContainer<Integer> params) {
//        params.addParameter("one", 1);
//    }
//
//    @Override
//    public void addAssertions(ParameterizedMixedAssertion assertion) {
//        // cannot possibly be
//        assertion.speed().forAllTests().assertValue("one").sameAs(-1);
//    }
//
//    @Override
//    public void config(TestConfiguration config) {
//        config.setTestListener(new TestListener() {
//            @Override
//            public <S extends Assertable, M extends Assertable> boolean notify(
//                        TestConfiguration config,
//                        MixedAssertion<?, ?> assertion,
//                        PHolder<S> speedStats,
//                        PHolder<M> usedMemStats,
//                        PHolder<M> allocatedMemStats,
//                        Throwable exception) {
//                    assertionErrorConsumerCaptured = true;
//                    return false;
//                }
//            }).speedTestOnly();
//    }
//
//    @Override
//    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
//        tests.addTest("test", new ParameterizedTestable<Integer>() {
//            private Lfsr lfsr = new Lfsr();
//            @Override
//            public Object test(Integer param) {
//                for (int i=0; i<param; i++) {
//                    lfsr.next();
//                }
//                return lfsr.next();
//            }
//        });
//    }

}
