package com.fillumina.performance.util;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class contains a hierarchy of immutable names.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO refactor using a single Map<> ordered
public class ComposedName implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static final ComposedName EMPTY = new ComposedName(null, "");

    public static ComposedName emtpyOnNull(ComposedName cname) {
        return  (cname == null) ? EMPTY : cname;
    }

    public static ComposedName create(String name) {
        if (name == null) {
            return EMPTY;
        }
        return EMPTY.append(name);
    }

    private final ComposedName parent;
    private final int size;
    private final String lastName;
    private final String fullName;
    private final int hashCode;
    private Map<String, WeakReference<ComposedName>> children;
    private final ReferenceQueue<ComposedName> nodeQueue = new ReferenceQueue<>();

    public ComposedName(ComposedName parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        this.size = parent == null ? 0 : parent.size() + 1;
        this.fullName = calculateFullName(parent, lastName);
        this.hashCode = innerHashCode(parent, lastName);
    }

    public synchronized boolean isEmpty() {
        return lastName.isEmpty();
    }

    public int size() {
        return size;
    }

    public List<String> asList() {
        String[] array = new String[size];
        int s = size;
        ComposedName current = this;
        while (s > 0) {
            array[--s] = current.lastName;
            current = current.parent;
        }
        return Arrays.asList(array);
    }

    public synchronized ComposedName append(String name) {
        if (name == null) {
            return this;
        }
        checkForRemovedEntries();
        WeakReference<ComposedName> nodeRef = null;
        if (children != null) {
            nodeRef = children.get(name);
        }
        ComposedName node;
        if (nodeRef == null || (node = nodeRef.get()) == null) {
            node = new ComposedName(this, name);
            nodeRef = new WeakReference<>(node, nodeQueue);
            checkForRemovedEntriesInAllSubTree();
            if (children == null) {
                children = new HashMap<>();
            }
            children.put(name, nodeRef);
        }
        return node;
    }

    void checkForRemovedEntriesInAllSubTree() {
        if (children != null) {
            Iterator<WeakReference<ComposedName>> it = children.values().iterator();
            while (it.hasNext()) {
                ComposedName node = it.next().get();
                if (node == null) {
                    it.remove();
                } else {
                    node.checkForRemovedEntriesInAllSubTree();
                }
            }
            if (children.isEmpty()) {
                children = null;
            }
        }
    }

    private synchronized void checkForRemovedEntries() {
        boolean removed = false;
        while (nodeQueue.poll() != null) {
            removed = true;
        }
        if (removed && children != null) {
            Iterator<WeakReference<ComposedName>> it = children.values().iterator();
            while (it.hasNext()) {
                ComposedName node = it.next().get();
                if (node == null) {
                    it.remove();
                }
            }
        }
    }

    private String calculateFullName(ComposedName parent, String lastName) {
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

    public ComposedName getComposedNameAtIndex(int index) {
        if (index == size) {
            return this;
        }
        ComposedName result = this;
        for (int g = size - index; g > 0; g--) {
            result = result.parent;
        }
        return result;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        ComposedName current = this;
        while(current.parent != null && current.parent != EMPTY) {
            current = current.parent;
        }
        return current.lastName;
    }

    /* testing */ boolean isEmptyNode() {
        return children == null || children.isEmpty();
    }

    /* testing */ void clean() {
        checkForRemovedEntriesInAllSubTree();
    }

    private static int innerHashCode(ComposedName parent, String lastName) {
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
        final ComposedName other = (ComposedName) obj;
        return parent == other.parent && lastName.equals(other.lastName);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
