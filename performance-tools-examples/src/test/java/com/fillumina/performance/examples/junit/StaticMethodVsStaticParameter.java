package com.fillumina.performance.examples.junit;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.util.junit.AutoProgressionPerformanceBuilder;
import com.fillumina.performance.util.junit.JUnitAutoProgressionPerformanceTemplate;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class StaticMethodVsStaticParameter
        extends JUnitAutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new StaticMethodVsStaticParameter().testWithOutput();
    }

    @Override
    public void init(AutoProgressionPerformanceBuilder config) {
    }

    private static class Bean {
        private final boolean b = false;

        public boolean useParameterMethod() {
            return b;
        }

        public boolean useStaticMethod() {
            return false;
        }
    }

    @Override
    public void addTests(InstrumentablePerformanceExecutor<?> pe) {
        final Bean b = new Bean();

        pe.addTest("static parameter", new Runnable() {

            @Override
            public void run() {
                final boolean result = b.useParameterMethod();
                assertFalse(result);
            }
        });

        pe.addTest("static method", new Runnable() {

            @Override
            public void run() {
                final boolean result = b.useStaticMethod();
                assertFalse(result);
            }
        });
    }

    @Override
    public void addAssertions(AssertPerformance ap) {
        //
    }

}
