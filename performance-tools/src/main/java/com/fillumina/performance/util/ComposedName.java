package com.fillumina.performance.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class contains a hierarchy of immutable names.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedName extends AbstractList<String> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final ComposedName EMPTY = new ComposedName(null, 0, null) {
        private static final long serialVersionUID = 1L;

        @Override
        public int size() {
            return 0;
        }
    };

    private final ComposedName parent;
    private final String lastName;
    private final String fullName;
    private final int size;
    private Map<String,ComposedName> children;

    public static ComposedName create(String name) {
        if (name == null) {
            return EMPTY;
        }
        return EMPTY.append(name);
    }

    private ComposedName(String name) {
        this(null, 1, name);
    }

    private ComposedName(ComposedName parent, int size, String name) {
        this.parent = parent;
        this.size = size;
        this.lastName = name;
        this.fullName = createFullName();
    }

    private String createFullName() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (String s : this) {
            if (buf.length() != 0) {
                buf.append(" : ");
            }
            buf.append(s);
        }
        return buf.toString();
    }

    public synchronized ComposedName append(String name) {
        if (name == null) {
            return this;
        }
        ComposedName cn = null;
        if (children != null) {
            cn = children.get(name);
        }
        if (cn == null) {
            cn = new ComposedName(this, size + 1, name);
            if (children == null) {
                children = new WeakHashMap<>();
            }
            children.put(name, cn);
        }
        return cn;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String get(int index) {
        int backwardIndex = size - index - 1;
        Iterator<String> it = reverseIterator();
        for (int i=0; i<backwardIndex; i++) {
            it.next();
        }
        return it.next();
    }

    public String getReverse(int index) {
        Iterator<String> it = reverseIterator();
        for (int i=0; i<index; i++) {
            it.next();
        }
        return it.next();
    }

    /** Iterates the names in a reverse order. */
    public Iterator<String> reverseIterator() {
        return new Iterator<String>() {
            ComposedName cn = ComposedName.this;

            @Override
            public boolean hasNext() {
                return cn != null && cn.size != 1;
            }

            @Override
            public String next() {
                String n = cn.lastName;
                cn = cn.parent;
                return n;
            }
        };
    }

    @Override
    public String toString() {
        return fullName;
    }
}
