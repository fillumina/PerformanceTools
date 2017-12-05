package com.fillumina.performance.executor.sample.strgen;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTableStringGenerator
        implements StringGenerator<Sample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleTableStringGenerator INSTANCE =
            new SampleTableStringGenerator();

    public static final Viewer<Sample> VIEWER = new Viewer<>(INSTANCE);

    protected SampleTableStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, Sample sample)
            throws IOException {
        appendable.append(sample.toString());
    }
}
