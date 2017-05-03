package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.TName;

/**
 * Private root for a {@link TName} hierarchy.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TN {

    public static final TName EMPTY = TName.createRoot();

    public static final TName n(String... names) {
        return TN.EMPTY.append(names);
    }
}
