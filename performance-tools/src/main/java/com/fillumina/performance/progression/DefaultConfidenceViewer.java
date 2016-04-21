package com.fillumina.performance.progression;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati
 */
public class DefaultConfidenceViewer
            implements ConfidenceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final DefaultConfidenceViewer INSTANCE =
            new DefaultConfidenceViewer();

    private final Appendable appendable;

    /** Print out on standard output. */
    private DefaultConfidenceViewer() {
        this(System.out);
    }

    public DefaultConfidenceViewer(final Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public void consume(final long iterations,
            final long samples, final double confidence) {
        try {
            appendable
                    .append("Iterations: ")
                    .append(String.valueOf(iterations))
                    .append("\tSamples: ")
                    .append(String.valueOf(samples))
                    .append("\tConfidence: ")
                    .append(String.valueOf(confidence))
                    .append("\n");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
