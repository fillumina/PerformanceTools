package com.fillumina.performance.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is immutable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedName implements Iterable<String>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final ComposedName EMPTY = new ComposedName(null, null) {
        private static final long serialVersionUID = 1L;

        @Override
        public int size() {
            return 0;
        }
    };

    private ComposedName parent;
    private String name;

    public ComposedName(String name) {
        this(null, name);
    }

    private ComposedName(ComposedName parent, String name) {
        this.name = name;
    }

    public ComposedName add(String name) {
        return new ComposedName(this, name);
    }

    public boolean isEmpty() {
        return name == null && parent == null;
    }

    public String getLastName() {
        return name;
    }

    public int size() {
        ComposedName p = parent;
        int size = 1;
        while (p != null) {
            p = p.parent;
            size++;
        }
        return size;
    }

    public String[] getName() {
        int size = size();
        String[] names = new String[size];
        size--;
        Iterator<String> it = iterator();
        while (it.hasNext()) {
            names[size] = it.next();
            size--;
        }
        return names;
    }

    /** Iterates the names in a reverse order. */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            ComposedName cn = ComposedName.this;

            @Override
            public boolean hasNext() {
                return cn.parent != null;
            }

            @Override
            public String next() {
                String n = cn.name;
                cn = cn.parent;
                return n;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String s : getName()) {
            if (buf.length() != 0) {
                buf.append(" : ");
            }
            buf.append(s);
        }
        return buf.toString();
    }
}
