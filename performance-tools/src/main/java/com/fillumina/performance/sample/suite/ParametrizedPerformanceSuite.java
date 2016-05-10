package com.fillumina.performance.sample.suite;

import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.PerformanceStats;
import java.util.Map;

/**
 * Instrumenter that allows to execute a parametrized test.
 * If a test has been already added to the
 * {@link com.fillumina.performance.sample.PerformanceTimer}
 * it will be executed alongside the parametrized
 * one defined by this class.
 * Applying this class to the right instrumenter allows to execute the tests
 * in a single-threaded or multi-threaded environment
 * (see {@link com.fillumina.performance.PerformanceTimerFactory}).
 *
 * @param P type of the test parameter
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuite<P>
        extends AbstractParametrizedInstrumenterSuite
            <ParametrizedPerformanceSuite<P>, ParametrizedTestable<P>, P> {

    @Override
    protected void createTests() {
        if (!getTests().isEmpty()) {
            final DefaultPerformanceTimer performanceTimer =
                    getPerformanceTimer();
            for (Map.Entry<String, ParametrizedTestable<P> > test :
                    getTests().entrySet()) {
                String testName = test.getKey();
                if (!PerformanceStats.BASELINE_TEST_NAME.equals(testName)) {
                    ParametrizedTestable<P> testable = test.getValue();
                    for (Map.Entry<String, P> param : getParams().entrySet()) {
                        String paramName = param.getKey();
                        P parameter = param.getValue();
                        performanceTimer.addTest(testName + "_" + paramName,
                                new ParametrizedTestableImpl<>(testable, parameter));
                    }
                }
            }
            getTests().clear();
            getParams().clear();
        }
    }

    private static class ParametrizedTestableImpl<P> implements Testable {
        private final ParametrizedTestable<P> test;
        private final P param;

        public ParametrizedTestableImpl(
                ParametrizedTestable<P> parametrizedTest, P param) {
            this.test = parametrizedTest;
            this.param = param;
        }

        @Override
        public void setUp() {
            test.setUp(param);
        }

        @Override
        public void onBeforeSample(int iterations) {
            test.onBeforeSample(param, iterations);
        }

        @Override
        public Object test() {
            return test.test(param);
        }
    }
}
