package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.HeatDetector;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.formatter.TimeFormat;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for other progression performance instrumenters.
 *
 * @see ProgressionPerformanceInstrumenter
 * @see AutoProgressionPerformanceInstrumenter
 *
 * @param I self (so fluent interface can be extended to subclasses)
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumenter
                <I extends AbstractPerformanceInstrumenter<I>>
        extends AbstractPerformanceProducer<I, SpeedStats, Testable>
        implements Instrumenter<PerformanceTimer>,
                   StatsProducer<SpeedStats> {

    private PerformanceTimer performanceTimer;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final boolean filterSamples;
    private final boolean coolDownCpu;
    private List<SampleProgressionStatusListener> sampleStatusListeners;
    private List<StatsProgressionStatusListener> statsStatusListeners;

    public AbstractPerformanceInstrumenter(StaticPath name,
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

    protected PerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    /** Accepts only {@link PerformanceTimer} producers. */
    @Override
    @SuppressWarnings("unchecked")
    public I instrument(PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return (I) this;
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
        performanceTimer.setName(getName());
        SpeedStats stats = executeTests();
        performanceTimer.clearTests();
        return new PHolder<>(getName(), stats,
                WrapperSpeedStatsTableStringGenerator.INSTANCE);
    }

    protected void addTestsToPerformanceTimer() {
        performanceTimer.clearTests();
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            performanceTimer.addTest(entry.getKey(), entry.getValue());
        }
    }

    private SpeedStats executeTests() {
        long start = System.nanoTime();
        SpeedSampleCollector collector;
        int[] iterationsPerSample;
        int totalSamples;
        SpeedSample speedSample;
        SpeedStats stats = null;
        boolean toBeRepeated;
        int timeSpentCoolingCpuMs = -1;

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
                speedSample = performanceTimer.execute(iterationsPerSample);
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

        dispatchToConsumers(new PHolder<>(getName(), stats));

        return stats;
    }

    protected void throwTimeoutException(SampleProgressionStatus status) {
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

    private void assertPerformanceExecutorNotNull() {
        if (performanceTimer == null) {
            throw new IllegalStateException(getClass().getCanonicalName() +
                ": an instrumentable class must be provided with instrument()");
        }
    }

    /** Assert positive non zero. */
    protected void assertStrictlyPositive(final int positiveValue,
            final String name) {
        if (positiveValue <= 0) {
            throw new IllegalArgumentException(name +
                    " cannot be negative or zero: " +
                    positiveValue);
        }
    }

    @Override
    public <T extends Instrumenter<StatsProducer<SpeedStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @SuppressWarnings("unchecked")
    public I addSampleProgressionListener(SampleProgressionStatusListener listener) {
        if (listener != null) {
            if (sampleStatusListeners == null) {
                sampleStatusListeners = new ArrayList<>();
            }
            sampleStatusListeners.add(listener);
        }
        return (I) this;
    }

    private void notifySampleListeners(SampleProgressionStatus status) {
        if (sampleStatusListeners != null) {
            for (SampleProgressionStatusListener l : sampleStatusListeners) {
                l.acceptSampleProgressionStatus(status);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public I addStatsProgressionListener(
            StatsProgressionStatusListener listener) {
        if (listener != null) {
            if (statsStatusListeners == null) {
                statsStatusListeners = new ArrayList<>();
            }
            statsStatusListeners.add(listener);
        }
        return (I) this;
    }

    private void notifyStatsListeners(StaticPath name, SpeedStats stats,
            String rejectionMessage) {
        if (statsStatusListeners != null) {
            for (StatsProgressionStatusListener l : statsStatusListeners) {
                l.acceptStatsProgressionStatus(name, stats, rejectionMessage);
            }
        }
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
