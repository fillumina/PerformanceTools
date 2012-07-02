package com.fillumina.performance.examples;

import com.fillumina.performance.timer.PerformanceTimer;
import com.fillumina.performance.timer.PerformanceTimerBuilder;
import com.fillumina.performance.sequence.ProgressionSequence;

/**
 *
 * @author fra
 */
public class NullCheckAgainstNpeApp {
    private static final Object TEST = new Object();
    public static void main(final String[] args) {
        new NullCheckAgainstNpeApp().test();
    }

    private static abstract class AlternateRunnable implements Runnable {
        private int value = 0;

        protected Object getAlternateNUll() {
            value++;
            if (value % 2 == 0) {
                return null;
            }
            return this;
        }
    }

    public void test() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("instanceof", new AlternateRunnable() {

            @Override
            public void run() {
                final Object obj = getAlternateNUll();
                final String c;
                if (obj instanceof Object) {
                    c = obj.toString();
                } else {
                    c = null;
                }
                // just to make sure c is calculated
                if (c == TEST) {
                    throw new RuntimeException();
                }
            }
        });

        pt.addTest("nullcheck", new AlternateRunnable() {

            @Override
            public void run() {
                final Object obj = getAlternateNUll();
                final String c;
                if (obj != null) {
                    c = obj.toString();
                } else {
                    c = null;
                }
                // just to make sure c is calculated
                if (c == TEST) {
                    throw new RuntimeException();
                }
            }
        });

        pt.addTest("trycatch", new AlternateRunnable() {

            @Override
            public void run() {
                final Object obj = getAlternateNUll();
                String c;
                try {
                    c = obj.toString();
                } catch (NullPointerException e) {
                    c = null;
                }
                // just to make sure c is calculated
                if (c == TEST) {
                    throw new RuntimeException();
                }
            }

        });

        // just about the same (instanceof slightly faster ~ 10%)
        new ProgressionSequence(pt).serie(10_000, 3, 10);
    }

}