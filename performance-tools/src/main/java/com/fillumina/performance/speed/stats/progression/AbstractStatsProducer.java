package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducer
            <I extends AbstractStatsProducer<I>>
        extends AbstractPerformanceProducer<I, SpeedStats, Runnable>
        implements Instrumenter<PerformanceTimer>, StatsProducer<SpeedStats> {

    private List<SampleProgressionStatusListener> sampleStatusListeners;
    private List<StatsProgressionStatusListener> statsStatusListeners;
    private PerformanceTimer performanceTimer;

    public AbstractStatsProducer() {
        super();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public I instrument(PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return (I) this;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<SpeedStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @SuppressWarnings(value = "unchecked")
    public I addSampleProgressionListener(
            SampleProgressionStatusListener listener) {
        if (listener != null) {
            if (sampleStatusListeners == null) {
                sampleStatusListeners = new ArrayList<>();
            }
            sampleStatusListeners.add(listener);
        }
        return (I) this;
    }

    protected void notifySampleListeners(SampleProgressionStatus status) {
        if (sampleStatusListeners != null) {
            for (SampleProgressionStatusListener l : sampleStatusListeners) {
                l.acceptSampleProgressionStatus(status);
            }
        }
    }

    @SuppressWarnings(value = "unchecked")
    public I addStatsProgressionListener(StatsProgressionStatusListener listener) {
        if (listener != null) {
            if (statsStatusListeners == null) {
                statsStatusListeners = new ArrayList<>();
            }
            statsStatusListeners.add(listener);
        }
        return (I) this;
    }

    protected void notifyStatsListeners(TName name, SpeedStats stats,
            String rejectionMessage) {
        if (statsStatusListeners != null) {
            for (StatsProgressionStatusListener l : statsStatusListeners) {
                l.acceptStatsProgressionStatus(name, stats, rejectionMessage);
            }
        }
    }

    protected PerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    protected void assertPerformanceExecutorNotNull() {
        if (performanceTimer == null) {
            throw new IllegalStateException(getClass().getCanonicalName() +
                    ": an instrumentable class must be provided with instrument()");
        }
    }

}
