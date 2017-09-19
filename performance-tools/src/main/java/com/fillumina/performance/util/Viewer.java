package com.fillumina.performance.util;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that prints out
 * performances using the specified {@link StringGenerator}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Viewer<T> implements Consumer<T> {

    private final StringGenerator<T> formatter;
    private final Appendable appendable;

    /**
     * @param formatter used to format the performance to print out.
     */
    public Viewer(StringGenerator<T> formatter) {
        this(formatter, System.out);
    }

    /**
     * @param appendable to append string to
     * @param formatter used to format the performance to print out.
     */
    public Viewer(StringGenerator<T> formatter,
            Appendable appendable) {
        Objects.requireNonNull(formatter, "formatter cannot be null");
        this.appendable = appendable;
        this.formatter = formatter;
    }

    @Override
    public void accept(T assertable) {
        if (appendable != null && assertable != null && formatter != null) {
            try {
                formatter.appendTo(appendable, assertable);
                appendable.append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
