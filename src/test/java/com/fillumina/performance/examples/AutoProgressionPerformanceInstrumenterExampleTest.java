package com.fillumina.performance.examples;

import com.fillumina.performance.producer.instrumenter.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.util.JUnitPerformanceTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fra
 */
@RunWith(JUnitPerformanceTestRunner.class)
public class AutoProgressionPerformanceInstrumenterExampleTest {
    private final static int MAX = 10;
    private final static int[] REFERENCE =
            new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterExampleTest()
                .test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    @Test
    public void boundaryCheckAgainstOOBExceptionTest() {
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    private void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        PerformanceTimerBuilder
            .createSingleThread()

            .addTest("boundary check", new Runnable() {
                private int counter = 0;
                private int[] array = new int[MAX];

                @Override
                public void run() {
                    if (counter < MAX) {
                        array[counter] = counter;
                        counter++;
                    } else {
                        assertArrayEquals(REFERENCE, array);
                        counter = 0;
                    }
                }
            })

            .addTest("exception", new Runnable() {
                private int counter = 0;
                private int[] array = new int[MAX];

                @Override
                public void run() {
                    try {
                        array[counter] = counter;
                        counter++;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        assertArrayEquals(REFERENCE, array);
                        counter = 0;
                    }
                }
            })

            .addPerformanceConsumer(iterationConsumer)

            .instrumentedBy(new AutoProgressionPerformanceInstrumenter.Builder())
                    //.setTimeout(1, TimeUnit.DAYS) // to ease debugging
                    .setMaxStandardDeviation(1.4)
                    .build()
                    .addPerformanceConsumer(resultConsumer)
                    .executeSequence()
                    .use(AssertPerformance.withTolerance(5F)
                        .assertTest("boundary check").equalsTo("exception"));
    }

}