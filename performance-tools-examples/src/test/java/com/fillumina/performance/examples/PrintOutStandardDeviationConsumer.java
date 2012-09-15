package com.fillumina.performance.examples;

import com.fillumina.performance.producer.progression.StandardDeviationConsumer;

/**
 *
 * @author fra
 */
public class PrintOutStandardDeviationConsumer
        implements StandardDeviationConsumer {

    public static final PrintOutStandardDeviationConsumer INSTANCE
            = new PrintOutStandardDeviationConsumer();

    private PrintOutStandardDeviationConsumer() {}

    @Override
    public void consume(final long iterations,
            final long samples, final double stdDev) {
        System.out.println(new StringBuilder()
                .append("Iterations: ").append(iterations)
                .append("\tSamples: ").append(samples)
                .append("\tStandard Deviation: ").append(stdDev)
                .toString());
    }
}
