package com.fillumina.performance.time.stats.strgen;

import java.util.function.Predicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReferenceTestFilter {

    /**
     * Default filter to avoid tests ending with $ or containing $_ to
     * be used in percentages stats (usually to allow expressions to be
     * favored instead).
     * It's really an hack because it relies on
     * {@link com.fillumina.performance.util.tname.TName#toString() }
     * representation which should not be part of the API.
     */
    public static final Predicate<String> FILTER =
            n -> n.endsWith("~") || n.contains("~_") || n.contains("~ :");

    public static String makeItFiltrable(String name) {
        return name + "~";
    }
}
