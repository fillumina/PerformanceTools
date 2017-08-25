package com.fillumina.performance.mem.strgen;

import com.fillumina.performance.infrastructure.AssertableStringGenerator;
import com.fillumina.performance.mem.sample.UsedMemSample;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleLineStringGenerator
        implements AssertableStringGenerator<UsedMemSample>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final MemSampleLineStringGenerator INSTANCE =
            new MemSampleLineStringGenerator();

    protected MemSampleLineStringGenerator() {}

    @Override
    public void appendTo(Appendable appendable, UsedMemSample memSample)
            throws IOException {
        appendable
            .append(memSample.getTestName().toString())
            .append(": ")
            .append("" + memSample.getBytes())
            .append(" bytes");
    }
}
