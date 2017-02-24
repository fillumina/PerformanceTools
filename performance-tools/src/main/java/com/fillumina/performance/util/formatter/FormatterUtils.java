package com.fillumina.performance.util.formatter;

import java.util.Locale;

/**
 *
 * @author Francesco Illuminati
 */
public class FormatterUtils {

    public static String formatPercentage(final double percentage) {
        return String.format(Locale.US, "%.2f %%", percentage);
    }
}
