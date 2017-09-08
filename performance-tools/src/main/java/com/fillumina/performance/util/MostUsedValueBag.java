package com.fillumina.performance.util;

import java.util.Collection;
import java.util.Objects;

/**
 * A structure that quite efficiently returns the most inserted value.
 * {@code null} value is not supported.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MostUsedValueBag<T> {

    private int[] occurrences;
    private T[] obj;

    public MostUsedValueBag() {
        this(10);
    }

    public MostUsedValueBag(Collection<T> collection) {
        this(collection.size());
        collection.forEach(t -> add(t));
    }

    /** @param size sets the <i>initial</i> size. It may still grow if needed. */
    @SuppressWarnings("unchecked")
    public MostUsedValueBag(int size) {
        occurrences = new int[size];
        obj = (T[]) new Object[size];
    }

    /** null value is not supported. */
    public void add(T value) {
        Objects.requireNonNull(value, "null value not supported");
        for (int i=0; i<obj.length; i++) {
            T o = obj[i];
            if (o == null) {
                // add a new element
                obj[i] = value;
                occurrences[i] = 1;
                return;
            }
            if (value == o || value.equals(o)) {
                // add occurrence
                occurrences[i]++;
                bubbleUp(i);
                return;
            }
        }
        // no space for new element, resize
        final int oldLength = obj.length;

        int l = oldLength << 1;
        int[] newOccurrences = new int[l];
        System.arraycopy(occurrences, 0, newOccurrences, 0, occurrences.length);
        occurrences = newOccurrences;

        @SuppressWarnings("unchecked")
        T[] newObj = (T[]) new Object[l];
        System.arraycopy(obj, 0, newObj, 0, obj.length);
        obj = newObj;

        // add new element
        obj[oldLength] = value;
        occurrences[oldLength] = 1;
    }

    private void bubbleUp(int i) {
        // sort up with bubble sort
        while (i > 0 && occurrences[i-1] < occurrences[i]) {
            T tmp = obj[i-1];
            obj[i-1] = obj[i];
            obj[i] = tmp;
            int otmp = occurrences[i-1];
            occurrences[i-1] = occurrences[i];
            occurrences[i] = otmp;
            i--;
        }
    }

    public T getMostUsedValue() {
        return obj[0];
    }

    public int getMostUsedValueFrequency() {
        return occurrences[0];
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName()).append('{');
        Object o;
        for (int i=0; i<obj.length; i++) {
            o = obj[i];
            if (o == null) {
                break;
            }
            if (i>0) {
                buf.append(", ");
            }
            buf.append(o.toString()).append(" -> ").append(occurrences[i]);
        }
        buf.append('}');
        return buf.toString();
    }
}
