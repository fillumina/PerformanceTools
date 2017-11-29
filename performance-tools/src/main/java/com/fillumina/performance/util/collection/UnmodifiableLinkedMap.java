package com.fillumina.performance.util.collection;

import java.util.Map;

/**
 * Being immutable this unmodifiable {@link LinkedMap} is also thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableLinkedMap<K,V> extends LinkedMap<K,V> {

    private static final long serialVersionUID = 1L;

    public static <K,V> LinkedMap<K,V> copy(LinkedMap<K,V> lmap) {
        if (lmap instanceof UnmodifiableLinkedMap) {
            return lmap;
        }
        return new UnmodifiableLinkedMap<>(lmap);
    }

    public UnmodifiableLinkedMap(Map<K, V> copy) {
        copy.forEach((k,v) -> super.linkEntry(new UnmodifiableEntry<>(k, v)) );
    }

    private class UnmodifiableEntry<K,V> extends LinkedEntry<K,V> {

        public UnmodifiableEntry(K key, V value) {
            super(key, value);
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("read only map");
        }

        @Override
        void setNext(LinkedEntry<K, V> next) {
            if (next == null && getNext() != null) {
                // trying to remove an element
                throw new UnsupportedOperationException("read only map");
            }
            super.setNext(next);
        }
    }


    @Override
    protected final void linkEntry(LinkedEntry<K, V> entry) {
        throw new UnsupportedOperationException("read only map");
    }

    @Override
    protected final LinkedEntry<K, V> createEntry(K key, V value) {
        throw new UnsupportedOperationException("read only map");
    }

}
