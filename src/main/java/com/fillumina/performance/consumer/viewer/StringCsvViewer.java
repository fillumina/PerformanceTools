package com.fillumina.performance.consumer.viewer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.util.StringOutputHolder;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author fra
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
        buf.append(toCsvString(loopPerformances.getPercentageList()));
        return new StringOutputHolder(buf.toString());
    }

    private static String toCsvString(final Collection<Float> values) {
        StringBuilder buf = new StringBuilder();
        for (float d: values) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append(String.format("%.2f", d));
        }
        return buf.toString();
    }
}
