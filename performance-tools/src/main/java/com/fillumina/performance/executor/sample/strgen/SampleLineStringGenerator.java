package com.fillumina.performance.executor.sample.strgen;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleLineStringGenerator
        implements StringGenerator<AbstractSample<?,?,?>>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleLineStringGenerator INSTANCE =
            new SampleLineStringGenerator();

    public static final Viewer<AbstractSample<?,?,?>> VIEWER =
            new Viewer<>(INSTANCE);

    protected SampleLineStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, AbstractSample<?,?,?> sample)
            throws IOException {
        boolean first = true;
        for (SampleValue v : sample.getValuesMap().values()) {
            if (first) {
                first = false;
            } else {
                appendable.append(", \t");
            }
            appendable.append(v.getName()).append("=");
            appendable.append(Double.toString(v.getValue()) );
        }
        //AppendableUtil.append(appendable, ", ", sample.getValuesMap().values());
    }
}
