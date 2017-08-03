package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.time.HeatDetector;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeSampleMultiCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.GarbageCollectorExecutor;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TimeFormat;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

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
        extends AbstractStatsProducer<ConfigurableStatsProducer> {

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

    private final Map<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>>
                collectorSuppliers;
    private final Strategy strategy;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean filterSamples;
    private final boolean coolDownCpu;

    public ConfigurableStatsProducer(
            Configuration config,
            Strategy strategy) {
        this(config, strategy, null);
    }

    public ConfigurableStatsProducer(
            Configuration config,
            Strategy strategy,
            Map<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>>
                    collectorSuppliers) {
        super();
        this.collectorSuppliers = collectorSuppliers;
        this.strategy = strategy;
        HeatDetector.INSTANCE.init();
        this.timeoutNanoseconds = config.getTimeoutNanoseconds();
        this.garbageCollectorMillis = config.getGarbageCollectorMillis();
        this.filterSamples = config.getFilterSamples();
        this.coolDownCpu = config.getCoolDownCpu();
    }

    @Override
    public MixedAssertableHolder execute() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getPerformanceTimer().setName(getName());
        TimeSampleMultiCollector multiCollector = executeTests();
        getPerformanceTimer().clearTests();
        return multiCollector.getMixedAssertableHolder();
    }

    private void addTestsToPerformanceTimer() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test registered");
        }
        getPerformanceTimer().clearTests();
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            getPerformanceTimer().addTest(entry.getKey(), entry.getValue());
        }
    }

    protected TimeSampleMultiCollector executeTests() {
        int[] iterationsPerSample;
        int samples;
        TimeSample sample;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;
        TimeSampleMultiCollector multiCollector;
        Map<Class<? extends Assertable>, TimeStats> statsMap;
        long start = System.nanoTime();
        int repetitions = 0;
        do {
            multiCollector = new TimeSampleMultiCollector(
                    getName(), filterSamples, collectorSuppliers);

            iterationsPerSample = strategy.getIterations(getPerformanceTimer());
            checkIterationsValidity(iterationsPerSample);

            samples = strategy.getSamples();
            checkSampleValidity(samples);

            GarbageCollectorExecutor
                    .performGarbageCollection(garbageCollectorMillis);

            int sampleCounter = 0;
            SampleProgressionStatus status;
            do {
                sample = getPerformanceTimer().iterate(iterationsPerSample);
                multiCollector.add(sample);
                sampleCounter++;
                if (coolDownCpu) {
                    timeSpentCoolingCpuMs = HeatDetector.INSTANCE.checkCpuHeat();
                }

                statsMap = multiCollector.getStatsMap();

                status = new SampleProgressionStatus(
                        strategy.getRejectionMessage(),
                        sampleCounter, samples, repetitions,
                        iterationsPerSample, sample,
                        statsMap,
                        timeSpentCoolingCpuMs);
                notifySampleListeners(status);
                if (isTimeout(start)) {
                    throwTimeoutException(status);
                }
            } while (strategy.continueTakingSamples(status));

            // sets the rejection message
            Collection<TimeStats> timeStats = statsMap.values();
            toBeRepeated = strategy.repeatExecution(timeStats);
            notifyStatsListeners(getName(), timeStats, strategy.getRejectionMessage());

            repetitions++;
        } while(toBeRepeated);

        statsMap.values().stream().forEach((stats) -> {
            dispatchToConsumers(stats);
        });

        return multiCollector;
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
