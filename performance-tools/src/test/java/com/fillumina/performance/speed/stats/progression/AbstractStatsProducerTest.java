package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractStatsProducerTest {

    private static class StatsProducerImpl
            extends  AbstractStatsProducer<StatsProducerImpl> {

        @Override
        public PHolder<SpeedStats> execute() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private final StatsProducerImpl statsProducer = new StatsProducerImpl();

    @Test
    public void shouldSetPerformanceTimer() {
        PerformanceTimer performanceTimer =
                PerformanceTimerFactory.createSingleThreaded();

        statsProducer.instrument(performanceTimer);

        assertEquals(performanceTimer, statsProducer.getPerformanceTimer());
    }

    private static class SampleProgressionStatusListenerImpl
            implements SampleProgressionStatusListener {

        private SampleProgressionStatus progressionStatus;

        @Override
        public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
            this.progressionStatus = status;
        }
    }

    @Test
    public void shouldNotifySampleProgressionStatusListeners() {
        SampleProgressionStatusListenerImpl listener =
                new SampleProgressionStatusListenerImpl();

        statsProducer.addSampleProgressionListener(listener);

        SampleProgressionStatus status = new SampleProgressionStatus(
                "", 0, 0, 0, new int[]{0}, null, null, 0, null);

        statsProducer.notifySampleListeners(status);

        assertEquals(status, listener.progressionStatus);
    }

    private static class StatsProgressionStatusListenerImpl
            implements StatsProgressionStatusListener {

        private TName tname;
        private SpeedStats stats;
        private String rejectionMessage;

        @Override
        public void acceptStatsProgressionStatus(TName name, SpeedStats stats,
                String rejectionMessage) {
            this.tname = name;
            this.stats = stats;
            this.rejectionMessage = rejectionMessage;
        }
    }

    @Test
    public void shouldNotifyStatsProgressionStatusListener() {
        StatsProgressionStatusListenerImpl listener =
                new StatsProgressionStatusListenerImpl();

        statsProducer.addStatsProgressionListener(listener);

        TName name = TN.tname("name");
        SpeedStats speedStats = SpeedStatsMock.builder()
                .addTest("test").samples(10).timeNs(1000).stdev(2).endTest()
                .buildWithCoincidentalValues();
        String message = "rejected";

        statsProducer.notifyStatsListeners(name, speedStats, message);

        assertEquals(name, listener.tname);
        assertEquals(message, listener.rejectionMessage);
        assertEquals(speedStats, listener.stats);
    }
}
