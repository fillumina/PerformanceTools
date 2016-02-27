package com.fillumina.performance.producer.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.timer.AbstractTestable;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * It isn't common to run parametrized tests along a simple (non instrumented)
 * one but it isn't at all forbidden and it may prove useful.
 *
 * @author Francesco Illuminati
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
        pt.addTest("simple", new AbstractTestable() {
            @Override
            public Object test() {
                oldTest.set(true);
                return null;
            }
        });

        pt.setIterations(1_000);

        // this is the parametrized test
        pt.instrumentedBy(new ParametrizedPerformanceSuite<>()
                .addParameter("one", 1)
                .addParameter("two", 2))
            .addPerformanceConsumer(printout ? StringTableViewer.INSTANCE : null)
            .executeTest("parametrized", new ParametrizedTestable<Object>() {
                @Override
                public Object test(final Object param) {
                    newTest.set(true);
                    return null;
                }
            });

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
