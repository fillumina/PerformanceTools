package com.fillumina.performance.template;

import com.fillumina.performance.sample.suite.AbstractParametrizedInstrumenterSuite;
import com.fillumina.performance.sample.suite.ParameterContainer;
import com.fillumina.performance.sample.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.sample.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.sample.suite.SequenceContainer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import java.util.Map;

/**
 * This template adds to each test a parameter and an item of a sequence.
 * <p>
 * The tests are created from the parameters (each new test will have a
 * different parameter and adopt the parameter's name) and there will be many
 * rounds each one named after the name of the test combined with the
 * string representation of the sequence item.
 * <p>
 * The performances returned are the average of the performances over all the
 * items of the sequence while intermediate performances are calculated on the
 * actual sequence item.
 * <p>
 * By this way it is possible to test different {@code Map}s (parameters)
 * with different sizes (sequence).
 * <p>
 * To create the name of the test use the static method
 * {@link #testName(String, Object) }.
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate<ParametrizedSequenceTestable<P,S>, P> {

    public AutoParametrizedSequencePerformanceTemplate() {
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
     * Adds a sequence to tests.
     * <pre>
     * sequences.setSequence('x', 'y', 'z');
     * </pre>
     */
    public abstract void addSequence(final SequenceContainer<?, S> sequences);

    /**
     * Defines the test to be executed. The test will be injected of
     * parameters (creating brand new tests taken the parameters' names)
     * and a sequence item (creating different series of tests).
     * <p>
     * It <b>could</b> be possible to
     * define more than one test but it would be complex to
     * match them with the right assertions (consumers). Use the
     * <i>fluent interface</i> approach if you need to do that: see
     * {@link com.fillumina.performance.PerformanceTimerFactory}.
     * Anyway each tests defined will act in a totally independent way.
     * <p>
     * It's protected so you don't have to export its output type.
     *
     * @return the test to be executed.
     */
    protected abstract ParametrizedSequenceTestable<P, S> getTest();

    /**
     * The assertions applies to each combination of test + sequence.
     */
    public abstract void addIntermediateAssertions(
            final PerformanceAssertion assertion);

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final Map<String, PerformanceStats> performanceMap) {}

    @Override
    protected AbstractParametrizedInstrumenterSuite<?,ParametrizedSequenceTestable<P,S>,P>
            getSuite() {
        return new ParametrizedSequencePerformanceSuite<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addOtherData(AbstractParametrizedInstrumenterSuite
            <?,ParametrizedSequenceTestable<P,S>,P> suite) {
        addSequence((SequenceContainer<?, S>) suite);
        addParameters(suite);
    }

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return ParametrizedSequencePerformanceSuite.createName(name, seqName);
    }
}
