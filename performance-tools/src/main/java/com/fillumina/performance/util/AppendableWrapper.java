package com.fillumina.performance.util;

import java.io.IOException;
import java.util.Objects;

/**
 * {@link Appendable} methods throws {@link IOExceptions} which is annoying,
 * this class wraps an {@link Appendable} and provides methods that throws a
 * {@link RuntimeException} instead.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableWrapper {

    private final Appendable appendable;

    public static void appendTo(Appendable appendable, CharSequence csq) {
        try {
            appendable.append(csq);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void appendTo(Appendable appendable, char c) {
        try {
            appendable.append(c);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /** Uses a {@link NullAppendable} to output nothing. */
    public AppendableWrapper() {
        this.appendable = NullAppendable.INSTANCE;
    }

    public AppendableWrapper(Appendable appendable) {
        this.appendable = appendable == null ?
                NullAppendable.INSTANCE : appendable;
    }

    public AppendableWrapper println(Object obj) {
        print(obj);
        print(System.lineSeparator());
        return this;
    }

    public AppendableWrapper print(Object obj) {
        return print(obj, "null");
    }

    public AppendableWrapper print(Object obj, String nullDefault) {
        if (appendable != null) {
            try {
                appendable.append(Objects.toString(obj, nullDefault));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    public AppendableWrapper newline() {
        return AppendableWrapper.this.println("");
    }

    public Appendable getAppendable() {
        return appendable;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }
}
