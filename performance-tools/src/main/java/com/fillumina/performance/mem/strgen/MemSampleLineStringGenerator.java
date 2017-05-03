package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.sample.MemSample;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleLineStringGenerator
        implements StringGenerator<MemSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemSampleLineStringGenerator INSTANCE =
            new MemSampleLineStringGenerator();

    protected MemSampleLineStringGenerator() {}

    @Override
    public void toString(Appendable appendable, MemSample memSample)
            throws IOException {
        appendable
            .append(memSample.getTestName().toString())
            .append(": ")
            .append("" + memSample.getBytes())
            .append(" bytes");
    }
}
