package com.fillumina.performance;

import com.fillumina.performance.SingleThreadPerformanceTestExecutor;
import com.fillumina.performance.ProgressionSerie;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class NullCheckAgainstIndirectionApp {

    private static abstract class AlternateRunnable implements Runnable {
        private int value = 0; // ++ it's circular so we don't mind overruns

        protected boolean getOccasionalFalse() {
            value++;
            if (value % 40 == 0) {
                return false;
            }
            return true;
        }
    }

    private static class Container {
        private final Long value;

        public Container(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    }

    public static void main(final String[] args) {
        new NullCheckAgainstIndirectionApp().test();
    }

    public void test() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("nullcheck", new AlternateRunnable() {
            private Long field = null;

            @Override
            public void run() {
                if (getOccasionalFalse()) {
                    assertNotNull(getField());
                } else {
                    reset();
                }
            }

            private Long getField() {
                if (field == null) {
                    field = System.currentTimeMillis();
                }
                return field;
            }

            private void reset() {
                field = null;
            }

        });

        pt.addTest("redirected", new AlternateRunnable() {
            private Container field = null;

            @Override
            public void run() {
                if (getOccasionalFalse()) {
                    assertNotNull(getField());
                } else {
                    reset();
                }
            }

            private Long getField() {
                try {
                    return field.getValue();
                } catch (NullPointerException e) {
                    field = new Container(System.currentTimeMillis());
                    return field.getValue();
                }
            }

            private void reset() {
                field = null;
            }

        });

        // it's really quite the same
        new ProgressionSerie(pt).printSerie(1_000_000, 3, 10);
    }

}
