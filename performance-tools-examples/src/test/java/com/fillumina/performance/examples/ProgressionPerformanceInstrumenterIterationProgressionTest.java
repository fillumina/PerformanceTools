package com.fillumina.performance.examples;

import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
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
                .test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    @Test
    public void shouldCallGetBeFasterThanCallingSet()
            throws NoSuchMethodException {
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer)
            throws NoSuchMethodException, SecurityException {
        final Class<?> clazz = ProgressionPerformanceInstrumenterIterationProgressionTest.class;
        final Method getter = clazz.getMethod("getAge", new Class[]{});
        final Method setter = clazz.getMethod("setAge", new Class[]{int.class});

        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("getter", new Runnable() {
            ProgressionPerformanceInstrumenterIterationProgressionTest bean =
                    new ProgressionPerformanceInstrumenterIterationProgressionTest();

            @Override
            public void run() {
                final int result;
                try {
                    result = (int) getter.invoke(bean);
                } catch (IllegalAccessException |
                        IllegalArgumentException |
                        InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
                assertEquals(25, result);
                bean.setAge(bean.getAge());
            }
        });

        pt.addTest("setter", new Runnable() {
            ProgressionPerformanceInstrumenterIterationProgressionTest bean =
                    new ProgressionPerformanceInstrumenterIterationProgressionTest();

            @Override
            public void run() {
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
        });

        pt
            .addPerformanceConsumer(iterationConsumer)
            .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
            .setTimeout(15, TimeUnit.SECONDS)
            .setIterationProgression(10_000, 100_000, 1_000_000)
            .setSamplesPerMagnitude(10)
            .build()
            .addPerformanceConsumer(resultConsumer)
            .addPerformanceConsumer(new AssertPerformance()
                .assertPercentageFor("getter").lessThan(90F))
            .execute();
    }
}
