package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.time.HeatDetector;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TimeFormat;
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
public class ConfigurableStatsProducer<T extends TimeStats>
        extends AbstractStatsProducer<ConfigurableStatsProducer<T>, T> {

    public interface Configuration {
        long getTimeoutNanoseconds();
        int getGarbageCollectorMillis();
        boolean getFilterSamples();
        boolean getCoolDownCpu();
        Supplier<TimeSampleCollector<? extends TimeStats>> getCollector();
    }

    public interface Strategy<T extends TimeStats> {

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
        boolean repeatExecution(final T stats);

        /** Called when new tests are being submitted. */
        void onReset();

        /** @return the error message (null for no errors). */
        String getRejectionMessage();
    }

    private final Supplier<TimeSampleCollector<? extends TimeStats>>
            collectorSupplier;
    private final Strategy<T> strategy;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean filterSamples;
    private final boolean coolDownCpu;

    public ConfigurableStatsProducer(
            Configuration config,
            Strategy<T> strategy) {
        super();
        this.collectorSupplier = config.getCollector();
        this.strategy = strategy;
        HeatDetector.INSTANCE.init();
        this.timeoutNanoseconds = config.getTimeoutNanoseconds();
        this.garbageCollectorMillis = config.getGarbageCollectorMillis();
        this.filterSamples = config.getFilterSamples();
        this.coolDownCpu = config.getCoolDownCpu();
    }

    @Override
    public PHolder<T> execute() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getPerformanceTimer().setName(getName());
        T stats = executeTests();
        getPerformanceTimer().clearTests();
        return new PHolder<>(getName(), stats);
        // TODO use a parameter for this or use stats.toString()
//                WrapperSpeedStatsTableStringGenerator.INSTANCE);
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

    protected T executeTests() {
        TimeSampleCollector<T> collector;
        int[] iterationsPerSample;
        int samples;
        TimeSample speedSample;
        T stats = null;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;

        long start = System.nanoTime();
        int repetitions = 0;
        do {
            collector = createCollector();

            iterationsPerSample = strategy.getIterations(getPerformanceTimer());
            checkIterationsValidity(iterationsPerSample);

            samples = strategy.getSamples();
            checkSampleValidity(samples);

            performGarbageCollection(garbageCollectorMillis);

            int sampleCounter = 0;
            SampleProgressionStatus status;
            do {
                speedSample = getPerformanceTimer().iterate(iterationsPerSample);
                collector.add(speedSample);
                sampleCounter++;
                if (coolDownCpu) {
                    timeSpentCoolingCpuMs = HeatDetector.INSTANCE.checkCpuHeat();
                }

                stats = collector
                        .createStatsAndFilterIf(filterSamples);
                status = new SampleProgressionStatus(
                        strategy.getRejectionMessage(),
                        sampleCounter, samples, repetitions,
                        iterationsPerSample,
                        speedSample, stats,
                        timeSpentCoolingCpuMs,
                        collector);
                notifySampleListeners(status);
                if (isTimeout(start)) {
                    throwTimeoutException(status);
                }
            } while (strategy.continueTakingSamples(status));

            // sets the rejection message
            toBeRepeated = strategy.repeatExecution(stats);
            notifyStatsListeners(getName(), stats,
                    strategy.getRejectionMessage());

            repetitions++;
        } while(toBeRepeated);

        dispatchToConsumers(stats);

        return stats;
    }

    @SuppressWarnings("unchecked")
    private TimeSampleCollector<T> createCollector() {
        return (TimeSampleCollector<T>) collectorSupplier.get();
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
