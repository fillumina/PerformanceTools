package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.infrastructure.RndRunnable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.speed.sample.iterator.SingleThreadPerformanceExecutor;
import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.OnlineMeasure;
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
        Runnable t1 = new RndRunnable();
        Runnable t2 = new RndRunnable();
        LinkedMap<TName,Runnable> tests = new LinkedMap<>();
        tests.put(TN.tname("one"), t1);
        tests.put(TN.tname("two"), t2);

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
                .addTest("one", new RndRunnable())
                .iterate(500_000)
                .getTotalTimeNs());
        }
        System.out.println("m=" + m);
    }

//    private final Testable t1 = new TimeTestable(4);
    private final Runnable t1 =
            new Runnable() {
                private final Lfsr lfsr = new Lfsr();

                @Override
                public void run() {
                    Sink.drain(lfsr.next());
                }
            };

//    private final Testable t2 = new TimeTestable(8);
    private final Runnable t2 =
            new Runnable() {
                private final Lfsr lfsr = new Lfsr();

                @Override
                public void run() {
                    Sink.drain(lfsr.next());
                    Sink.drain(lfsr.next());
                }
            };

    @Override
    public void addAssertions(MixedAssertion assertions) {
        assertions.speed()
                .assertPercentage("single").sameAs(50);
    }

    @Override
    public void config(Configuration config) {
        config.speedTestOnly()
                .setMillisecondsPerSample(500)
                .setSamples(66);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("single", t1);
        tests.addTest("double", t2);
    }

}
