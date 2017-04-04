package com.fillumina.performance.util.collection;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface UnmodifiableSymmetricMatrix<K, V> {

    int size();

    V get(K k1, K k2);

    Set<K> keySet();

    Collection<V> values();
}
