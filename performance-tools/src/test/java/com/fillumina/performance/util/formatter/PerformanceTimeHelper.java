package com.fillumina.performance.util.formatter;

/**
 * If you are interested in measuring/calculating elapsed time, then always
 * use System.nanoTime(). On most systems it will give a resolution on the
 * order of microseconds. Be aware though, this call can also take microseconds
 * to execute on some platforms.
 *
 * @see https://blogs.oracle.com/dholmes/entry/inside_the_hotspot_vm_clocks
 * @see http://www.rationaljava.com/2015/10/measuring-microsecond-in-java.html
 * @see http://www.javamex.com/tutorials/threads/thread_scheduling_2.shtml
 * @see https://www.codeproject.com/Articles/662735/Internals-of-Windows-Thread
 * @see https://www.microsoftpressstore.com/articles/article.aspx?p=2233328&seqNum=7
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimeHelper {

    /**
     * It should be more accurate than {@link Thread#sleep(long)}
     * because it doesn't involve thread management by the SO.
     * <p>
     * Be warned that on some systems (i.e. Windows) accuracy is below
     * 30 us so it's better to be safe and don't use anything below 50 us.
     */
    public static void sleepMicroseconds(final int microseconds) {
        final long end = System.nanoTime() + microseconds * 1_000L;
        while(System.nanoTime() < end) {}
    }
}
