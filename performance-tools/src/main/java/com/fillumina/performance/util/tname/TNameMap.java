package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.IndexedArrayMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed>
        extends IndexedArrayMap<TName, T> {
    private static final long serialVersionUID = 1L;

    public TNameMap() {
        super();
    }

    public TNameMap(int size) {
        super(size);
    }

    public TNameMap(TNameMap<T> copy) {
        super(copy);
    }

    private TNameMap(List<T> list) {
        super();
        list.forEach( t -> add(t) );
    }

    @Override
    public IndexedArrayMap<TName, T> unmodifiable() {
        return new IndexedArrayMap.UnmodifiableView<>(this);
    }

    @Override
    public boolean equals(Object a, Object b) {
        return a == b || Objects.toString(a).equals(Objects.toString(b));
    }

    public TNameMap<T> add(T t) {
        put(t.getName(), t);
        return this;
    }

    public T put(T t) {
        return put(t.getName(), t);
    }

    /** Much slower, it must perform a linear search. */
    public T get(String... key) {
        Iterator<Entry<TName,T>> it = cursor();
        while (it.hasNext()) {
            Entry<TName,T> e = it.next();
            if (equals(e.getKey(), key)) {
                return e.getValue();
            }
        }
        return null;
    }

    static boolean equals(TName tname, String[] key) {
        if (tname.size() != key.length) {
            return false;
        }
        int index = key.length;
        Iterator<String> it = tname.reverseIterator();
        while (it.hasNext()) {
            index--;
            if (index < 0 || !key[index].equals(it.next()) ) {
                return false;
            }
        }
        return true;
    }
}
