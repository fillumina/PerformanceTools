package com.fillumina.performance.util;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NullAppendable implements Appendable, Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final Appendable INSTANCE = new NullAppendable();

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws
            IOException {
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        return this;
    }
}
