package com.fillumina.performance;

/**
 *
 * @author fra
 */
public class InstanceOfPerformanceApp {

    public static void main(final String[] args) {
        final Object object = new InstanceOfPerformanceApp();
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("instanceof", new Runnable() {

            @Override
            public void run() {
                if (!(object instanceof InstanceOfPerformanceApp)) {
                    throw new RuntimeException();
                }

            }
        });

        // it's 67% FASTER on the long run!!
        pt.addTest("classcheck", new Runnable() {

            @Override
            public void run() {
                if (!Object.class.isAssignableFrom(InstanceOfPerformanceApp.class)) {
                    throw new RuntimeException();
                }

            }
        });

        new ProgressionSerie(pt).serie(10_000_000, 3, 10);
    }
}
