package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.TimeSample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SinkTestHelper {
    protected boolean printout;

    //Include exorcism.h
    protected void checkIfItIsEvicted(String name, Runnable testable) {
        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded().
                addTest(name, testable);
        int iterations = pt.estimateIterations(250)[0];
        if (printout) {
            System.out.print(name + ":\t");
            System.out.println("iterations       " + iterations);
        }
        // throws InvalidTestException if executeWithoutOutput is evicted
        final TimeSample sample = pt.iterate(iterations);
        if (printout) {
            System.out.println(sample.getMeasure(name).getMean());
            System.out.println("total time       " + sample.getTotalTimeNs());
        }
    }

    private static int x = 1;
    public static void main(final String[] args) {
        String msg = PerformanceBuilder
                .config()
                    .tests()
                        .addTest("old", () -> { x++; OldSink.drain(x); })
                        .addTest("fast", () -> { x++; FastSink.drain(x); })
                        .addTest("safe", () -> { x++; SafeSink.drain(x); })
                    .end()
                .end()
                .executeWithFullOutput()
                .toString();
        System.out.println(msg);
    }

}
