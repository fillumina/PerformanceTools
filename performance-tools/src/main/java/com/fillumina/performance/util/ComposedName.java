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

    public static final ComposedName EMPTY = new ComposedName(null, 0, null) {
        private static final long serialVersionUID = 1L;

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String toString() {
            return null;
        }
    };

    private final ComposedName parent;
    private final String name;
    private final int size;

    public ComposedName(String name) {
        this(null, 1, name);
    }

    private ComposedName(ComposedName parent, int size, String name) {
        this.parent = parent;
        this.size = size;
        this.name = name;
    }

    public ComposedName add(String name) {
        return new ComposedName(this, size + 1, name);
    }

    public ComposedName join(ComposedName other) {
        ComposedName c = ComposedName.EMPTY;
        for (String s : getNames()) {
            c = c.add(s);
        }
        for (String s : other.getNames()) {
            c = c.add(s);
        }
        return c;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String getLastName() {
        return name;
    }

    public int size() {
        return size;
    }

    public String[] getNames() {
        String[] names = new String[size];
        int s = size - 1;
        Iterator<String> it = iterator();
        while (it.hasNext() && s >= 0) {
            names[s] = it.next();
            s--;
        }
        return names;
    }

    public String getName(int index) {
        int backwardIndex = size - index;
        Iterator<String> it = iterator();
        for (int i=0; i<backwardIndex; i++) {
            it.next();
        }
        return it.next();
    }

    /** Iterates the names in a reverse order. */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            ComposedName cn = ComposedName.this;

            @Override
            public boolean hasNext() {
                return cn != null && cn.size != 0;
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
    public ComposedName clone() {
        ComposedName c = ComposedName.EMPTY;
        for (String s : getNames()) {
            c = c.add(s);
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String s : getNames()) {
            if (buf.length() != 0) {
                buf.append(" : ");
            }
            buf.append(s);
        }
        return buf.toString();
    }
}
