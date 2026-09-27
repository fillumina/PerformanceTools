package com.fillumina.performance.examples;

import com.fillumina.performance.Telemetry;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.AccurateSleeper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The third leg: tracing a running code path rather than a loop.
 * <p>
 * The two other modes compare a code you have isolated. Telemetry does not
 * require isolation at all. You drop a marker at a point of interest and it
 * reports what share of the time was spent there, so you can find out where a
 * long algorithm actually spends its time without writing a benchmark for each
 * stage first.
 * <p>
 * Markers go anywhere, including inside third-party or generated code you would
 * rather not restructure. Because each call records the time elapsed since the
 * previous marker, the sequence of calls is what defines the sections.
 * <p>
 * A section that runs a known number of times is declared with the iteration
 * count, so that its share is of the work rather than of the calls:
 * {@code section("name", 10)} for a stage that ran ten times.
 * <p>
 * The interesting property is the last one below. Every call returns
 * {@code true}, so a marker can be wrapped in {@code assert} and compiled out
 * entirely in production. The instrumentation costs nothing where you cannot
 * afford it.
 */
public class TelemetryExplorationApp {

    private static final int WARMUP = 200;
    private static final int ITERATIONS = 2_000;

    public static void main(final String[] args) {
        trace();
    }

    private static void trace() {
        Telemetry.clear();
        Telemetry.init();

        for (int i = 0; i < WARMUP; i++) {
            handleRequest("user-42");
        }
        // discard the warm-up so the JIT is not part of the measurement
        Telemetry.reset();

        for (int i = 0; i < ITERATIONS; i++) {
            handleRequest("user-" + (i % 500));
        }

        final MixedStatsHolder stats = Telemetry.stopAndGetStats();
        System.out.println("(" + ITERATIONS + " requests traced)");
        stats.getStatsHolder(TimeStatsType.AVERAGE).appendTo(System.out);
        System.out.println();
    }

    /** A stand-in for one request through a small pipeline. */
    private static void handleRequest(final String user) {
        Telemetry.start();
        Telemetry.section("parse");

        final List<Integer> items = parse(user);
        Telemetry.section("transform");

        final int total = transform(items);
        Telemetry.section("serialize");

        serialize(total);
        Telemetry.section("serialize");
    }

    private static List<Integer> parse(final String user) {
        final List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            items.add(user.length() * 31 + i);
        }
        AccurateSleeper.sleepMicroseconds(20);
        return items;
    }

    private static int transform(final List<Integer> items) {
        int acc = 0;
        for (int i = 0; i < 10; i++) {
            for (final int item : items) {
                acc = acc * 31 + item;
            }
        }
        return acc;
    }

    private static void serialize(final int total) {
        AccurateSleeper.sleepMicroseconds(60);
    }

    /** What a concurrent server would call to see every thread at once. */
    static void printAllThreads() {
        for (final Map.Entry<String, MixedStatsHolder> e
                : Telemetry.getStatsFromAllThreads().entrySet()) {
            System.out.println(e.getKey());
            e.getValue().getStatsHolder(TimeStatsType.AVERAGE).appendTo(System.out);
        }
    }
}
