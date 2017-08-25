package com.fillumina.performance.assertion;

import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.stats.Measure;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertable<I extends AbstractAssertable<I>>
        extends Printable<I>
        implements Assertable {

    private Measure refMeasure;
    private int refIndex;
    private CharSequence refName;

    protected Measure getReferenceTestMeasure() {
        if (refMeasure == null) {
            calculateBiggerMeasure();
        }
        return refMeasure;
    }

    protected int getReferenceTestIndex() {
        if (refIndex == -1) {
            calculateBiggerMeasure();
        }
        return refIndex;
    }

    @Override
    public CharSequence getReferenceTestName() {
        if (refName == null) {
            calculateBiggerMeasure();
        }
        return refName;
    }

    @Override
    public boolean isEmpty() {
        return getTestNames().isEmpty();
    }

    private void calculateBiggerMeasure() {
        CharSequence name = null;
        int index = -1;
        Measure measure = null;

        int i = 0;
        for (CharSequence n : getTestNames()) {
            Measure m = getMeasure(n);
            if (measure == null || measure.getMean() < m.getMean()) {
                name = n;
                index = i;
                measure = m;
            }
            i++;
        }

        this.refName = name;
        this.refIndex = index;
        this.refMeasure = measure;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I appendTo(Appendable appendable) {
        try {
            appendable.append(getClass().getSimpleName()).append('{');
            Collection<? extends CharSequence> names = getTestNames();
            if (!names.isEmpty()) {
                final CharSequence slowest = getReferenceTestName();
                appendable.append(slowest);
                boolean first = true;
                for (CharSequence n : names) {
                    if (first) {
                        first = false;
                    } else {
                        appendable.append(", ");
                    }
                    if (n.equals(slowest)) {
                        appendable.append("(*) ");
                    }
                    appendable
                            .append(n)
                            .append("=")
                            .append(getMeasure(n).toString());
                }
            }
            appendable.append('}');
            return (I) this;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
