package com.fillumina.performance.util.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Use this class if you want to run the performance tests only
 * if the parameter "RunPerformanceTests" is defined in the
 * maven command line.
 * (i.e. <code>mvn clean install -DRunPerformanceTests</code>).
 * <br />
 * That would allow to have a fast compilation time during developing
 * while still having the choice to execute performance tests when needed.
 * To use this class in JUnit tests just specify this class in the @RunWith
 * class annotation:
 * <code><pre>
 *   &#64;RunWith(JUnitPerformanceTestRunner.class)
 *   public class SomeKindOfClassPerformanceTest {
 *
 *       &#64;Test
 *       public void shouldThisBeFasterThanThat() {}
 *   }
 * </code></pre>
 * @author fra
 */
public class JUnitPerformanceTestRunner extends BlockJUnit4ClassRunner {
    public static final String VARNAME = "RunPerformanceTests";

    public JUnitPerformanceTestRunner(final Class<?> klass)
            throws InitializationError {
        super(klass);
    }

    /**
     * Run the test <b>only if</b> the global variable
     * <i>RunPerformanceTest</i> is defined.
     * (use <code>-DRunPerformanceTest</code> in the java command line).
     */
    @Override
    public void run(RunNotifier notifier) {
        if (System.getProperty(VARNAME) != null) {
            super.run(notifier);
        }
    }
}
