package com.fillumina.performance.producer.progression;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class StandardDeviationViewer
            implements StandardDeviationConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final StandardDeviationViewer INSTANCE =
            new StandardDeviationViewer();

    private final Appendable appendable;

    /** Print out on standard output. */
    private StandardDeviationViewer() {
        this(System.out);
    }

    public StandardDeviationViewer(final Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public void consume(final long iterations,
            final long samples, final double stdDev) {
        try {
            appendable
                    .append("Iterations: ")
                    .append(String.valueOf(iterations))
                    .append("\tSamples: ")
                    .append(String.valueOf(samples))
                    .append("\tStandard Deviation: ")
                    .append(String.valueOf(stdDev))
                    .append("\n");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
