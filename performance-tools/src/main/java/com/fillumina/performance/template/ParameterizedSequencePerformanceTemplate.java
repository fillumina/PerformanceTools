package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedPerformanceSuite;
import com.fillumina.performance.suite.ParameterizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParameterizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.util.formatter.StringHelper;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class ParameterizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate
            <ParameterizedSequenceTestable<P,S>,
             SpeedStats,
             MemStats,
             AssertParameterizedSequencePerformanceImpl<ParameterizedSequenceAssertion, SpeedStats>,
             AssertParameterizedSequencePerformanceImpl<ParameterizedSequenceAssertion, MemStats>> {

    @Override
    protected void initConfiguration(TestConfiguration configuration) {
        configuration.getSpeed()
                //.setSamples(33)
                .setMaxPercentageMargin(5)
                .setTimeout(360, TimeUnit.SECONDS);
    }

    /**
     * Adds named parameters to tests.
     * <pre>
     * parameters
     *       .addParameter(NAME_1, VALUE_1)
     *       .addParameter(NAME_2, VALUE_2)
     *       .addParameter(NAME_3, VALUE_3);
     * </pre>
     * @param params
     */
    public abstract void addParameters(final ParameterContainer<P> params);

    /**
     * Adds a sequence to tests.
     * <pre>
     * sequences.setSequence('x', 'y', 'z');
     * </pre>
     */
    public abstract void addSequence(final SequenceContainer<S> sequence);

    public abstract void addAssertions(ParameterizedSequenceAssertion assertion);

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return StringHelper.createName(name, seqName);
    }

    @Override
    protected void appendConfigParameters(Appendable appendable) {
        final TableFormatter tf = new TableFormatter();

        tf.header("sequence", TableFormatter.Alignment.LEFT, '-');

        addSequence(new SequenceContainer<S>() {
            @Override
            public SequenceContainer<S> setSequence(S... sequence) {
                for (S s : sequence) {
                    setSequenceItem(String.valueOf(s), s);
                }
                return this;
            }

            @Override
            public SequenceContainer<S> setSequence(Iterable<S> iterable) {
                for (S s : iterable) {
                    setSequenceItem(String.valueOf(s), s);
                }
                return this;
            }

            @Override
            public SequenceContainer<S> setSequence(
                    Map<String, S> namedSequence) {
                for (Map.Entry<String,S> entry : namedSequence.entrySet()) {
                    setSequenceItem(entry.getKey(), entry.getValue());
                }
                return this;
            }

            @Override
            public SequenceContainer<S> setSequenceItem(String name, S item) {
                tf.cell(name, ":").cell(item).endl();
                return this;
            }
        });

        tf.endl();

        tf.header("paramenters", TableFormatter.Alignment.LEFT, '-');

        addParameters(new ParameterContainer<P>() {
            @Override
            public ParameterContainer<P> addParameter(String name, P param) {
                tf.cell(name, ":").cell(param).endl();
                return this;
            }
        });

        try {
            appendable.append(tf.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected MixedAssertion<AssertParameterizedSequencePerformanceImpl
                        <ParameterizedSequenceAssertion, SpeedStats>,
                   AssertParameterizedSequencePerformanceImpl
                        <ParameterizedSequenceAssertion, MemStats>>
            createAndInitAssertion() {
        ParameterizedSequenceAssertion assertion =
            new ParameterizedSequenceAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected PerformanceHolder<SpeedStats> executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParameterizedSequencePerformanceImpl
                    <ParameterizedSequenceAssertion, SpeedStats> assertion,
            AutoProgressionPerformanceInstrumenter progression) {

        ParameterizedPerformanceSuite<P,SpeedStats,SpeedStats> parameterizedSpeedSuite =
                SpeedSuite.<P>parameterizedSuite();
        addParameters(parameterizedSpeedSuite);
        parameterizedSpeedSuite.instrument(progression);

        ParameterizedSequencePerformanceSuite<P,S,SpeedStats,SpeedStats,SpeedStats>
                sequencedSpeedSuite =
                    SpeedSuite.<P,S>parameterizedSequenceSuite();
        addSequence(sequencedSpeedSuite);
        sequencedSpeedSuite.instrument(parameterizedSpeedSuite);

        addTests(sequencedSpeedSuite);

        return sequencedSpeedSuite
                .performGarbageCollection(
                        speedConfiguration.garbageCollectorMillis)
                .setName(testName)
                .execute()
                .check(assertion);
    }

    @Override
    protected PerformanceHolder<MemStats> executeMem(String testName,
            AssertParameterizedSequencePerformanceImpl
                    <ParameterizedSequenceAssertion, MemStats> assertion,
            MemAnalyzer analyzer) {

        ParameterizedPerformanceSuite<P,MemStats,MemStats> parameterizedMemSuite =
                MemSuite.<P>parameterizedSuite();
        addParameters(parameterizedMemSuite);
        parameterizedMemSuite.instrument(analyzer);

        ParameterizedSequencePerformanceSuite<P,S,MemStats,MemStats,MemStats>
                sequencedMemSuite =
                    MemSuite.<P,S>parameterizedSequenceSuite();
        sequencedMemSuite.instrument(parameterizedMemSuite);
        addSequence(sequencedMemSuite);
        addTests(sequencedMemSuite);

        return sequencedMemSuite
                .setName(testName)
                .execute()
                .check(assertion);
    }
}
