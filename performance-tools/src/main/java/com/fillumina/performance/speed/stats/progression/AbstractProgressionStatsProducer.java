package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.HeatDetector;
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
public abstract class AbstractProgressionStatsProducer
                <I extends AbstractProgressionStatsProducer<I>>
        extends AbstractStatsProducer<I> {

    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean filterSamples;
    private final boolean coolDownCpu;

    public AbstractProgressionStatsProducer(TName name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            boolean filterSamples,
            boolean coolDownCpu,
            PerformanceConsumer<SpeedStats>[] performanceStatsConsumers) {
        super();
        HeatDetector.INSTANCE.init();
        setName(name);
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.filterSamples = filterSamples;
        this.coolDownCpu = coolDownCpu;
        if (performanceStatsConsumers != null) {
            for (PerformanceConsumer<SpeedStats> pc :
                    performanceStatsConsumers) {
                addPerformanceConsumer(pc);
            }
        }
    }

    /** @return an error message. */
    protected abstract String getRejectionMessage();

    /** @return the number of samples to take. */
    protected abstract int getSamples();

    /** @return the number of iterations for each sample. */
    protected abstract int[] getIterations();

    protected boolean continueTakingSamples(SampleProgressionStatus status,
            boolean timeout) {
        if (timeout) {
            throwTimeoutException(status);
        }
        return true;
    }

    /**
     * Override if you need to stop the sequence.
     *
     * @param stats the current step's performances
     * @return {@code true} if you want to stop at this step
     */
    protected abstract boolean repeatExecution(final SpeedStats stats);

    /**
     * Override if you need to use non default sample filters.
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
        getPerformanceTimer().clearTests();
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            getPerformanceTimer().addTest(entry.getKey(), entry.getValue());
        }
    }

    private SpeedStats executeTests() {
        SpeedSampleCollector collector;
        int[] iterationsPerSample;
        int totalSamples;
        SpeedSample speedSample;
        SpeedStats stats = null;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;

        long start = System.nanoTime();
        int repetitions = 0;
        do {
            collector = createSampleCollector();
            iterationsPerSample = getIterations();
            checkIterationsValidity(iterationsPerSample);
            totalSamples = getSamples();

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
                status = new SampleProgressionStatus(getRejectionMessage(),
                        sampleCounter, totalSamples, repetitions,
                        iterationsPerSample,
                        speedSample, stats,
                        timeSpentCoolingCpuMs);
                notifySampleListeners(status);
            } while (sampleCounter < totalSamples &&
                    continueTakingSamples(status, isTimeout(start)));

            stats = collector
                    .createPerformanceStatsAndFilterIf(filterSamples);
            toBeRepeated = repeatExecution(stats); // sets the rejection message
            notifyStatsListeners(getName(), stats, getRejectionMessage());

            repetitions++;
        } while(toBeRepeated);

        dispatchToConsumers(getName(), stats);

        return stats;
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

    private boolean isTimeout(long start) {
        return timeoutNanoseconds > 0 &&
                System.nanoTime() - start > timeoutNanoseconds;
    }

    private void checkIterationsValidity(int[] iterations) {
        for (int it : iterations) {
            if (it < 0) {
                throw new IllegalStateException(
                        "too many iterations required, " +
                        "check the stability of the algorithm");
            }
        }
    }
}
