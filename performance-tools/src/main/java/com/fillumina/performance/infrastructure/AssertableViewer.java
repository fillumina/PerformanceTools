package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.io.IOException;
import java.util.Objects;

/**
 * A {@link AssertableConsumer} that prints out
 * performances using the specified {@link AssertableStringGenerator}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableViewer<A extends Assertable>
        implements AssertableConsumer<A> {

    private final AssertableStringGenerator<A> formatter;
    private final Appendable appendable;

    /**
     * @param formatter used to format the performance to print out.
     */
    public AssertableViewer(AssertableStringGenerator<A> formatter) {
        this(formatter, System.out);
    }

    /**
     * @param appendable to append string to
     * @param formatter used to format the performance to print out.
     */
    public AssertableViewer(
            AssertableStringGenerator<A> formatter,
            Appendable appendable) {
        Objects.requireNonNull(formatter, "formatter cannot be null");
        this.appendable = appendable;
        this.formatter = formatter;
    }

    @Override
    public void consume(A assertable) {
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
