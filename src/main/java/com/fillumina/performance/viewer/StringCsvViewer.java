package com.fillumina.performance.viewer;

import com.fillumina.performance.AbstractPerformanceConsumer;
import com.fillumina.performance.utils.StringOutputHolder;
import java.util.Collection;

/**
 *
 * @author fra
 */
public class StringCsvViewer
        extends AbstractPerformanceConsumer<StringCsvViewer> {
    private static final long serialVersionUID = 1L;

    public StringOutputHolder toCsvString() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%d, ", getLoopPerformances().getIterations()));
        buf.append(toCsvString(getLoopPerformances().getPercentageList()));
        return new StringOutputHolder(buf.toString());
    }

    @Override
    public void process() {
        toCsvString().println();
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
