package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenterTest {
    public static final int SAMPLES = 10;
    public static final int UNSTABLE_ITERATIONS = SAMPLES * 1_000;
    public static final int TOTAL_ITERATIONS = SAMPLES * (1_000 + 10_000 /*+ 100_000*/);

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(StringCsvViewer.CONSUMER);
    }

    @Test
    public void shouldIterateThreeTimes() {
        final int actualIterations = iterate(NullPerformanceConsumer.INSTANCE);

        assertEquals( TOTAL_ITERATIONS, actualIterations);
    }

    private int iterate(final PerformanceConsumer consumer) {
        final AtomicInteger iterations = new AtomicInteger();

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("first", new Runnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        if (++counter < UNSTABLE_ITERATIONS) {
                            sleepMicroseconds(
                                    Math.round(counter * 10F / UNSTABLE_ITERATIONS));
                        } else {
                            sleepMicroseconds(5);
                        }
                    }
                })

                .addTest("second", new Runnable() {

                    @Override
                    public void run() {
                        iterations.incrementAndGet();
                        sleepMicroseconds(10);
                    }
                })

                .addPerformanceConsumer(consumer)
                //.addPerformanceConsumer(StringCsvViewer.CONSUMER)

                .instrumentedBy(new AutoProgressionPerformanceInstrumenter.Builder())
                        .setTimeout(20, TimeUnit.SECONDS)
                        .setMaxStandardDeviation(4)
                        .build()
                        .executeSequence();

                 return iterations.get();
    }
}
