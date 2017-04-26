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
public class TreeName implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static final TreeName ROOT = createRoot();

    public static TreeName createRoot() {
        return new TreeName(null, null);
    }

    public static TreeName chooseIfNull(TreeName cn, TreeName def) {
        return cn == null ? def : cn;
    }

    private final TreeName parent;
    private final int size;
    private final String lastName;
    private final String fullName;
    private final int hashCode;
    private ArrayList<WeakReference<TreeName>> children;

    private TreeName(TreeName parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        this.size = parent == null ? 0 : parent.size() + 1;
        this.fullName = calculateFullName(parent, lastName);
        this.hashCode = innerHashCode(parent, lastName);
    }

    public boolean isSameRoot(TreeName cn) {
        return getRoot() == cn.getRoot();
    }

    public TreeName getRoot() {
        TreeName current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public boolean isEmpty() {
        return fullName == null || fullName.isEmpty();
    }

    protected static ArrayList<WeakReference<TreeName>> createList() {
        return new ArrayList<>(3);
    }

    public int size() {
        return size;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public TreeName getParent() {
        return parent;
    }

    public List<String> asList() {
        String[] array = new String[size];
        int s = size;
        TreeName current = this;
        while (s > 0) {
            array[--s] = current.lastName;
            current = current.parent;
        }
        return Arrays.asList(array);
    }

    public synchronized TreeName append(String name) {
        if (name == null) {
            return this;
        }
        if (children != null) {
            ListIterator<WeakReference<TreeName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<TreeName> wr = it.next();
                TreeName cn = wr.get();
                if (cn == null) {
                    it.remove();
                } else if (name.equals(cn.getLastName())) {
                    return cn;
                }
            }
        } else {
            children = createList();
        }
        TreeName cn = new TreeName(this, name);
        children.add(new WeakReference<>(cn));
        return cn;
    }

    public synchronized TreeName append(TreeName name) {
        if (name == null) {
            return this;
        }
        TreeName current = this;
        for (String n : name.asList()) {
            current = current.append(n);
        }
        return current;
    }

    public String getLastName() {
        return lastName;
    }

    public synchronized String getFirstName() {
        TreeName current = this;
        while(current.parent != null && current.parent.lastName != null) {
            current = current.parent;
        }
        return current.lastName;
    }

    private String calculateFullName(TreeName parent, String lastName) {
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
            ListIterator<WeakReference<TreeName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<TreeName> wr = it.next();
                TreeName cn = wr.get();
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

    private static int innerHashCode(TreeName parent, String lastName) {
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
        final TreeName other = (TreeName) obj;
        return parent == other.parent && lastName.equals(other.lastName);
    }

    public String toStringWithSeparator(String separator) {
        StringBuilder buf = new StringBuilder();
        for (String s : asList()) {
            if (buf.length() != 0) {
                buf.append(separator);
            }
            buf.append(s);
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return fullName;
    }
}
