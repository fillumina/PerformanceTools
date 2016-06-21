package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedStringGenerator;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StringHelper;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate
            <Map<ComposedName, Map<ComposedName, SpeedStats>>,
             ParametrizedSequenceTestable<P,S>> {

    private PerformanceConsumer
                <Map<ComposedName, Map<ComposedName, SpeedStats>>>
            paramSequencePerformanceConsumer =
            NullPerformanceConsumer.
                <Map<ComposedName, Map<ComposedName, SpeedStats>>>instance();

    private PerformanceConsumer<Map<ComposedName, SpeedStats>>
            parametrizedStatConsumer =
            NullPerformanceConsumer.<Map<ComposedName, SpeedStats>>instance();

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
                SpeedStringGenerator.parametrizedSequenceViewer();
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
        parametrizedStatConsumer = SpeedStringGenerator.parametrizedViewer();
        paramSequencePerformanceConsumer =
                SpeedStringGenerator.parametrizedSequenceViewer();
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
            AssertParametrizedSequencePerformance<Void, SpeedStats> assertion);

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return StringHelper.createName(name, seqName);
    }

    public PerformanceConsumer
            <Map<ComposedName, Map<ComposedName, SpeedStats>>>
            getParamSequencePerformanceConsumer() {
        return paramSequencePerformanceConsumer;
    }

    public PerformanceConsumer<Map<ComposedName, SpeedStats>>
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

        ParametrizedPerformanceSuite<P,SpeedStats> parametrizedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(parametrizedSuite);
        parametrizedSuite.addPerformanceConsumerIf(printout,
                getParametrizedStatConsumer());
        parametrizedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S,SpeedStats> sequencedSuite =
                SpeedSuite.<P,S>parametrizedSequenceSuite();
        addSequence(sequencedSuite);
        sequencedSuite.instrument(parametrizedSuite);

        addTests(sequencedSuite);

        AssertParametrizedSequencePerformance<Void, SpeedStats> assertion =
                AssertSpeed.parametrizedSequence();
        addAssertions(assertion);

        final Map<ComposedName, Map<ComposedName, SpeedStats>> stats =
                sequencedSuite
                    .performGarbageCollection(configuration.garbageCollectorMillis)
                    .addPerformanceConsumerIf(printout,
                            getParamSequencePerformanceConsumer())
                    .setName(configuration.getName())
                    .execute()
                    .use(assertion)
                    .getPerformance();

        printOutAssertion(printout, assertion,
                ComposedName.create(configuration.getName()), stats);

        onAfterExecution(stats);
    }
}
