package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.annotation.SetUp;
import com.fillumina.performance.executor.annotation.TearDown;
import com.fillumina.performance.util.instrument.Instrumenter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducerTest {

    public abstract AbstractStatsProducer<?,?> createStatsProducer();

    private static class StatsProducerInstrumenter
            implements Instrumenter<StatsProducer<?, ?>> {

        private StatsProducer<?, ?> producer;

        @Override
        public Instrumenter<StatsProducer<?, ?>> instrument(
                StatsProducer<?, ?> instrumentable) {
            this.producer = instrumentable;
            return this;
        }

        public MixedAssertableHolder getStatsFromProducer() {
            return producer.execute();
        }
    }

    @Test
    public void shouldBeInstrumentedBy() {
        StatsProducerInstrumenter instrumenter = new StatsProducerInstrumenter();
        createStatsProducer().instrumentedBy(instrumenter);

        assertEquals(createStatsProducer().execute(),
                instrumenter.getStatsFromProducer());
    }

    private static class AnnotatedTest implements Runnable {
        private boolean setup;
        private boolean run;
        private boolean teardown;

        @SetUp
        public void setup() {
            setup = true;
        }

        @Override
        public void run() {
            run = true;
        }

        @TearDown
        public void teardown() {
            teardown = true;
        }
    }

    @Test
    public void shouldEventsBeCalled() {
        AbstractStatsProducer<?,?> producer = createStatsProducer();
        AnnotatedTest test = new AnnotatedTest();
        producer.addTest(test);
        producer.execute();

        assertTrue(test.setup);
        assertTrue(test.run);
        assertTrue(test.teardown);
    }
}
