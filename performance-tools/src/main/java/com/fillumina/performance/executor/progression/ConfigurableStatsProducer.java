package com.fillumina.performance.executor.progression;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsCreator;
import com.fillumina.performance.time.HeatDetector;
import com.fillumina.performance.util.GarbageCollectorExecutor;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.ConvergenceFilter;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TimeFormat;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collection;
import java.util.Map;

/**
 * Base class for other progression performance statistics producer.
 *
 * @see ProgressionPerformanceInstrumenter
 * @see AutoProgressionPerformanceInstrumenter
 *
 * @param I self (so fluent interface can be extended to subclasses)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducer<S extends Stats<?>,
                                       A extends AbstractSample<A,?,S>>
        extends AbstractSampleProducerInstrumenter
                    <ConfigurableStatsProducer<S,A>, S, A> {

    public interface Configuration {
        Quantity<IntervalUnit> getStatsTimeout();
        ListFilter<Double> getSampleFilter();
        int getGarbageCollectorMillis();
        boolean getCoolDownCpu();
    }

    public interface Strategy {

        /** @return the number of iterations for each test. */
        int[] getIterations();

        /** @return the number of samples to take. */
        int getSamples();

        /**
         * @return true to continue taking samples
         *          (even past the required number).
         */
        boolean continueTakingSamples(SampleProgressionStatus status);

        /**
         * Repeat the test completely.
         *
         * @param stats the statistics relative to the current step
         * @return true to execute the whole execution again
         */
        boolean repeatExecution(final Collection<? extends Stats<?>> stats);

        /** Called when new tests are being submitted. */
        void onReset();

        /** @return the error message (null for no errors). */
        String getErrorMessage();
    }

    private static final FilterChain<Double> DEFAULT_SAMPLE_FILTER =
            new FilterChain<>(33,
                    OutlierEliminatorFilter.INSTANCE,
                    ConvergenceFilter.INSTANCE);

    private static final Configuration DEFAULT_CONFIGURATION =
            new Configuration() {
        @Override
        public Quantity<IntervalUnit> getStatsTimeout() {
            return IntervalUnit.SECONDS.quantity(20);
        }

        @Override
        public ListFilter<Double> getSampleFilter() {
            return DEFAULT_SAMPLE_FILTER;
        }

        @Override
        public int getGarbageCollectorMillis() {
            return -1;
        }

        @Override
        public boolean getCoolDownCpu() {
            return true;
        }
    };

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
        this.coolDownCpu = config.getCoolDownCpu();
        ListFilter<Double> lf = config.getSampleFilter();
        if (lf != null) {
            filter = lf;
        } else {
            filter = DEFAULT_SAMPLE_FILTER;
        }
    }

    @Override
    public MixedAssertableHolder get() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getSampleProducer().setName(getName());
        MixedAssertableHolder multiCollector = executeTests();
        getSampleProducer().clearTests();
        return multiCollector;
    }

    private void addTestsToPerformanceTimer() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test registered");
        }
        getSampleProducer().clearTests();
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            getSampleProducer().addTest(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    protected MixedAssertableHolder executeTests() {
        int sampleNumber;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = 0;
        StatsCreator<S, A> creator;
        MixedAssertableHolder mixedHolder;
        Map<Class<?>,S> statsMap;
        Collection<? extends Stats<?>> statsColl;
        int repetitions = 0;

        long start = System.nanoTime();
        do {
            creator = new StatsCreator<>(getName());

            sampleNumber = strategy.getSamples();
            checkSampleValidity(sampleNumber);

            GarbageCollectorExecutor
                    .performGarbageCollection(garbageCollectorMillis);

            int sampleCounter = 0;
            SampleProgressionStatus status;
            setUpTests();
            do {
                int[] iterationsPerSample = strategy.getIterations();

                Map<Class<?>,A> resultSampleMap =
                        executeTests(iterationsPerSample);
                creator.addSample(resultSampleMap);

                sampleCounter++;
                if (coolDownCpu) {
                    timeSpentCoolingCpuMs +=
                            HeatDetector.INSTANCE.checkCpuHeat();
                }

                mixedHolder = creator.getMixedAssertableHolder(filter);

                status = new SampleProgressionStatus(
                        sampleCounter, iterationsPerSample,
                        sampleNumber, repetitions,
                        resultSampleMap,
                        mixedHolder,
                        timeSpentCoolingCpuMs,
                        strategy.getErrorMessage());
                notifySampleListeners(status);


                if (isTimeout(start)) {
                    throwTimeoutException(status);
                }
            } while (strategy.continueTakingSamples(status));
            tearDownTests();

            // sets the rejection message
            statsMap = getAllAssertables(mixedHolder);
            statsColl = statsMap.values();
            toBeRepeated = strategy.repeatExecution(statsColl);
            notifyStatsListeners(getName(), statsColl,
                    strategy.getErrorMessage());

            repetitions++;
        } while(toBeRepeated);

        for (Stats<?> s : statsColl) {
            dispatchToConsumers((S)s);
        }

        return mixedHolder;
    }

    private Map<Class<?>, A> executeTests(int[] iterationsPerSample) {
        if (iterationsPerSample == null ||
                iterationsPerSample.length != getTests().size()) {
            return getSampleProducer().execute();
        }
        return getSampleProducer().executeWithIterations(iterationsPerSample);
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>, S> getAllAssertables(
            MixedAssertableHolder mixedHolder) {
        Map<Class<?>, S> map = new LinkedMap<>();
        for (AssertableHolder<?> h : mixedHolder.getStatsMap().values()) {
            for (Assertable a : h.getFlattenedAssertableMap().values()) {
                map.put(a.getClass(), (S)a);
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
