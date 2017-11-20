package com.fillumina.performance.executor.sample.strgen;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.AppendableUtil;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCsvStringGenerator
        implements StringGenerator<Sample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final SampleCsvStringGenerator INSTANCE =
            new SampleCsvStringGenerator();

    public static final Viewer<Sample> VIEWER = new Viewer<>(INSTANCE);

    protected SampleCsvStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, Sample sample)
            throws IOException {
        AppendableUtil.append(appendable, ", ",
                sample.getValuesMap().values().stream()
                        .map(s -> s.toCsv())
                        .collect(Collectors.toList()));
    }

}
