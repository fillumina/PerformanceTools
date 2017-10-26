package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSampleProducerInstrumenter
            <I extends AbstractSampleProducerInstrumenter<I,S,A>,
             S extends Stats<?>,
             A extends AbstractSample<A,?,S>>
        extends AbstractStatsProducer<I, S>
        implements Instrumenter<SampleProducer<?,A>> {

    private List<SampleProgressionStatusListener> sampleStatusListeners;
    private List<StatsProgressionStatusListener> statsStatusListeners;
    private SampleProducer<?,A> sampleProducer;

    public AbstractSampleProducerInstrumenter() {
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

    protected void notifySampleListeners(SampleProgressionStatus status) {
        if (sampleStatusListeners != null) {
            for (SampleProgressionStatusListener l : sampleStatusListeners) {
                l.acceptSampleProgressionStatus(status);
            }
        }
    }

    protected void notifyStatsListeners(TName name,
            Collection<? extends Stats<?>> stats,
            String rejectionMessage) {
        if (statsStatusListeners != null) {
            for (StatsProgressionStatusListener l : statsStatusListeners) {
                l.acceptStatsProgressionStatus(name, stats, rejectionMessage);
            }
        }
    }

    protected SampleProducer<?,A> getSampleProducer() {
        return sampleProducer;
    }

    protected void assertPerformanceExecutorNotNull() {
        if (sampleProducer == null) {
            throw new IllegalStateException(getClass().getCanonicalName() +
                    ": an instrumentable class must be provided with instrument()");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I instrument(SampleProducer<?,A> instrumentable) {
        this.sampleProducer = instrumentable;
        return (I) this;
    }
}
