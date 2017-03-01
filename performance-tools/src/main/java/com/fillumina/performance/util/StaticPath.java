package com.fillumina.performance.util;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Contains trees of immutable strings each forming a path.
 * Names are weak referenced so they are automatically reclaimed when not needed.
 * The class is synchronized so it is thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StaticPath implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static final StaticPath EMPTY = createRoot();

    public static StaticPath createRoot() {
        return new StaticPath(null, null);
    }

    public static StaticPath chooseIfNull(StaticPath cn, StaticPath def) {
        return cn == null ? def : cn;
    }

    private final StaticPath parent;
    private final int size;
    private final String lastName;
    private final String fullName;
    private final int hashCode;
    private ArrayList<WeakReference<StaticPath>> children;

    private StaticPath(StaticPath parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        this.size = parent == null ? 0 : parent.size() + 1;
        this.fullName = calculateFullName(parent, lastName);
        this.hashCode = innerHashCode(parent, lastName);
    }

    public boolean isSameRoot(StaticPath cn) {
        return getRoot() == cn.getRoot();
    }

    public StaticPath getRoot() {
        StaticPath current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public boolean isEmpty() {
        return fullName == null || fullName.isEmpty();
    }

    protected static ArrayList<WeakReference<StaticPath>> createList() {
        return new ArrayList<>(3);
    }

    public int size() {
        return size;
    }

    public List<String> asList() {
        String[] array = new String[size];
        int s = size;
        StaticPath current = this;
        while (s > 0) {
            array[--s] = current.lastName;
            current = current.parent;
        }
        return Arrays.asList(array);
    }

    public synchronized StaticPath append(String name) {
        if (name == null) {
            return this;
        }
        if (children != null) {
            ListIterator<WeakReference<StaticPath>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<StaticPath> wr = it.next();
                StaticPath cn = wr.get();
                if (cn == null) {
                    it.remove();
                } else if (name.equals(cn.getLastName())) {
                    return cn;
                }
            }
        } else {
            children = createList();
        }
        StaticPath cn = new StaticPath(this, name);
        children.add(new WeakReference<>(cn));
        return cn;
    }

    public String getLastName() {
        return lastName;
    }

    public synchronized String getFirstName() {
        StaticPath current = this;
        while(current.parent != null && current.parent.lastName != null) {
            current = current.parent;
        }
        return current.lastName;
    }

    private String calculateFullName(StaticPath parent, String lastName) {
        if (parent == null) {
            return lastName;
        }
        StringBuilder buf = new StringBuilder();
        for (String name : asList()) {
            if (buf.length() != 0) {
                buf.append(SEPARATOR);
            }
            buf.append(name);
        }
        return buf.toString();
    }

    public boolean isChildrenEmpty() {
        return children == null || children.isEmpty();
    }

    public synchronized void clean() {
        if (children != null) {
            int removed = 0;
            ListIterator<WeakReference<StaticPath>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<StaticPath> wr = it.next();
                StaticPath cn = wr.get();
                if (cn == null) {
                    removed++;
                    it.remove();
                } else {
                    cn.clean();
                }
            }
            if (children.isEmpty()) {
                children = null;
            } else if (removed > children.size() / 2) {
                children.trimToSize();
            }
        }
    }

    private static int innerHashCode(StaticPath parent, String lastName) {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(parent);
        hash = 59 * hash + Objects.hashCode(lastName);
        return hash;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaticPath other = (StaticPath) obj;
        return parent == other.parent && lastName.equals(other.lastName);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
