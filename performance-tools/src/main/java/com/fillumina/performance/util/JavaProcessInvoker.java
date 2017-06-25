package com.fillumina.performance.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO finish this
public class JavaProcessInvoker {

    public interface OutputListener {
        void onLineOutput(String line);
        void onError(Exception err);
    }

    public void invokeJavaOnClass(Class<?> clazz,
            OutputListener listener,
            String... options) {

        String javaExec = getJavaExecutableAbsolutePath();
        String classpath = getClassPath();
        String filename = clazz.getName();

        System.out.println("java: " + javaExec);
        System.out.println("calling: " + filename);

        List<String> commands = new ArrayList<>();
        commands.addAll(
                Arrays.asList(javaExec, "-classpath", classpath, filename));
        commands.addAll(
                Arrays.asList(options));
        ProcessBuilder builder = new ProcessBuilder(commands);

        Process p;
        try {
            p = builder.start();
            BufferedReader br = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
            p.waitFor();
            String t;
            while ((t = br.readLine()) != null) {
                if (listener != null) {
                    listener.onLineOutput(t);
                }
            }
            p.destroy();
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    private String getClassPath() {
        URL[] urls = ((URLClassLoader)
                Thread.currentThread().getContextClassLoader()).getURLs();
        StringBuilder buf = new StringBuilder();
        for (URL url : urls) {
            if (buf.length() > 0) {
                buf.append(":");
            }
            buf.append(url.getFile());
        }
        String classpath = buf.toString();
        return classpath;
    }

    private static String getJavaExecutableAbsolutePath() {
        return System.getProperty("java.home") +
                File.separator + "bin" +
                File.separator + "java" +
                (System.getProperty("os.name").contains("ux") ? "" : ".exe");
    }

}
