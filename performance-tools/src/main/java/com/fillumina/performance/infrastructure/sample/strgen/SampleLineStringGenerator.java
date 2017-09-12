package com.fillumina.performance.infrastructure.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.util.AppendableUtil;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleLineStringGenerator
        implements AssertableStringGenerator<AbstractSample<?,?,?>>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleLineStringGenerator INSTANCE =
            new SampleLineStringGenerator();

    protected SampleLineStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, AbstractSample<?,?,?> sample)
            throws IOException {
        AppendableUtil.append(appendable, ", ", sample.getValuesMap());
    }
}
