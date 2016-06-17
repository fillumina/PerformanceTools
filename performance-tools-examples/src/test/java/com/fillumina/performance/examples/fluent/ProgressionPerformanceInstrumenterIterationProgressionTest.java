package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterIterationProgressionTest {

    private int age = 25;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(final String[] args) throws NoSuchMethodException {
        new ProgressionPerformanceInstrumenterIterationProgressionTest()
                .test(SampleCsvStringGenerator.VIEWER, SpeedTableStringGenerator.VIEWER);
    }

    @Test
    public void shouldCallGetBeFasterThanCallingSet()
            throws NoSuchMethodException {
        test(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
    }

    public void test(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer)
            throws NoSuchMethodException, SecurityException {
        final Class<?> clazz = ProgressionPerformanceInstrumenterIterationProgressionTest.class;
        final Method getter = clazz.getMethod("getAge", new Class[]{});
        final Method setter = clazz.getMethod("setAge", new Class[]{int.class});

        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt
            .addPerformanceConsumer(iterationConsumer)
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setTimeout(30, TimeUnit.SECONDS)
                .setIterationProgression(1_000, 10_000, 100_000)
                .setSamples(100)
                .build())

            .addTest("getter", new AbstractTestable() {
                ProgressionPerformanceInstrumenterIterationProgressionTest bean =
                        new ProgressionPerformanceInstrumenterIterationProgressionTest();

                @Override
                public Object test() {
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
                    return null;
                }
            })

            .addTest("setter", new AbstractTestable() {
                ProgressionPerformanceInstrumenterIterationProgressionTest bean =
                        new ProgressionPerformanceInstrumenterIterationProgressionTest();

                @Override
                public Object test() {
                    try {
                        setter.invoke(bean, 30);
                    } catch (IllegalAccessException |
                            IllegalArgumentException |
                            InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                    assertEquals(30, bean.getAge());
                    bean.setAge(25);
                    return null;
                }
            })

            .addPerformanceConsumer(resultConsumer)

            .execute()

            .use(AssertSpeed.withTolerance(10)
                .assertPercentage("getter").lessThan(90));
    }
}
