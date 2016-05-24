package com.fillumina.performance.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class is immutable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedName implements Iterable<String>, Serializable {
    private static final long serialVersionUID = 1L;
    private final String[] names;

    public static final ComposedName EMPTY = new ComposedName();

    public ComposedName(String... names) {
        this.names = names;
    }

    public ComposedName add(String name) {
        String[] newNames = Arrays.copyOf(names, names.length + 1);
        newNames[names.length] = name;
        return new ComposedName(newNames);
    }

    public boolean isEmpty() {
        return names == null || names.length == 0;
    }

    public String getLastName() {
        return names[names.length - 1];
    }

    public int getSize() {
        return names.length;
    }

    public String getNameAt(int index) {
        return names[index];
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < names.length;
            }

            @Override
            public String next() {
                return names[index];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String s : names) {
            if (buf.length() != 0) {
                buf.append(" : ");
            }
            buf.append(s);
        }
        return buf.toString();
    }
}
