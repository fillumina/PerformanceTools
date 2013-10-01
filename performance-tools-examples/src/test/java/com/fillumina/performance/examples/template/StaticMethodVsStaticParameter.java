package com.fillumina.performance.examples.template;

import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.TestsContainer;
import com.fillumina.performance.template.ProgressionConfigurator;
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
    public void init(ProgressionConfigurator config) {
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
    public void addTests(TestsContainer<?> pe) {
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
    public void addAssertions(PerformanceAssertion ap) {
        //
    }

}
