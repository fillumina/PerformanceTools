package com.fillumina.performance.util;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class contains a hierarchy of immutable names.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedName implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final ComposedName EMPTY =
            new ComposedName(new Node(null, null), 0);

    private static class Node {
        private final Node parent;
        private final String lastName;
        private final WeakReference<ComposedName>[] partials;
        private Map<String, WeakReference<Node>> children;
        private final ReferenceQueue<Node> nodeQueue = new ReferenceQueue<>();

        @SuppressWarnings("unchecked")
        public Node(Node parent, String lastName) {
            this.parent = parent;
            this.lastName = lastName;
            int size = calculateSize(parent);
            this.partials = (WeakReference<ComposedName>[])
                    new WeakReference[size];
        }

        boolean isEmpty() {
            if (children == null) {
                return true;
            }
            if (children.isEmpty()) {
                children = null;
                return true;
            }
            return false;
        }

        String getName() {
            return lastName;
        }

        List<String> getNames(int size) {
            String[] array = new String[size];
            Node current = this;
            while (size > 0) {
                array[--size] = current.lastName;
                current = current.parent;
            }
            return Arrays.asList(array);
        }

        ComposedName getDefaultComposedName() {
            return getComposedNodeWithLength(partials.length);
        }

        String calculateFullName(int size) {
            if (parent == null) {
                return "";
            }
            StringBuilder buf = new StringBuilder();
            for (String name : getNames(size)) {
                if (buf.length() != 0) {
                    buf.append(" : ");
                }
                buf.append(name);
            }
            return buf.toString();
        }

        ComposedName getComposedNodeWithLength(int length) {
            WeakReference<ComposedName> cnRef = partials[length - 1];
            ComposedName cn;
            if (cnRef == null || (cn = cnRef.get()) == null) {
                cn = new ComposedName(this, length);
                partials[length - 1] = new WeakReference<>(cn);
            }
            return cn;
        }

        private synchronized Node append(String name) {
            if (name == null) {
                return this;
            }
            checkForRemovedEntries();
            WeakReference<Node> nodeRef = null;
            if (children != null) {
                nodeRef = children.get(name);
            }
            Node node;
            if (nodeRef == null || (node = nodeRef.get()) == null) {
                node = new Node(this, name);
                nodeRef = new WeakReference<>(node, nodeQueue);
                if (children == null) {
                    children = new HashMap<>();
                } else {
                    checkForRemovedEntriesInAllSubTree();
                }
                children.put(name, nodeRef);
            }
            return node;
        }

        private static int calculateSize(Node parent) {
            int size = 0;
            Node current = parent;
            while (current != null) {
                current = current.parent;
                size++;
            }
            return size;
        }

        void checkForRemovedEntriesInAllSubTree() {
            if (children != null) {
                Iterator<WeakReference<Node>> it = children.values().iterator();
                while (it.hasNext()) {
                    Node node = it.next().get();
                    if (node == null) {
                        it.remove();
                    } else {
                        node.checkForRemovedEntriesInAllSubTree();
                    }
                }
            }
        }

        private synchronized void checkForRemovedEntries() {
            boolean removed = false;
            while (nodeQueue.poll() != null) {
                removed = true;
            }
            if (removed && children != null) {
                Iterator<WeakReference<Node>> it = children.values().iterator();
                while (it.hasNext()) {
                    Node node = it.next().get();
                    if (node == null) {
                        it.remove();
                    }
                }
            }
        }
    }

    private final Node node;
    private final int size;
    private final String fullName;

    private ComposedName(Node node, int size) {
        this.node = node;
        this.size = size;
        this.fullName = node.calculateFullName(size);
    }

    public static ComposedName create(String name) {
        if (name == null) {
            return EMPTY;
        }
        return EMPTY.append(name);
    }

    public synchronized ComposedName removeHead() {
        if (node == null) {
            return this; // this == EMPTY
        }
        return node.getComposedNodeWithLength(size - 1);
    }

    public synchronized ComposedName append(String name) {
        if (name == null || node == null) {
            return this;
        }
        return node.append(name).getDefaultComposedName();
    }

    public String getLastName() {
        return node.lastName;
    }

    public String getFirstName() {
        Node current = node;
        for (int i=1; i<size; i++) {
            current = current.parent;
        }
        return current.lastName;
    }

    public int size() {
        return size;
    }

    public void clean() {
        node.checkForRemovedEntriesInAllSubTree();
    }

    public List<ComposedName> asComposedNameList() {
        ComposedName[] array = new ComposedName[size];
        Node current = node;
        int index = size - 1;
        while (index >= 0) {
            array[index] = current.getDefaultComposedName();
            current = current.parent;
            index--;
        }
        return Arrays.asList(array);
    }

    @Override
    public String toString() {
        return fullName;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    boolean isEmptyNode() {
        return node == null || node.isEmpty();
    }
}
