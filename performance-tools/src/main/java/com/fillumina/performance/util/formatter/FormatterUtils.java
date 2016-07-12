package com.fillumina.performance.util.formatter;

/**
 *
 * @author Francesco Illuminati
 */
public class FormatterUtils {

    public static String formatPercentage(final double percentage) {
        return String.format("%.2f %%", percentage);
    }
}
