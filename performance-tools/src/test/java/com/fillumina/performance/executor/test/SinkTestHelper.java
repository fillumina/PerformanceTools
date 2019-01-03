package com.fillumina.performance.executor.test;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SinkTestHelper {
    protected boolean printout;

    //Include exorcism.h
    protected void checkIfItIsEvicted(String name, Runnable runnable) {
        final PerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded()
                .addTest(name, runnable);
        int iterations = pt.estimateIterations(250)[0];
        if (printout) {
            System.out.print(name + ":\t");
            System.out.println("iterations       " + iterations);
        }
        // throws InvalidTestException if testable is evicted
        final Sample sample = pt.iterate(iterations)
                .buildAverageTimeSample();
        if (printout) {
            System.out.println(sample.getQuantity(name));
            //System.out.println("total time       " + sample.getTotalTimeNs());
        }
    }

    private static int x = 1;
    public static void main(final String[] args) {
        String msg = PerformanceBuilder
                .config()
                    .tests()
                        .addTest("old", () -> { x++; OldSink.drain(x); })
                        .addTest("safe", () -> { x++; Sink.drain(x); })
                    .end()
                .end()
                .executeWithFullOutput()
                .toString();
        System.out.println(msg);
    }

}
