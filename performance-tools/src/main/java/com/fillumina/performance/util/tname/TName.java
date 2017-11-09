package com.fillumina.performance.util.tname;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Contains trees of immutable strings each forming a path.
 * Names are weak referenced so they are automatically reclaimed when not needed.
 * The class is synchronized so it is thread safe.
 * TName means TreeName but has been shortened because of its frequent use.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TName extends AbstractList<String>
        implements Comparable<TName>, CharSequence, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static final TName ROOT = createRoot();

    public static TName createRoot() {
        return new TName(null, null);
    }

    public static String toString(String... names) {
        StringBuilder buf = new StringBuilder();
        for (String n : names) {
            if (buf.length() != 0) {
                buf.append(SEPARATOR);
            }
            buf.append(n);
        }
        return buf.toString();
    }

    public static TName commonPrefix(Iterable<TName> iterable) {
        Iterator<TName> it = iterable.iterator();
        if (!it.hasNext()) {
            return null;
        }
        TName common = it.next();
        while (it.hasNext()) {
            common = common.commonPrefix(it.next());
        }
        return common;
    }

    private final TName parent;
    private final int size;
    private final String lastName;
    private final int hashCode;
    private final String fullName;
    private ArrayList<WeakReference<TName>> children;

    private TName(TName parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        this.size = parent == null ? 0 : parent.size() + 1;
        this.hashCode = innerHashCode(parent, lastName);
        this.fullName = toStringWithSeparator(SEPARATOR);
    }

    public boolean isSameRoot(TName cn) {
        return getRoot() == cn.getRoot();
    }

    public TName getRoot() {
        TName current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    @Override
    public boolean isEmpty() {
        return lastName == null;
    }

    @Override
    public int size() {
        return size;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public TName getParent() {
        return parent;
    }

    @Override
    public String[] toArray() {
        String[] array = new String[size];
        int s = size;
        TName current = this;
        while (s > 0) {
            array[--s] = current.lastName;
            current = current.parent;
        }
        return array;
    }

    @Override
    public boolean add(String e) {
        append(e);
        return true;
    }

    public synchronized TName append(Iterable<String> names) {
        TName current = this;
        for (String n : names) {
            if (n != null) {
                current = current.append(n);
            }
        }
        return current;
    }

    public synchronized TName append(String... names) {
        TName current = this;
        for (String n : names) {
            if (n != null) {
                current = current.append(n);
            }
        }
        return current;
    }

    public synchronized TName append(String name) {
        if (name == null) {
            return this;
        }
        if (children != null) {
            ListIterator<WeakReference<TName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<TName> wr = it.next();
                TName cn = wr.get();
                if (cn == null) {
                    it.remove();
                } else if (name.equals(cn.getLastName())) {
                    return cn;
                }
            }
        } else {
            children = new ArrayList<>(3);
        }
        TName cn = new TName(this, name);
        children.add(new WeakReference<>(cn));
        return cn;
    }

    public String getLastName() {
        return lastName;
    }

    public synchronized String getFirstName() {
        TName current = this;
        while(current.parent != null && current.parent.lastName != null) {
            current = current.parent;
        }
        return current.lastName;
    }

    /** @return all but last name. */
    public synchronized String getPrefix() {
        StringBuilder buf = new StringBuilder();
        String[] array = toArray();
        for (int i=0, l=array.length-1; i<l; i++) {
            if (i > 0) {
                buf.append(SEPARATOR);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public boolean isChildrenEmpty() {
        return children == null || children.isEmpty();
    }

    public synchronized void clean() {
        if (children != null) {
            int removed = 0;
            ListIterator<WeakReference<TName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<TName> wr = it.next();
                TName cn = wr.get();
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

    public TName commonPrefix(TName other) {
        int minlen = Math.min(size, other.size);
        TName prefix = getRoot();
        for (int i=0; i<minlen; i++) {
            String indexedName = get(i);
            if (indexedName.equals(other.get(i))) {
                prefix = prefix.append(indexedName);
            } else {
                break;
            }
        }
        return prefix;
    }

    private static int innerHashCode(TName parent, String lastName) {
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
        final TName other = (TName) obj;
        return parent == other.parent && lastName.equals(other.lastName);
    }

    public String toStringWithSeparator(String separator) {
        return toStringWithSeparatorFromIndex(separator, 0);
    }

    public String toStringWithSeparatorFromIndex(String separator, int index) {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (String s : toArray()) {
            if (i >= index) {
                if (buf.length() != 0) {
                    buf.append(separator);
                }
                buf.append(s);
            }
            i++;
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public String get(int index) {
        int backIndex = size - index;
        TName current = this;
        for (int i=1; i<backIndex; i++) {
            current = current.parent;
        }
        return current.lastName;
    }

    @Override
    public ListIterator<String> listIterator() {
        return listIterator(0);
    }

    @Override
    public Iterator<String> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<String> listIterator(int startIndex) {
        return new ListIterator<String>() {
            private final String[] array = toArray();
            private int index = startIndex;

            @Override
            public boolean hasNext() {
                return index < array.length;
            }

            @Override
            public String next() {
                String r = array[index];
                index++;
                return r;
            }

            @Override
            public boolean hasPrevious() {
                return index > 0;
            }

            @Override
            public String previous() {
                index--;
                return array[index];
            }

            @Override
            public int nextIndex() {
                return index;
            }

            @Override
            public int previousIndex() {
                return index - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("read only.");
            }

            @Override
            public void set(String e) {
                throw new UnsupportedOperationException("read only.");
            }

            @Override
            public void add(String e) {
                throw new UnsupportedOperationException("read only.");
            }
        };
    }

    @Override
    public int compareTo(TName other) {
        if (other == null) {
            return -1;
        }
        int thisSize = size();
        int otherSize = other.size();
        int sz = Math.min(thisSize, otherSize);
        int cmp;
        for (int i=0; i<sz; i++) {
            cmp = get(i).compareTo(other.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(thisSize, otherSize);
    }

    @Override
    public int length() {
        return fullName.length();
    }

    @Override
    public char charAt(int index) {
        return fullName.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return fullName.subSequence(start, end);
    }
}
