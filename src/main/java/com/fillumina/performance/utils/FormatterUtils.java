package com.fillumina.performance.utils;

/**
 *
 * @author fra
 */
public class FormatterUtils {

    public static String formatPercentage(final double percentage) {
        return String.format("%.2f %%", percentage);
    }
}
