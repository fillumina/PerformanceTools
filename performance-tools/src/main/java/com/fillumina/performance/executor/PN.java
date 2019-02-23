package com.fillumina.performance.executor;

import com.fillumina.performance.util.pathname.PathName;

/**
 * Private root for a {@link PathName} hierarchy and helpers.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PN {

    private static final String SEPARATOR = " : ";

    public static final PathName EMPTY =
            PathName.createRootWithSeparator(SEPARATOR);
    public static final PathName CURRENT = EMPTY.append("current");

    public static final PathName pname(CharSequence name) {
        if (name == null || name.length() == 0) {
            return PN.EMPTY;
        }
        if (name instanceof PathName) {
            return (PathName)name;
        }
        return PN.EMPTY.append(name.toString());
    }

    public static final PathName pname(Iterable<String> names) {
        return PN.EMPTY.append(names);
    }

    public static final PathName pname(String... names) {
        return PN.EMPTY.append(names);
    }

    public static final PathName pname(PathName pname) {
        return notNull(pname);
    }

    public static final PathName notNull(PathName pname) {
        if (pname == null) {
            return EMPTY;
        }
        return pname;
    }
}
