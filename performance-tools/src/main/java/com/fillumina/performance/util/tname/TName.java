package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.UnmodifiableList;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains trees of immutable strings each forming a path.
 * Different trees can be created (same concept as namespaces).
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
    private final int level;
    private final String lastName;
    private final String fullName;
    private final String[] array;
    private ArrayList<WeakReference<TName>> children;

    private TName(TName parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        this.level = parent == null ? 0 : parent.size() + 1;
        this.array = createArray();
        this.fullName = toStringWithSeparator(SEPARATOR);
    }

    private String[] createArray() {
        String[] a = new String[level];
        int s = level;
        TName current = this;
        while (s > 0) {
            a[--s] = current.lastName;
            current = current.parent;
        }
        return a;
    }

    public TName append(Iterable<String> names) {
        TName current = this;
        for (String n : names) {
            if (n != null) {
                current = current.append(n);
            }
        }
        return current;
    }

    public TName append(String... names) {
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
                TName child = wr.get();
                if (child == null) {
                    it.remove();
                } else if (name.equals(child.getLastName())) {
                    return child;
                }
            }
        } else {
            children = new ArrayList<>(3);
        }
        TName child = new TName(this, name);
        children.add(new WeakReference<>(child));
        return child;
    }

    public synchronized void clean() {
        if (children != null) {
            int removed = 0;
            ListIterator<WeakReference<TName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<TName> wr = it.next();
                TName child = wr.get();
                if (child == null) {
                    removed++;
                    it.remove();
                } else {
                    child.clean();
                }
            }
            if (children.isEmpty()) {
                children = null;
            } else if (removed > children.size() / 2) {
                children.trimToSize();
            }
        }
    }

    /** test only */
    protected boolean isChildrenEmpty() {
        return children == null || children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
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
        return level == 0;
    }

    @Override
    public int size() {
        return level;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public TName getParent() {
        return parent;
    }

    /** @return an unmodifiable {@link List} of {@link TName}s. */
    public List<TName> getAllPartialTNames() {
        TName[] tnames = new TName[level];
        TName current = this;
        for (int index = level - 1; index >= 0; index--) {
            tnames[index] = current;
            current = current.parent;
        }
        return new UnmodifiableList<>(tnames);
    }

    public TName getTNameAt(int index) {
        TName current = this;
        for (int i=0; i< level - index - 1; i++) {
            current = current.parent;
        }
        return current;
    }

    @Override
    public String[] toArray() {
        return this.array.clone();
    }

    /** It does nothing. */
    @Override
    public boolean add(String e) {
        return true;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return array == null || array.length == 0 ? null : array[0];
    }

    /** @return all but last name. */
    public String getPrefixString(String separator) {
        return parent.toStringWithSeparator(separator);
    }

    public boolean isSharingPrefixWith(TName other) {
        return !commonPrefix(other).isEmpty();
    }

    public TName commonPrefix(TName other) {
        int minlen = Math.min(level, other.level);
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

    public String toStringWithSeparator(String separator) {
        return toStringWithSeparatorStartingFrom(separator, 0);
    }

    public String toStringWithSeparatorStartingFrom(String separator, int index) {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (String s : array) {
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
    public String get(int index) {
        return array == null ? null : array[index];
    }

    @Override
    public ListIterator<String> listIterator() {
        return listIterator(0);
    }

    @Override
    public Iterator<String> iterator() {
        return listIterator(0);
    }

    /** Much faster than {@link #iterator()} */
    public Iterator<String> reverseIterator() {
        return new Iterator<String>() {
            private TName current = TName.this;

            @Override
            public boolean hasNext() {
                return !current.isRoot();
            }

            @Override
            public String next() {
                String result = current.lastName;
                current = current.parent;
                return result;
            }
        };
    }

    @Override
    public ListIterator<String> listIterator(int startIndex) {
        return new ListIterator<String>() {
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

    @Override
    public int hashCode() {
        // this is by design so TName can be hash compatible with their
        // string representations (i.e. in maps, especially if size = 1)
        return fullName.hashCode();
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

    @Override
    public String toString() {
        return fullName;
    }
}
