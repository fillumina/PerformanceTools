package com.fillumina.performance.sample.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.viewer.StringTableSampleViewer;
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

        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded();

        // this test is executed along the parametrized one
        pt.addTest("simple", new AbstractTestable() {
            @Override
            public Object test() {
                oldTest.set(true);
                return null;
            }
        });

        // this is the parametrized test
        PerformanceSample sample =
                pt.instrumentedBy(new ParametrizedPerformanceSuite<Integer>()
                    .addParameter("one", 1)
                    .addParameter("two", 2))
                    .addTest("parametrized",
                            new ParametrizedTestable<Integer>() {
                        @Override
                        public Object test(final Integer param) {
                            newTest.set(true);
                            return null;
                        }
                    })
                    .execute(1_000);
        
        if (printout) {
            StringTableSampleViewer.INSTANCE.consume("test", sample);
        }

        assertTrue(oldTest.get());
        assertTrue(newTest.get());
    }
}
