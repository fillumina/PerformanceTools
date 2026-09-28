package com.fillumina.performance.mem;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsCreator;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.unit.MemUnit;
import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.Random;

/**
 * Opt-in comparison of bytes allocated on the calling thread. This counts
 * transient allocations, not the heap retained after garbage collection.
 * Work on other threads is not counted. Use the ratio on the same JVM rather
 * than comparing absolute byte counts between JVM configurations.
 */
public final class ThreadAllocationComparison {
    private static final int WARMUP_ROUNDS = 256;

    private enum Type implements StatsType {
        ALLOCATED_BYTES;

        @Override
        public String toString() {
            return "Thread allocated bytes";
        }
    }

    private ThreadAllocationComparison() {}

    /** Whether the current JVM has enabled per-thread allocation counters. */
    public static boolean isSupported() {
        java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean instanceof ThreadMXBean &&
                ((ThreadMXBean) bean).isThreadAllocatedMemorySupported() &&
                ((ThreadMXBean) bean).isThreadAllocatedMemoryEnabled();
    }

    /**
     * Takes a fixed, interleaved sample set after warming up both operations.
     * Each variant runs once per round. No GC is requested, and a JVM without
     * enabled allocation counters is rejected instead of using a heap estimate.
     * This is an experimental comparison, not an automatic regression gate.
     *
     * @throws IllegalStateException if allocation counters are unavailable
     * @throws IllegalArgumentException if fewer than 33 rounds are requested
     */
    @SuppressWarnings("deprecation") // Thread.threadId() requires Java 19.
    public static Stats compare(Runnable reference, Runnable candidate, int rounds) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(candidate, "candidate");
        if (rounds < 33) {
            throw new IllegalArgumentException("at least 33 rounds are required");
        }
        if (!isSupported()) {
            throw new IllegalStateException("per-thread allocation counters are not enabled on this JVM");
        }
        ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        long threadId = Thread.currentThread().getId();
        bean.getThreadAllocatedBytes(threadId); // initialize the counter outside the sample
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            reference.run();
            candidate.run();
        }
        Random order = new Random(0x414c4c4f43L);
        StatsCreator stats = new StatsCreator(Type.ALLOCATED_BYTES);
        for (int i = 0; i < rounds; i++) {
            long referenceBytes;
            long candidateBytes;
            if (order.nextBoolean()) {
                referenceBytes = measure(bean, threadId, reference);
                candidateBytes = measure(bean, threadId, candidate);
            } else {
                candidateBytes = measure(bean, threadId, candidate);
                referenceBytes = measure(bean, threadId, reference);
            }
            PathNamedMap<SampleValue> values = new PathNamedMap<>(2);
            values.add(new SampleValue(PN.pname("reference"), referenceBytes, MemUnit.B));
            values.add(new SampleValue(PN.pname("candidate"), candidateBytes, MemUnit.B));
            stats.addSample(new Sample(Type.ALLOCATED_BYTES, values));
        }
        return stats.createStats();
    }

    private static long measure(ThreadMXBean bean, long threadId, Runnable workload) {
        long before = bean.getThreadAllocatedBytes(threadId);
        workload.run();
        long after = bean.getThreadAllocatedBytes(threadId);
        if (before < 0 || after < before) {
            throw new IllegalStateException("thread allocation counter is unavailable or has reset");
        }
        return after - before;
    }
}
