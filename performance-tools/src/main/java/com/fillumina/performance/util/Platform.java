package com.fillumina.performance.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Locale;

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
        buf.append(String.format(Locale.US, "# Date: %s%n",
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(now)));

        // The processor identifier works only on MS Windows:
        buf.append(String.format(Locale.US, "# CPU: %s; %d \"procs\"%n",
            System.getenv("PROCESSOR_IDENTIFIER"),
            Runtime.getRuntime().availableProcessors()));

        buf.append(String.format(Locale.US, "# OS: %s; %s; %s%n",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch")));

        buf.append(String.format(Locale.US, "# JVM: %s; %s%n",
            System.getProperty("java.vendor"),
            System.getProperty("java.version")));

        /* Total amount of free memory available to the JVM */
        long maxMemory = Runtime.getRuntime().maxMemory();
        buf.append(String.format(Locale.US,
                "# JVM Memory: %s free, %s available, %s max, ",
            mb(Runtime.getRuntime().freeMemory()),
            mb(Runtime.getRuntime().totalMemory()),
            (maxMemory == Long.MAX_VALUE ? "no limit" : mb(maxMemory))));

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        buf.append("\n# JVM bin: ")
                .append(runtimeMXBean.getVmName())
                .append(" (")
                .append(runtimeMXBean.getVmVersion())
                .append(')');

        buf.append("\n# JVM args: ");
        List<String> jvmArgs = runtimeMXBean.getInputArguments();
        for (String arg : jvmArgs) {
            buf.append(arg).append(System.lineSeparator());
        }

        return buf.toString();
    }

    private static String mb(long bytes) {
        return String.format(Locale.US, "%,d MiB", bytes / 1024 / 1024);
    }
}
