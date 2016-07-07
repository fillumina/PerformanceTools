package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TemplateSampleViewer
        implements PerformanceConsumer<PerformanceSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final TemplateSampleViewer INSTANCE =
            new TemplateSampleViewer();

    private TemplateSampleViewer() {}

    @Override
    public void consume(ComposedName name, PerformanceSample sample) {
        System.out.println(toString(sample));
    }

    public String toString(PerformanceSample sample) {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String,IterationTime> entry :
                sample.getTimeMap().entrySet()) {
            String testName = entry.getKey();
            IterationTime ti = entry.getValue();
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append('\'').append(testName).append("' ")
                    .append(ti.getTime()).append(" ns");
        }
        return buf.toString();
    }
}
