package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.SampleProducerMock;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractSampleProducerInstrumenterTest {

    private static class AbstractSampleProducerInstrumenterImpl
            extends AbstractSampleProducerInstrumenter<
                                AbstractSampleProducerInstrumenterImpl> {

        @Override
        public MixedStatsHolder get() {
            return null;
        }
    }

    private AbstractSampleProducerInstrumenterImpl producer =
                new AbstractSampleProducerInstrumenterImpl();

    private static class SampleProgressionStatusListenerImpl
            implements SampleProgressionStatusListener {

        private SampleProgressionStatus status;

        @Override
        public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
            this.status = status;
        }
    }

    @Test
    public void shouldAddSampleProgressionListener() {
        SampleProgressionStatusListenerImpl listener =
                new SampleProgressionStatusListenerImpl();
        producer.addSampleProgressionListener(listener);

        SampleProgressionStatus status = new SampleProgressionStatus(0, 0, 0,
                UnmodifiableIntList.EMPTY, null, MixedStatsHolder.EMPTY, 0,
                "statusMessage");

        producer.notifySampleListeners(status);

        assertEquals(status, listener.status);
    }

    private static class StatsProgressionStatusListenerImpl
            implements StatsProgressionStatusListener {

        private PathName name;
        private Collection<? extends Stats> stats;
        private String statusMessage;

        @Override
        public void acceptStatsProgressionStatus(StatsProgressionStatus status) {
            this.name = status.getName();
            this.stats = status.getStats();
            this.statusMessage = status.getStatusMessage();
        }
    }

    @Test
    public void shouldAddStatsProgressionListener() {
        StatsProgressionStatusListenerImpl listener =
                new StatsProgressionStatusListenerImpl();
        producer.addStatsProgressionListener(listener);

        PathName name = PN.pname("name");
        String message = "status message";
        producer.notifyStatsListeners(
                new StatsProgressionStatus(name, null, message));

        assertEquals(name, listener.name);
        assertEquals(message, listener.statusMessage);
    }

    @Test
    public void shouldInstrument() {
        SampleProducerMock sampleProducer =
                new SampleProducerMock(Magnitude.UNIT);
        producer.instrument(sampleProducer);
        assertEquals(sampleProducer, producer.getSampleProducer());
    }

}
