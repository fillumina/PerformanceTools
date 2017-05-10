package com.fillumina.performance.util;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableWrapperSentinel implements Appendable {
    private final Appendable delegate;
    private boolean modified;

    public AppendableWrapperSentinel(Appendable appendable) {
        this.delegate = appendable;
    }

    public boolean isModified() {
        return modified;
    }

    public void setUnmodified() {
        modified = false;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        modified = true;
        return delegate.append(csq);
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end)
            throws IOException {
        modified = true;
        return delegate.append(csq, start, end);
    }

    @Override
    public Appendable append(char c) throws IOException {
        modified = true;
        return delegate.append(c);
    }
}

