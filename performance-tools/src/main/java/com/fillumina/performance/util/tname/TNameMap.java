package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.AbstractMapListWrapper;
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

    /** Equality is defined in terms of equal string representations. */
    public T get(CharSequence key) {
        String str = key.toString();
        return findByKey(t -> t.equals(key) || str.equals(t.toString()));
    }
}
