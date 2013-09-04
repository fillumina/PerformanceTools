package com.fillumina.performance.producer.suite;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
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

        final PerformanceTimer pt = PerformanceTimerBuilder
                .createSingleThreaded();

        pt.addTest("OLD", new Runnable() {
            @Override
            public void run() {
                oldTest.set(true);
            }
        });

        pt.setIterations(1_000);

        if (printout) {
            pt.addPerformanceConsumer(StringTableViewer.CONSUMER);
        }

        pt.instrumentedBy(new ParametrizedPerformanceSuite<>()
                .addObjectToTest("one", 1)
                .addObjectToTest("two", 2))
            .executeTest(new ParametrizedRunnable<Object>() {
                @Override
                public void call(final Object param) {
                    newTest.set(true);
                }
        });

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
