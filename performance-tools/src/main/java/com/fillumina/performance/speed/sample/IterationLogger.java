package com.fillumina.performance.speed.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationLogger {
    private final double[][] log;
    private String name;
    private int index;

    public IterationLogger(String testName, int lines) {
        this.name = testName;
        this.log = new double[lines][4];
    }

    void log(int iterations, double desired, long time, double ratio) {
        log[index][0] = iterations;
        log[index][1] = desired;
        log[index][2] = time;
        log[index][3] = ratio;
    }

    public String getMessage() {
        return "test '" + name + "' has been probably " +
                "evicted by JVM optimizations and cannot be tested." +
                System.lineSeparator() + toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("iteration estimator debug info:")
                .append(System.lineSeparator());
        for (int i=0; i<log.length; i++) {
            int iterations = (int) log[i][0];
            if (iterations == 0) {
                break;
            }
            double desired = log[i][1];
            long time = (long) log[i][2];
            double ratio = log[i][3];

            buf.append("iterations=").append(iterations);
            buf.append("\tdesiredTime(ns)=").append(desired);
            buf.append("\ttime(ns)=").append(time);
            buf.append("\tratio=").append(ratio);
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }
}
