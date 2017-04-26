package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.RndTestable;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.speed.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.LinkedHashMap;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearCodeTimeTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        final LinearCodeTimeTest test = new LinearCodeTimeTest();
        test.shouldACodeExecutedTwiceTakeDoubleTheTime();
    }

    @Test
    public void shouldACodeExecutedTwiceTakeDoubleTheTime() {
        doTestWithPT();
        doTestWithSingleThread();
        executeWithFullOutput();
    }

    private static void doTestWithSingleThread() {
        PerformanceExecutor executor = new SingleThreadPerformanceExecutor(10);
        Testable t1 = new RndTestable();
        Testable t2 = new RndTestable();
        LinkedHashMap<String,Runnable> tests = new LinkedHashMap<>();
        tests.put("one", t1);
        tests.put("two", t2);

        OnlineMeasure m = new OnlineMeasure();
        for (int i=0; i<66; i++) {
            final SpeedSample sample =
                    executor.executeTests(tests, new int[] {100_000, 100_000});
            m.add(sample.getTotalTimeNs());
        }
        System.out.println("m=" + m);
    }

    private static void doTestWithPT() {
        OnlineMeasure m = new OnlineMeasure();
        for (int i=0; i<66; i++) {
            m.add(PerformanceTimerFactory.createSingleThreadedWithFractions(4)
                .addTest("one", new RndTestable())
                .iterate(500_000)
                .getTotalTimeNs());
        }
        System.out.println("m=" + m);
    }

//    private final Testable t1 = new TimeTestable(4);
    private final Testable t1 =
            new Testable() {
                private final Lfsr lfsr = new Lfsr();

                @Override
                public void run() {
                    drain(lfsr.next());
                }
            };

//    private final Testable t2 = new TimeTestable(8);
    private final Testable t2 =
            new Testable() {
                private final Lfsr lfsr = new Lfsr();

                @Override
                public void run() {
                    drain(lfsr.next());
                    drain(lfsr.next());
                }
            };

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
        assertions.speedWithTolerance(Ratio.percentage(5))
                .assertPercentage("single").sameAs(50);
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTestOnly()
                .setSampleTimeMillis(500)
                .setSamples(66);
    }

    @Override
    public void addTests(TestContainer<Runnable> tests) {
        tests.addTest("single", t1);
        tests.addTest("double", t2);
    }

}
