package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.NullPerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
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
                .test(StringCsvSampleViewer.INSTANCE, StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void shouldCallGetBeFasterThanCallingSet()
            throws NoSuchMethodException {
        test(NullPerformanceSampleConsumer.INSTANCE, NullPerformanceStatsConsumer.INSTANCE);
    }

    public void test(final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer)
            throws NoSuchMethodException, SecurityException {
        final Class<?> clazz = ProgressionPerformanceInstrumenterIterationProgressionTest.class;
        final Method getter = clazz.getMethod("getAge", new Class[]{});
        final Method setter = clazz.getMethod("setAge", new Class[]{int.class});

        final PerformanceTimer<Testable> pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt.addTest("getter", new AbstractTestable() {
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
        });

        pt.addTest("setter", new AbstractTestable() {
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
        });

        pt
            .addPerformanceSampleConsumer(iterationConsumer)
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                .setTimeout(30, TimeUnit.SECONDS)
                .setIterationProgression(1_000, 10_000, 100_000)
                .setSamplesPerStep(100)
                .build())
            .addPerformanceConsumer(resultConsumer)
            .addPerformanceConsumer(AssertPerformance.withTolerance(10)
                .assertPercentage("getter").lessThan(90F))
            .execute();
    }
}
