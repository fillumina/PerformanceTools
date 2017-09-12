package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.infrastructure.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // TODO substitute with
public abstract class AbstractStatsProducer<I extends AbstractStatsProducer<I>>
        extends AbstractStatsProducerInstrumenter<I, TimeStats> {

    private List<SampleProgressionStatusListener> sampleStatusListeners;
    private List<StatsProgressionStatusListener> statsStatusListeners;
    private PerformanceTimer performanceTimer;

    public AbstractStatsProducer() {
        super();
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

    protected void notifyStatsListeners(TName name,
            Collection<TimeStats> stats,
            String rejectionMessage) {
        if (statsStatusListeners != null) {
            for (StatsProgressionStatusListener l : statsStatusListeners) {
                l.acceptStatsProgressionStatus(name, stats, rejectionMessage);
            }
        }
    }

    protected void notifyWarmupListeners(TName testName, double speed) {
        if (statsStatusListeners != null) {
            for (StatsProgressionStatusListener l : statsStatusListeners) {
                l.acceptWarmupProgressionStatus(testName, speed);
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
