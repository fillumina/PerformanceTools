package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class IterationLogger {
    private final double[][] log;
    private TName name;
    private int index;

    public IterationLogger(TName testName, int lines) {
        this.name = testName;
        this.log = new double[lines][5];
    }

    void log(int step, int iterations, double desired, long time, double ratio) {
        log[index][0] = step;
        log[index][1] = iterations;
        log[index][2] = desired;
        log[index][3] = time;
        log[index][4] = ratio;
        index++;
    }

    public String getMessage() {
        return "test '" + name +
                "' has been evicted by JVM optimizations and cannot be tested." +
                System.lineSeparator() + toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("### iteration estimator debug info:")
                .append(System.lineSeparator());
        for (int i=0; i<log.length; i++) {
            int iterations = (int) log[i][1];
            if (iterations == 0) {
                break;
            }
            int step = (int) log[i][0];
            double desired = log[i][2];
            long time = (long) log[i][3];
            double ratio = log[i][4];

            buf.append("step=").append(step);
            buf.append("\titerations=").append(iterations);
            buf.append("\tdesiredTime(ns)=").append(desired);
            buf.append("\ttime(ns)=").append(time);
            buf.append("\tratio=").append(ratio);
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }
}
