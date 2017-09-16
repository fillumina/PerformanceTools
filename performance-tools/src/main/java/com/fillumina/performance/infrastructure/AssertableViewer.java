package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.StringGenerator;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that prints out
 * performances using the specified {@link StringGenerator}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableViewer<A extends Assertable>
        implements Consumer<A> {

    private final Class<A> acceptedAssertable;
    private final StringGenerator<A> formatter;
    private final Appendable appendable;

    /**
     * @param formatter used to format the performance to print out.
     */
    public AssertableViewer(
            Class<A> acceptedAssertable,
            StringGenerator<A> formatter) {
        this(acceptedAssertable, formatter, System.out);
    }

    /**
     * @param appendable to append string to
     * @param formatter used to format the performance to print out.
     */
    public AssertableViewer(
            Class<A> acceptedAssertable,
            StringGenerator<A> formatter,
            Appendable appendable) {
        Objects.requireNonNull(formatter, "formatter cannot be null");
        this.acceptedAssertable = acceptedAssertable;
        this.appendable = appendable;
        this.formatter = formatter;
    }

    @Override
    public void accept(A assertable) {
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
