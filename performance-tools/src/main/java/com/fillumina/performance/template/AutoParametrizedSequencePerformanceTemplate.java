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
import com.fillumina.performance.suite.assertion.AssertParametrizedSequenceSpeed;
import com.fillumina.performance.suite.formatter.StringTableParametrizedSequenceStatsFormatter;
import com.fillumina.performance.suite.formatter.StringTableParametrizedStatsFormatter;
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
                StringTableParametrizedSequenceStatsFormatter.VIEWER;
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
        parametrizedStatConsumer = StringTableParametrizedStatsFormatter.VIEWER;
        paramSequencePerformanceConsumer =
                StringTableParametrizedSequenceStatsFormatter.VIEWER;
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
            AssertParametrizedSequenceSpeed assertion);

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
        parametrizedSuite.addPerformanceConsumerIf(printout,
                getParametrizedStatConsumer());
        parametrizedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S> sequencedSuite =
                new ParametrizedSequencePerformanceSuite<>();
        addSequence(sequencedSuite);
        sequencedSuite.instrument(parametrizedSuite);

        addTests(sequencedSuite);

        AssertParametrizedSequenceSpeed assertion =
                new AssertParametrizedSequenceSpeed();
        addAssertions(assertion);

        final Map<ComposedName, Map<ComposedName, PerformanceStats>> stats =
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
