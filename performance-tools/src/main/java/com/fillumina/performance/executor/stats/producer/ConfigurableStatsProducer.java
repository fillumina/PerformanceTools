package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.MixedStatsHolderCreator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTypedMap;
import com.fillumina.performance.time.HeatDetector;
import com.fillumina.performance.util.GarbageCollectorExecutor;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.filter.ConvergenceFilter;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TimeFormat;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collection;
import java.util.Map;
import com.fillumina.performance.assertion.AssertableExperiment;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducer
        extends AbstractSampleProducerInstrumenter<ConfigurableStatsProducer> {

    public interface Configuration {
        Quantity<IntervalUnit> getStatsTimeout();
        ListFilter<Double> getSampleFilter();
        int getGarbageCollectorMillis();
        boolean isCoolDownCpuActive();
    }

    public static class Builder<I extends Builder<I>> {
        private Quantity<IntervalUnit> statsTimeout =
                IntervalUnit.SECONDS.quantity(20);
        private ListFilter<Double> sampleFilter = DEFAULT_SAMPLE_FILTER;
        private int garbageCollectorMillis = -1;
        private boolean coolDownCpuActive = true;

        @SuppressWarnings("unchecked")
        public I setStatsTimeout(final Quantity<IntervalUnit> value) {
            this.statsTimeout = value;
            return (I) this;
        }

        @SuppressWarnings("unchecked")
        public I setSampleFilter(final ListFilter<Double> value) {
            this.sampleFilter = value;
            return (I) this;
        }

        @SuppressWarnings("unchecked")
        public I setGarbageCollectorMillis(final int value) {
            this.garbageCollectorMillis = value;
            return (I) this;
        }

        @SuppressWarnings("unchecked")
        public I setCoolDownCpuActive(final boolean value) {
            this.coolDownCpuActive = value;
            return (I) this;
        }

        public ConfigurableStatsProducer.Configuration buildConfiguration() {
            return new Configuration() {
                @Override public Quantity<IntervalUnit> getStatsTimeout() {
                    return statsTimeout;
                }
                @Override public ListFilter<Double> getSampleFilter() {
                    return sampleFilter;
                }
                @Override public int getGarbageCollectorMillis() {
                    return garbageCollectorMillis;
                }
                @Override
                public boolean isCoolDownCpuActive() {
                    return coolDownCpuActive;
                }
            };
        }
    }

    public interface Strategy {

        /**
         * @return the number of iterations for each test or EMPTY if
         * automatic.
         */
        UnmodifiableIntList getIterations();

        /**
         * @return the expected number of samples to take (effective number is
         * decided by {@link #continueTakingSamples(SampleProgressionStatus)}.
         * This value is used by ETA calculations.
         */
        int getExpectedNumberOfSamples();

        /**
         * @return an estimation of the error toward the enough samples
         *         collected condition.
         *         Should approximately be a monotone decreasing sequence
         *         ending with 0.0 (which means to stop taking samples).
         */
        double errorToStopTakingSamplesCondition(SampleProgressionStatus status);

        /**
         * Repeat the test completely (used when warmup or if
         * statistics should be unsatisfactory).
         *
         * @param stats the statistics
         * @return true to execute it again
         */
        boolean repeatExecution(final Collection<Stats> stats);

        /** @return status message. */
        String getStatusMessage();
    }

    private static final FilterChain<Double> DEFAULT_SAMPLE_FILTER =
            new FilterChain<>(33,
                    OutlierEliminatorFilter.INSTANCE,
                    ConvergenceFilter.INSTANCE);

    private static final Configuration DEFAULT_CONFIGURATION =
            new Builder().buildConfiguration();

    private final ListFilter<Double> filter;
    private final Strategy strategy;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean coolDownCpu;

    public ConfigurableStatsProducer(Strategy strategy) {
        this(DEFAULT_CONFIGURATION, strategy);
    }

    public ConfigurableStatsProducer(
            Configuration config,
            Strategy strategy) {
        super();
        this.strategy = strategy;
        HeatDetector.INSTANCE.init();
        this.timeoutNanoseconds =
                (long) config.getStatsTimeout().as(IntervalUnit.NANOSECONDS);
        this.garbageCollectorMillis = config.getGarbageCollectorMillis();
        this.coolDownCpu = config.isCoolDownCpuActive();
        ListFilter<Double> lf = config.getSampleFilter();
        if (lf != null) {
            filter = lf;
        } else {
            filter = DEFAULT_SAMPLE_FILTER;
        }
    }

    @Override
    public MixedStatsHolder get() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getSampleProducer().setName(getName());
        MixedStatsHolder multiCollector = executeTests();
        getSampleProducer().clearTests();
        return multiCollector;
    }

    private void addTestsToPerformanceTimer() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test registered");
        }
        getSampleProducer().clearTests();
        getTests().entrySet().forEach((entry) -> {
            getSampleProducer().addTest(entry.getKey(), entry.getValue());
        });
    }

    @SuppressWarnings("unchecked")
    protected MixedStatsHolder executeTests() {
        int sampleNumber;
        boolean toBeRepeated;
        int coolerTime = -1;
        MixedStatsHolderCreator creator;
        MixedStatsHolder mixedHolder;
        Map<StatsType,Stats> statsMap;
        Collection<Stats> statsColl;
        int repetitions = 0;

        long start = System.nanoTime();
        do {
            creator = new MixedStatsHolderCreator(getName());

            sampleNumber = strategy.getExpectedNumberOfSamples();
            checkSampleValidity(sampleNumber);

            GarbageCollectorExecutor
                    .performGarbageCollection(garbageCollectorMillis);

            double error;
            int sampleCounter = 0;
            SampleProgressionStatus status;
            setUpTests();
            do {
                UnmodifiableIntList iterationsPerSample =
                        strategy.getIterations();

                Map<StatsType,Sample> resultSampleMap =
                        executeTests(iterationsPerSample.toIntArray());
                creator.addSample(resultSampleMap);

                sampleCounter++;
                if (coolDownCpu) {
                    coolerTime = HeatDetector.INSTANCE.checkCpuHeat();
                }

                mixedHolder = creator.getMixedAssertableHolder(filter);

                status = new SampleProgressionStatus(
                        sampleCounter,
                        sampleNumber, repetitions, iterationsPerSample,
                        resultSampleMap,
                        mixedHolder,
                        coolerTime,
                        strategy.getStatusMessage());
                error = strategy.errorToStopTakingSamplesCondition(status);
                status.setError(error);

                notifySampleListeners(status);

                if (isTimeout(start)) {
                    throwTimeoutException(status);
                }
            } while (error != 0);
            tearDownTests();

            statsMap = getAllAssertables(mixedHolder);
            statsColl = statsMap.values();
            toBeRepeated = strategy.repeatExecution(statsColl);
            notifyStatsListeners(
                    new StatsProgressionStatus(getName(), statsColl,
                        strategy.getStatusMessage()));

            repetitions++;
        } while(toBeRepeated);

        for (Stats s : statsColl) {
            dispatchToConsumers(s);
        }

        return mixedHolder;
    }

    private Map<StatsType, Sample> executeTests(int[] iterationsPerSample) {
        if (iterationsPerSample == null ||
                iterationsPerSample.length != getTests().size()) {
            return getSampleProducer().execute();
        }
        return getSampleProducer().executeWithIterations(iterationsPerSample);
    }

    @SuppressWarnings("unchecked")
    private Map<StatsType, Stats> getAllAssertables(
            MixedStatsHolder mixedHolder) {
        StatsTypedMap<Stats> map = new StatsTypedMap<>();
        for (StatsHolder h : mixedHolder.getStatsMap().values()) {
            for (AssertableExperiment a : h.getFlattenedAssertableMap().values()) {
                map.add((Stats)a);
            }
        }
        return map;
    }

    private boolean isTimeout(long start) {
        return timeoutNanoseconds > 0 &&
                System.nanoTime() - start > timeoutNanoseconds;
    }

    private void throwTimeoutException(SampleProgressionStatus status) {
        String name = getName().toString();
        String testName = (name == null || name.isEmpty()) ? "" :
                "'" + name + "' ";
        throw new RuntimeException("Timeout occurred: test " + testName +
                "was lasting " +
                "more than required maximum of " +
                TimeFormat.TEXT.second(timeoutNanoseconds) +
                System.lineSeparator() + status.toString());
    }

    private void checkSampleValidity(int samples) {
        if (samples <= 0) {
            throw new IllegalStateException("invalid samples: " + samples);
        }
    }

    private void setUpTests() {
        getTests().values().forEach(
                r -> AnnotatedRunnableSetter.INSTANCE.setUp(r));
    }

    private void tearDownTests() {
        getTests().values().forEach(
                r -> AnnotatedRunnableSetter.INSTANCE.tearDown(r));
    }
}
