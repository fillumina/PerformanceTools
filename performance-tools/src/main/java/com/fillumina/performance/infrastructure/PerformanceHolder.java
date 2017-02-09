package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TreeHolder;
import java.io.IOException;
import java.io.Serializable;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a tree
 * in place without having to use a variable or to enclose a long chain of
 * methods as a parameter.
 *
 * @param T the tree
 * @param L the leaves of the tree
 * @author Francesco Illuminati
 */
public class PerformanceHolder<L,T> extends TreeHolder<L,T>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final PerformanceHolder<?,?> EMPTY =
            new PerformanceHolder<>(null);

    private final StringGenerator<T> formatter;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <L,T> PerformanceHolder<L,T> empty() {
        return (PerformanceHolder<L,T>) EMPTY;
    }

    public PerformanceHolder(final T stats) {
        this(null, stats, null);
    }

    public PerformanceHolder(final ComposedName name,
            final T tree,
            final StringGenerator<T> formatter) {
        super(name, tree);
        this.formatter = formatter;
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public PerformanceHolder<L,T> use(PerformanceConsumer<T> consumer) {
        if (consumer != null) {
            consumer.consume(getName(), getTree());
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @param assertion to be checked
     * @return {@code this}
     */
    public PerformanceHolder<L,T> check(Assertion<T> assertion) {
        if (assertion != null) {
            assertion.check(getTree());
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PerformanceHolder<L,T> checkAndPrint(Appendable appendable,
            Assertion<T> assertion) {
        if (assertion != null) {
            assertion.check(getTree());
            if (appendable != null) {
                try {
                    appendable
                            .append(System.lineSeparator())
                            .append(assertion.toString(getTree()))
                            .append(System.lineSeparator());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return this;
    }

    /**
     * Prints the statistics to standard output if the {@code condition} is
     * true.
     */
    public PerformanceHolder<L,T> printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    public PerformanceHolder<L,T> print() {
        printTo(System.out);
        return this;
    }


    public PerformanceHolder<L,T> printTo(final Appendable appendable) {
        if (appendable != null) {
            try {
                appendable.append(toString()).append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        if (formatter != null) {
            return formatter.toString(getName(), getTree());
        }
        return getTree().toString();
    }

}
