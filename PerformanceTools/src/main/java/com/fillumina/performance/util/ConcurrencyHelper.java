package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class ConcurrencyHelper {

    /** this would make the test much faster and more reliable. */
    public static int getConcurrencyLevel() {
        int concurrency = Runtime.getRuntime().availableProcessors();
        if (concurrency > 3) {
            concurrency -= 2;
        }
        return concurrency;
    }
}
