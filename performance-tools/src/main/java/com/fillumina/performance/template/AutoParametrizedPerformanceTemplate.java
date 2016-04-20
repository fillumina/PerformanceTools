package com.fillumina.performance.template;

import com.fillumina.performance.sample.suite.AbstractParametrizedInstrumenterSuite;
import com.fillumina.performance.sample.suite.ParameterContainer;
import com.fillumina.performance.sample.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.sample.suite.ParametrizedTestable;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import java.util.Map;

/**
 * It works just like the {@link AutoProgressionPerformanceTemplate} but it
 * allows to add a parameter to each test. This means that you can run
 * the same code against different objects and so automatically
 * creating different tests. (i.e. you can test the relative speed of different
 * type of {@code List}s by using the same test code and passing different
 * type of list to it).
 * <p>
 * Differently from {@link AutoProgressionPerformanceTemplate} each
 * test is executed on the spot (where it is created) and the results
 * returned so there isn't a global {@code execute()} code for all tests.
 * To discriminate between different tests each has a name that can
 * be used in {@code assertion.forExecution(TEST_NAME)} and each parameter
 * is named too:
 * <pre>
 * assertion.forExecution(<b>TEST_NAME</b>).
 *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
 * </pre>
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate<ParametrizedTestable<P>, P> {

    private final AssertionSuiteBuilder assertionBuilder =
            new AssertionSuiteBuilder();

    public AutoParametrizedPerformanceTemplate() {
        super();
    }

    /**
     * Adds named parameters to tests.
     * <pre>
     * parameters
     *       .addParameter(NAME_1, VALUE_1)
     *       .addParameter(NAME_2, VALUE_2)
     *       .addParameter(NAME_3, VALUE_3);
     * </pre>
     * @param parameters
     */
    public abstract void addParameters(final ParameterContainer<P> parameters);

    /**
     * To discriminate between different tests use test's and parameter's names:
     * <pre>
     * assertion.forExecution(<b>TEST_NAME</b>).
     *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
     * </pre>
     */
    public abstract void addAssertions(final AssertionSuiteBuilder assertion);

    /** Called at the end of the execution, use for assertions or printouts. */
    public void onAfterExecution(
            final Map<String, PerformanceStats> performanceMap) {}

    @Override
    protected AbstractParametrizedInstrumenterSuite<?, ParametrizedTestable<P>, P>
            getSuite() {
        return new ParametrizedPerformanceSuite<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addOtherData(
            AbstractParametrizedInstrumenterSuite<?, ParametrizedTestable<P>, P> suite) {
        addParameters(suite);
        addAssertions(assertionBuilder);
    }

    @Override
    protected PerformanceStatsConsumer getAssertions() {
        return assertionBuilder.getAssertPerformanceForExecutionSuite();
    }

}
