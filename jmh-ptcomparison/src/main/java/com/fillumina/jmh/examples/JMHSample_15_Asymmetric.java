/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.fillumina.jmh.examples;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.time.sample.iterator.ParallelTest;
import com.fillumina.performance.template.PerformanceBuilder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_15_Asymmetric {

    /*
     * So far all the tests were symmetric: the same code was executed in all the threads.
     * At times, you need the asymmetric executeWithoutOutput. JMH provides this with the notion of @Group,
     * which can bind several methods together, and all the threads are distributed among
     * the executeWithoutOutput methods.
     *
     * Each execution group contains of one or more threads. Each thread within a particular
     * execution group executes one of @Group-annotated @Benchmark methods. Multiple execution
     * groups may participate in the run. The total thread count in the run is rounded to the
     * execution group size, which will only allow the full execution groups.
     *
     * Note that two state scopes: Scope.Benchmark and Scope.Thread are not covering all
     * the use cases here -- you either share everything in the state, or share nothing.
     * To break this, we have the middle ground Scope.Group, which marks the state to be
     * shared within the execution group, but not among the execution groups.
     *
     * Putting this all together, the example below means:
     *  a) define the execution group "g", with 3 threads executing inc(), and 1 thread
     *     executing get(), 4 threads per group in total;
     *  b) if we run this executeWithoutOutput case with 4 threads, then we will have a single execution
     *     group. Generally, running with 4*N threads will create N execution groups, etc.;
     *  c) each execution group has one @State instance to share: that is, execution groups
     *     share the counter within the group, but not across the groups.
     */

    private AtomicInteger counter;

    @Setup
    public void up() {
        counter = new AtomicInteger();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(3)
    public int inc() {
        return counter.incrementAndGet();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public int get() {
        return counter.get();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will have the distinct metrics for inc() and get() from this run.
     *
     * You can run this executeWithoutOutput:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_15 -wi 5 -i 5 -f 1
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_15_Asymmetric.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
        main_jmh(args);
        main_pt(args);
    }

    /**
     * PT provides a simple way to define groups within a executeWithoutOutput
     * @param args
     */
    public static void main_pt(final String[] args) {
        JMHSample_15_Asymmetric test = new JMHSample_15_Asymmetric();

        test.up();

        PerformanceBuilder
            .config()
                .speedConfig()
                .end()
                .tests()
                    .addTest(new ParallelTest()
                        .addTask("inc", 3, () -> { SafeSink.drain(test.inc()); })
                        .addTask("get", 1, () -> { SafeSink.drain(test.get()); }))
                .end()
            .end()
            .executeWithFullOutput();

        SafeSink.drain(test.counter.get());
    }
}
