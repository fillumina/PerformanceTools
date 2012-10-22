package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FormatterUtils {

    public static String formatPercentage(final double percentage) {
        return String.format("%.2f %%", percentage);
    }
}
