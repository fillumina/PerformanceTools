package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.HeatDetector;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TimeFormat;
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
        boolean repeatExecution(final SpeedStats stats);

        /** Called when new tests are being submitted. */
        void onReset();

        /** @return the error message (null for no errors). */
        String getRejectionMessage();
    }

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

    /**
     * Override if you need to use non default sample collector
     * (i.e. with different filters).
     */
    protected SpeedSampleCollector createSampleCollector() {
        return new SpeedSampleCollector();
    }

    @Override
    public PHolder<SpeedStats> execute() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        getPerformanceTimer().setName(getName());
        SpeedStats stats = executeTests();
        getPerformanceTimer().clearTests();
        return new PHolder<>(getName(), stats,
                WrapperSpeedStatsTableStringGenerator.INSTANCE);
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

    private SpeedStats executeTests() {
        SpeedSampleCollector collector;
        int[] iterationsPerSample;
        int samples;
        SpeedSample speedSample;
        SpeedStats stats = null;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;

        long start = System.nanoTime();
        int repetitions = 0;
        do {
            collector = createSampleCollector();

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
                        .createPerformanceStatsAndFilterIf(filterSamples);
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
