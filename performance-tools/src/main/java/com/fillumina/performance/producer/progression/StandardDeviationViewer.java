package com.fillumina.performance.producer.progression;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class StandardDeviationViewer
            implements StandardDeviationConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StandardDeviationViewer INSTANCE =
            new StandardDeviationViewer();

    private StandardDeviationViewer() {}

    @Override
    public void consume(final long iterations,
            final long samples, final double stdDev) {
        final String message = new StringBuilder()
                    .append("Iterations: ").append(iterations)
                    .append("\tSamples: ").append(samples)
                    .append("\tStandard Deviation: ").append(stdDev)
                    .toString();
        System.out.println(message);
    }
}
