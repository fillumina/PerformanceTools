package com.fillumina.performance.util;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableWrapper implements Appendable {

    private final Appendable appendable;

    /** Uses a {@link NullAppendable} to output nothing. */
    public AppendableWrapper() {
        this.appendable = NullAppendable.INSTANCE;
    }

    public AppendableWrapper(Appendable appendable) {
        this.appendable = appendable;
    }

    public boolean isNullAppendable() {
        return appendable == NullAppendable.INSTANCE;
    }

    public AppendableWrapper writeOrNull(Object obj) {
        return write(obj, "null");
    }

    public AppendableWrapper write(Object obj) {
        return write(obj, "");
    }

    public AppendableWrapper write(Object obj, String nullDefault) {
        try {
            appendable.append(Objects.toString(obj, nullDefault));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public AppendableWrapper newline() {
        return write(System.lineSeparator());
    }

    @Override
    public AppendableWrapper append(CharSequence csq) throws IOException {
        appendable.append(csq);
        return this;
    }

    @Override
    public AppendableWrapper append(CharSequence csq, int start, int end)
            throws IOException {
        appendable.append(csq, start, end);
        return this;
    }

    @Override
    public AppendableWrapper append(char c) throws IOException {
        appendable.append(c);
        return this;
    }
}
