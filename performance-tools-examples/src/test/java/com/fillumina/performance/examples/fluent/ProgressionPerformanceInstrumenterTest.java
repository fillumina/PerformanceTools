package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.instrumenter.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Ratio;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterTest {

    private PrintOut printOut = new PrintOut();

    private int age = 25;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(final String[] args) throws NoSuchMethodException {
        final ProgressionPerformanceInstrumenterTest test =
                new ProgressionPerformanceInstrumenterTest();
        test.printOut = new PrintOut(true);
        test.test();
    }

    @Test
    public void shouldCallGetBeFasterThanCallingSet()
            throws NoSuchMethodException {
        test();
    }

    public void test()
            throws NoSuchMethodException, SecurityException {
        final Class<?> clazz = ProgressionPerformanceInstrumenterTest.class;
        final Method getter = clazz.getMethod("getAge", new Class[]{});
        final Method setter = clazz.getMethod("setAge", new Class[]{int.class});

        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt
            .addPerformanceConsumerIf(printOut.isPrintOut(),
                        SampleLineStringGenerator.VIEWER)
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setIterationProgression(1_000, 10_000, 100_000)
                .setSamples(100)
                .build())

            .addTest("getter", new Testable() {
                ProgressionPerformanceInstrumenterTest bean =
                        new ProgressionPerformanceInstrumenterTest();

                @Override
                public void test() {
                    final int result;
                    try {
                        result = (int) getter.invoke(bean);
                    } catch (IllegalAccessException |
                            IllegalArgumentException |
                            InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                    assertEquals(25, result);
                    bean.setAge(25);
                }
            })

            .addTest("setter", new Testable() {
                ProgressionPerformanceInstrumenterTest bean =
                        new ProgressionPerformanceInstrumenterTest();

                @Override
                public void test() {
                    try {
                        setter.invoke(bean, 30);
                    } catch (IllegalAccessException |
                            IllegalArgumentException |
                            InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                    assertEquals(30, bean.getAge());
                    bean.setAge(25);
                }
            })

            .addPerformanceConsumerIf(printOut.isPrintOut(),
                    WrapperSpeedStatsTableStringGenerator.VIEWER)

            .execute()

            .use(AssertSpeed.withTolerance(Ratio.percentage(10))
                .assertPercentage("getter").lessThan(90));
    }
}
