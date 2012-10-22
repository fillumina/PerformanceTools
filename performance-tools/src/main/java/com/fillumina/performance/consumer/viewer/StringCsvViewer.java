package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringCsvViewer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LoopPerformances loopPerformances;

    public static final PerformanceConsumer CONSUMER = new PerformanceConsumer() {

        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            new StringCsvViewer(loopPerformances)
                    .toCsvString()
                    .println();
        }
    };

    public StringCsvViewer(final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
    }

    public StringOutputHolder toCsvString() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%d, ", loopPerformances.getIterations()));
        appendCsvString(buf, loopPerformances.getPercentageList());
        return new StringOutputHolder(buf.toString());
    }

    private static void appendCsvString(
            final StringBuilder buf,
            final Collection<Float> values) {
        boolean first = true;
        for (final float d: values) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(String.format("%.2f", d));
        }
    }

    @Override
    public String toString() {
        return toCsvString().toString();
    }
}
