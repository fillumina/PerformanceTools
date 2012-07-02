package com.fillumina.performance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class GetAgainstSetPerformanceApp {

    private int age = 25;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(final String[] args) throws NoSuchMethodException {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        final Class<?> clazz = GetAgainstSetPerformanceApp.class;
        final Method getter = clazz.getMethod("getAge", new Class[]{});
        final Method setter = clazz.getMethod("setAge", new Class[]{int.class});

        pt.addTest("getter", new Runnable() {
            GetAgainstSetPerformanceApp bean =
                    new GetAgainstSetPerformanceApp();

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
            GetAgainstSetPerformanceApp bean =
                    new GetAgainstSetPerformanceApp();

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

        new ProgressionSequence(pt)
                .setTimeout(15, TimeUnit.SECONDS)
                .setOnIterationPerformanceConsumer(new StringCsvPresenter())
                .setFinalPerformanceConsumer(new StringTablePresenter())
                .serie(100_000, 3, 10);
    }
}
