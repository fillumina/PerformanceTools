package com.fillumina.performance.executor;

import com.fillumina.performance.util.tname.TName;

/**
 * Private root for a {@link TName} hierarchy and helpers.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TN {

    public static final TName EMPTY = TName.createRoot();

    public static final TName tname(CharSequence name) {
        if (name == null || name.length() == 0) {
            return TN.EMPTY;
        }
        if (name instanceof TName) {
            return (TName)name;
        }
        return TN.EMPTY.append(name.toString());
    }

    public static final TName tname(Iterable<String> names) {
        return TN.EMPTY.append(names);
    }

    public static final TName tname(String... names) {
        return TN.EMPTY.append(names);
    }

    public static final TName notNull(TName tname) {
        if (tname == null) {
            return EMPTY;
        }
        return tname;
    }
}
