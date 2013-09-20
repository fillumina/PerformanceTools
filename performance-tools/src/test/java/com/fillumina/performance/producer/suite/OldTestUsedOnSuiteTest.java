package com.fillumina.performance.producer.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * It isn't common to run parametrized tests along a simple (non instrumented)
 * one but it isn't at all forbidden and it may prove useful.
 *
 * @author fra
 */
public class OldTestUsedOnSuiteTest {

    private boolean printout = false;

    public static void main(final String[] args) {
        final OldTestUsedOnSuiteTest test = new OldTestUsedOnSuiteTest();
        test.printout = true;
        test.shouldExecuteTestAddedToPerformanceTimerToo();
    }


    @Test
    public void shouldExecuteTestAddedToPerformanceTimerToo() {
        final AtomicBoolean oldTest = new AtomicBoolean(false);
        final AtomicBoolean newTest = new AtomicBoolean(false);

        final PerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded();

        // this test is executed along the parametrized one
        pt.addTest("simple", new Runnable() {
            @Override
            public void run() {
                oldTest.set(true);
            }
        });

        pt.setIterations(1_000);

        // this is the parametrized test
        pt.instrumentedBy(new ParametrizedPerformanceSuite<>()
                .addParameter("one", 1)
                .addParameter("two", 2))
            .addPerformanceConsumer(printout ? StringTableViewer.INSTANCE : null)
            .executeTest("parametrized", new ParametrizedRunnable<Object>() {
                @Override
                public void call(final Object param) {
                    newTest.set(true);
                }
            });

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
