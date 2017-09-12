package com.fillumina.performance.infrastructure.sample.strgen;

import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.util.AppendableUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCsvStringGenerator
        implements AssertableStringGenerator
                        <AbstractSample<?,? extends SampleValue,?>>,
                   Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleCsvStringGenerator INSTANCE =
            new SampleCsvStringGenerator();

    protected SampleCsvStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable,
            AbstractSample<?,? extends SampleValue,?> sample)
            throws IOException {
        AppendableUtil.append(appendable, ", ",
                sample.getValuesMap().values().stream()
                        .map(s -> s.toCsv())
                        .collect(Collectors.toList()));
    }
}
