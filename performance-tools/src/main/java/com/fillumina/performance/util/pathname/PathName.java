package com.fillumina.performance.util.pathname;

import com.fillumina.performance.util.collection.UnmodifiableList;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains a tree of nodes where each node represents a sequence of immutable
 * strings from the root down to that node. Different roots can be created.
 * It's an efficient way to use path names without having to manage lists or
 * arrays and consuming as little memory as possible maintaining an acceptable
 * speed.
 * Names are weak referenced so they are automatically reclaimed when not needed.
 * The class is synchronized so it is thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathName extends AbstractList<String>
        implements Comparable<PathName>, CharSequence, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String[] EMPTY_ARRAY = new String[0];

    public static final String DEFAULT_SEPARATOR = " : ";

    // this is just one of the possible roots. this is used by default.
    public static final PathName ROOT = createRoot();

    public static PathName createRoot() {
        return new PathName(null, DEFAULT_SEPARATOR);
    }

    public static PathName createRootWithSeparator(String separator) {
        return new PathName(null, separator);
    }

    public static PathName getCommonPrefix(Iterable<PathName> iterable) {
        Iterator<PathName> it = iterable.iterator();
        if (!it.hasNext()) {
            return null;
        }
        PathName common = it.next();
        while (it.hasNext()) {
            common = common.commonPrefix(it.next());
        }
        return common;
    }

    // it's an inverted linked list
    private final PathName parent;
    // in the root element it doubles as the separator.
    private final String lastName;

    private final int level;
    private final String fullName;
    private final String[] array;
    private ArrayList<WeakReference<PathName>> children;

    protected PathName(PathName parent, String lastName) {
        this.parent = parent;
        this.lastName = lastName;
        String separator;
        String[] a;
        if (parent == null) {
            // its root
            this.level = 0;
            separator = lastName;
            a = EMPTY_ARRAY;
        } else {
            this.level = parent.size() + 1;

            // create array
            a = new String[level];
            int s = level;
            PathName current = this;
            while (s > 0) {
                a[--s] = current.lastName;
                current = current.parent;
            }
            // current is now the root
            separator = current.getSeparator();
        }
        this.array = a;
        this.fullName = toStringWithSeparator(separator);
    }

    /** Override if you extend this class. */
    protected PathName createNew(String name) {
        return new PathName(this, name);
    }

    public PathName append(Iterable<String> names) {
        if (names == null) {
            return this;
        }
        PathName current = this;
        for (String n : names) {
            if (n != null) {
                current = current.append(n);
            }
        }
        return current;
    }

    public PathName append(String... names) {
        if (names == null || names.length == 0) {
            return this;
        }
        PathName current = this;
        for (String n : names) {
            if (n != null) {
                current = current.append(n);
            }
        }
        return current;
    }

    public synchronized PathName append(String name) {
        if (name == null || name.isEmpty()) {
            return this;
        }
        if (children != null) {
            ListIterator<WeakReference<PathName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<PathName> wr = it.next();
                PathName child = wr.get();
                if (child == null) {
                    it.remove();
                } else if (name.equals(child.getLastName())) {
                    return child;
                }
            }
        } else {
            children = new ArrayList<>(3);
        }
        PathName child = createNew(name);
        children.add(new WeakReference<>(child));
        return child;
    }

    public synchronized void clean() {
        if (children != null) {
            int removed = 0;
            ListIterator<WeakReference<PathName>> it = children.listIterator();
            while (it.hasNext()) {
                WeakReference<PathName> wr = it.next();
                PathName child = wr.get();
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

    public boolean isSameRoot(PathName cn) {
        return getRoot() == cn.getRoot();
    }

    public PathName getRoot() {
        PathName current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public String getSeparator() {
        if (isRoot()) {
            return this.lastName;
        }
        return getRoot().getSeparator();
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

    public PathName getParent() {
        return parent;
    }

    /** @return an unmodifiable {@link List} of {@link PathName}s. */
    public List<PathName> getAllPartialTNames() {
        PathName[] tnames = new PathName[level];
        PathName current = this;
        for (int index = level - 1; index >= 0; index--) {
            tnames[index] = current;
            current = current.parent;
        }
        return new UnmodifiableList<>(tnames);
    }

    public PathName getTNameAt(int index) {
        PathName current = this;
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
        if (isRoot()) {
            return null;
        }
        return lastName;
    }

    public String getFirstName() {
        return array == null || array.length == 0 ? null : array[0];
    }

    /** @return all but last name. */
    public String getPrefixString(String separator) {
        return parent.toStringWithSeparator(separator);
    }

    public boolean isSharingPrefixWith(PathName other) {
        return !commonPrefix(other).isEmpty();
    }

    public PathName commonPrefix(PathName other) {
        int minlen = Math.min(level, other.level);
        PathName prefix = getRoot();
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
            private PathName current = PathName.this;

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
    public int compareTo(PathName other) {
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
        // this is by design so PathName can be hash compatible with their
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
        final PathName other = (PathName) obj;
        return parent == other.parent && lastName.equals(other.lastName);
    }

    @Override
    public String toString() {
        return fullName;
    }
}
