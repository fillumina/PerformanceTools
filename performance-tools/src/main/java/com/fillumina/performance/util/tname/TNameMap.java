package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.AbstractMapListWrapper;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed>
        extends AbstractMapListWrapper<TNameMap<T>, TName, T> {
    private static final long serialVersionUID = 1L;

    public TNameMap() {
    }

    public TNameMap(int size) {
        super(size);
    }

    public TNameMap(TNameMap<T> copy) {
        super(copy);
    }

    private TNameMap(List<T> list) {
        super(list);
    }

    @Override
    protected TName getKeyFromValue(T value) {
        return value.getName();
    }

    @Override
    protected TNameMap<T> createNew(List<T> list) {
        return new TNameMap<>(list);
    }

    /** Search by string equality. */
    public T get(CharSequence key) {
        String s = key.toString();
        Iterator<Entry<TName,T>> it = iterator();
        while (it.hasNext()) {
            Entry<TName,T> e = it.next();
            if ( s.equals(e.getKey().toString()) ) {
                return e.getValue();
            }
        }
        return null;
    }

    public T get(String... key) {
        Iterator<Entry<TName,T>> it = iterator();
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
