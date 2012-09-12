package com.fillumina.performance.util;

/**
 *
 * @author fra
 */
public class ConcurrencyHelper {

    /** This would make the test much faster and more reliable leaving
     * some CPU free for the SO.
     */
    public static int getConcurrencyLevel() {
        int concurrency = Runtime.getRuntime().availableProcessors();
        if (concurrency > 3) {
            concurrency -= 2;
        }
        return concurrency;
    }
}
