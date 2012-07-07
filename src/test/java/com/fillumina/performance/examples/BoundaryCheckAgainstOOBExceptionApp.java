package com.fillumina.performance.examples;

import com.fillumina.performance.producer.instrumenter.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class BoundaryCheckAgainstOOBExceptionApp {
    private final static int MAX = 10;
    private final static int[] REFERENCE =
            new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static void main(final String[] args) {
        new BoundaryCheckAgainstOOBExceptionApp().test();
    }

    public void test() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("boundary check", new Runnable() {
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
        });

        pt.addTest("exception", new Runnable() {
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
        });

        AutoProgressionPerformanceInstrumenter sequence = new AutoProgressionPerformanceInstrumenter(pt);
        sequence.executeAutoSequence();
    }

}
