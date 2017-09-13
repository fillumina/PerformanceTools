package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.stats.StatsCreator;
import com.fillumina.performance.time.HeatDetector;
import com.fillumina.performance.time.sample.AbstractTimeSample;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.sample.ThroughputSample;
import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.GarbageCollectorExecutor;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.ConvergenceFilter;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TimeFormat;
import com.fillumina.performance.util.tname.TName;
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
public class ConfigurableStatsProducer
        extends AbstractPerformanceTimerInstrumenter<ConfigurableStatsProducer> {

    public interface Configuration {
        long getTimeoutNanoseconds();
        int getGarbageCollectorMillis();
        boolean getFilterSamples();
        boolean getCoolDownCpu();
    }

    public interface Strategy {

        /** @return the number of iterations for each test. */
        int[] getIterations(PerformanceTimer pt);

        /** @return the (approximated) number of samples to take. */
        int getSamples();

        /** @return true to continue taking samples. */
        boolean continueTakingSamples(SampleProgressionStatus status);

        /**
         * Repeat the test completely.
         *
         * @param stats the statistics relative to the current step
         * @return true to execute the whole execution again
         */
        boolean repeatExecution(final Collection<TimeStats> stats);

        /** Called when new tests are being submitted. */
        void onReset();

        /** @return the error message (null for no errors). */
        String getRejectionMessage();
    }

    private final ListFilter<Double> filter =
            new FilterChain<>(33,
                    OutlierEliminatorFilter.INSTANCE,
                    ConvergenceFilter.INSTANCE);

    private final Strategy strategy;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean filterSamples;
    private final boolean coolDownCpu;

    public ConfigurableStatsProducer(
            Configuration config,
            Strategy strategy) {
        super();
        this.strategy = strategy;
        HeatDetector.INSTANCE.init();
        this.timeoutNanoseconds = config.getTimeoutNanoseconds();
        this.garbageCollectorMillis = config.getGarbageCollectorMillis();
        this.filterSamples = config.getFilterSamples();
        this.coolDownCpu = config.getCoolDownCpu();
    }

    @Override
    public MixedAssertableHolder get() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getPerformanceTimer().setName(getName());
        MixedAssertableHolder multiCollector = executeTests();
        getPerformanceTimer().clearTests();
        return multiCollector;
    }

    //TODO there is a super method for that
    private void addTestsToPerformanceTimer() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test registered");
        }
        getPerformanceTimer().clearTests();
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            getPerformanceTimer().addTest(entry.getKey(), entry.getValue());
        }
    }

    protected MixedAssertableHolder executeTests() {
        int[] iterationsPerSample;
        int samples;
        TimeSampleBuilder sampleBuilder;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;
        StatsCreator<TimeStats, AbstractTimeSample> creator;
        MixedAssertableHolder mixedHolder;
        Collection<TimeStats> timeStatsColl;
        Map<Class<? extends Assertable>, TimeStats> timeStatsMap;
        long start = System.nanoTime();
        int repetitions = 0;
        do {
            creator = new StatsCreator<>(getName());

            iterationsPerSample = strategy.getIterations(getPerformanceTimer());
            checkIterationsValidity(iterationsPerSample);

            samples = strategy.getSamples();
            checkSampleValidity(samples);

            GarbageCollectorExecutor
                    .performGarbageCollection(garbageCollectorMillis);

            int sampleCounter = 0;
            SampleProgressionStatus status;
            do {
                sampleBuilder = getPerformanceTimer().iterate(iterationsPerSample);
                AverageTimeSample avgSample =
                        sampleBuilder.buildAverageTimeSample();
                creator.addSample(avgSample);
                ThroughputSample trpSample =
                        sampleBuilder.buildThroughputSample();
                creator.addSample(trpSample);

                sampleCounter++;
                if (coolDownCpu) {
                    timeSpentCoolingCpuMs = HeatDetector.INSTANCE.checkCpuHeat();
                }

                mixedHolder = creator.getMixedAssertableHolder(filter);
                timeStatsMap = getAllAssertables(mixedHolder);

                status = new SampleProgressionStatus(
                        strategy.getRejectionMessage(),
                        sampleCounter, samples, repetitions,
                        iterationsPerSample,
                        avgSample, trpSample,
                        timeStatsMap,
                        timeSpentCoolingCpuMs);
                notifySampleListeners(status);


                if (isTimeout(start)) {
                    throwTimeoutException(status);
                }
            } while (strategy.continueTakingSamples(status));

            // sets the rejection message
            timeStatsColl = timeStatsMap.values();
            toBeRepeated = strategy.repeatExecution(timeStatsColl);
            notifyStatsListeners(getName(), timeStatsColl,
                    strategy.getRejectionMessage());

            repetitions++;
        } while(toBeRepeated);

        for (TimeStats s : timeStatsColl) {
            dispatchToConsumers(s);
        }

        return mixedHolder;
    }

    private Map<Class<? extends Assertable>, TimeStats> getAllAssertables(
            MixedAssertableHolder mixedHolder) {
        Map<Class<? extends Assertable>, TimeStats> map = new LinkedMap<>();
        for (AssertableHolder<?> h : mixedHolder.getStatsMap().values()) {
            for (Assertable a : h.getFlattenedAssertableMap().values()) {
                map.put(a.getClass(), (TimeStats) a);
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

    private void checkIterationsValidity(int[] iterations) {
        for (int it : iterations) {
            if (it <= 0) {
                throw new IllegalStateException("invalid iterations: " + it);
            }
        }
    }

    private void checkSampleValidity(int samples) {
        if (samples <= 0) {
            throw new IllegalStateException("invalid samples: " + samples);
        }
    }
}
