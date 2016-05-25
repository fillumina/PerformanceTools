package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.suite.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.suite.viewer.StringTableParametrizedSequenceStatsViewer;
import com.fillumina.performance.suite.viewer.StringTableParametrizedStatsViewer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StringHelper;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        extends AbstractPerformanceTemplate
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>,
             ParametrizedSequenceTestable<P,S>> {

    private PerformanceConsumer
                <Map<ComposedName, Map<ComposedName, PerformanceStats>>>
            paramSequencePerformanceConsumer =
            NullPerformanceConsumer.
                <Map<ComposedName, Map<ComposedName, PerformanceStats>>>instance();

    private PerformanceConsumer<Map<ComposedName, PerformanceStats>>
            parametrizedStatConsumer =
            NullPerformanceConsumer.<Map<ComposedName, PerformanceStats>>instance();

    public AutoParametrizedSequencePerformanceTemplate() {
        super();
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    @Override
    public void executeWithIntermediateOutput() {
        paramSequencePerformanceConsumer =
                StringTableParametrizedSequenceStatsViewer.INSTANCE;
        super.executeWithIntermediateOutput();
    }
    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    @Override
    public void executeWithFullOutput() {
        parametrizedStatConsumer = StringTableParametrizedStatsViewer.INSTANCE;
        paramSequencePerformanceConsumer =
                StringTableParametrizedSequenceStatsViewer.INSTANCE;
        super.executeWithFullOutput();
    }

    @Override
    protected void initConfiguration(TestConfigurator configuration) {
        configuration
                .setSamplesPerStep(60)
                .setMinConfidence(0.7)
                .setMaxPercentageMargin(5)
                .setTimeout(120, TimeUnit.SECONDS);
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
    public abstract void addSequence(final SequenceContainer<S> sequences);

    public abstract void addAssertions(
            AssertParametrizedSequencePerformance<?> assertion);

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return StringHelper.createName(name, seqName);
    }

    public PerformanceConsumer
            <Map<ComposedName, Map<ComposedName, PerformanceStats>>>
            getParamSequencePerformanceConsumer() {
        return paramSequencePerformanceConsumer;
    }

    public PerformanceConsumer<Map<ComposedName, PerformanceStats>>
        getParametrizedStatConsumer() {
        return parametrizedStatConsumer;
    }

    @Override
    public void executePerformanceTest(boolean printout) {

        TestConfigurator configuration = new TestConfigurator();
        printOutConfiguration(printout, configuration);
        initConfiguration(configuration);
        config(configuration);

        PerformanceTimer producer = configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration);

        ParametrizedPerformanceSuite<P> parametrizedSuite =
                new ParametrizedPerformanceSuite<>();
        addParameters(parametrizedSuite);
        parametrizedSuite.addPerformanceConsumer(getParametrizedStatConsumer());
        parametrizedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S> sequencedSuite =
                new ParametrizedSequencePerformanceSuite<>();
        addSequence(sequencedSuite);
        sequencedSuite.instrument(parametrizedSuite);

        addTests(sequencedSuite);

        AssertParametrizedSequencePerformance<?> assertion =
                new AssertParametrizedSequencePerformance<>();
        addAssertions(assertion);

        final Map<ComposedName, Map<ComposedName, PerformanceStats>> stats =
                sequencedSuite
                    .performGarbageCollection()
                    .addPerformanceConsumer(getParamSequencePerformanceConsumer())
                    .setName(configuration.getName())
                    .execute()
                    .use(assertion)
                    .getPerformance();

        printOutAssertion(printout, assertion,
                new ComposedName(configuration.getName()), stats);

        onAfterExecution(stats);
    }
}
