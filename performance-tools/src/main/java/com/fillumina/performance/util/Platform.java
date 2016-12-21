package com.fillumina.performance.util;

/**
 * Retrieve some information about the system.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Platform {

    public static final Platform INSTANCE = new Platform();

    private Platform() {}

    public static void main(String[] args) {
        System.out.println(Platform.INSTANCE.toString());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        java.util.Date now = new java.util.Date();
        buf.append(String.format("# Date: %s%n",
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(now)));

        // The processor identifier works only on MS Windows:
        buf.append(String.format("# CPU: %s; %d \"procs\"%n",
            System.getenv("PROCESSOR_IDENTIFIER"),
            Runtime.getRuntime().availableProcessors()));

        buf.append(String.format("# OS: %s; %s; %s%n",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch")));

        buf.append(String.format("# JVM: %s; %s%n",
            System.getProperty("java.vendor"),
            System.getProperty("java.version")));

        /* Total amount of free memory available to the JVM */
        long maxMemory = Runtime.getRuntime().maxMemory();
        buf.append(String.format(
                "# JVM Memory: %s free, %s available, %s max, ",
            mb(Runtime.getRuntime().freeMemory()),
            mb(Runtime.getRuntime().totalMemory()),
            (maxMemory == Long.MAX_VALUE ? "no limit" : mb(maxMemory))));

        return buf.toString();
    }

    private static String mb(long bytes) {
        return String.format("%,d MiB", bytes / 1024 / 1024);
    }
}
