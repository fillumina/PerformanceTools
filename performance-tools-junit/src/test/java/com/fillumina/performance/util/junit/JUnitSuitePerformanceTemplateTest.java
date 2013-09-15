package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedRunnable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JUnitSuitePerformanceTemplateTest
        extends JUnitSuitePerformanceTemplate<Integer> {
    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer SLEEP_1 = 10;
    private static final Integer SLEEP_2 = 20;
    private static final Integer SLEEP_3 = 30;

    private static final String TEST = "test";

    public static void main(final String[] args) {
        new JUnitSuitePerformanceTemplateTest().testWithOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        config.setBaseIterations(1)
                .setMaxStandardDeviation(10);
    }

    @Override
    public void addParameters(final ParametersContainer<?,Integer> parameters) {
        parameters
                .addParameter(NAME_1, SLEEP_1)
                .addParameter(NAME_2, SLEEP_2)
                .addParameter(NAME_3, SLEEP_3);
    }

    @Override
    public void addAssertions(final SuiteExecutionAssertion assertion) {
        assertion.forExecution(TEST)
                .assertPercentageFor(NAME_1).sameAs(33);

        assertion.forExecution(TEST)
                .assertPercentageFor(NAME_2).sameAs(66);

        assertion.forExecution(TEST)
                .assertPercentageFor(NAME_3).sameAs(100);

    }

    @Override
    public void executeTests(ParametrizedPerformanceSuite<Integer> suite) {
        suite.executeTest(TEST, new ParametrizedRunnable<Integer>() {

            @Override
            public void call(final Integer param) {
                try {
                    Thread.sleep(param);
                } catch (InterruptedException ex) {
                }
            }
        });
    }
}
