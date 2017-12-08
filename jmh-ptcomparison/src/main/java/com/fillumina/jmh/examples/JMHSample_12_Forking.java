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
import com.fillumina.performance.template.PerformanceBuilder;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_12_Forking {

    /*
     * JVMs are notoriously good at profile-guided optimizations. This is bad
     * for benchmarks, because different tests can mix their profiles together,
     * and then render the "uniformly bad" code for every run. Forking (running
     * in a separate process) each run can help to evade this issue.
     *
     * JMH will fork the tests by default.
     */

    /*
     * Suppose we have this simple counter interface, and two implementations.
     * Even though those are semantically the same, from the JVM standpoint,
     * those are distinct classes.
     */

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private volatile int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private volatile int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    /*
     * And this is how we measure it.
     * Note this is susceptible for same issue with loops we mention in previous examples.
     */

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    /*
     * These are two counters.
     */
    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * We first measure the Counter1 alone...
     * Fork(0) helps to run in the same JVM.
     */

    @Benchmark
    @Fork(0)
    public int measure_1_c1() {
        return measure(c1);
    }

    /*
     * Then Counter2...
     */

    @Benchmark
    @Fork(0)
    public int measure_2_c2() {
        return measure(c2);
    }

    /*
     * Then Counter1 again...
     */

    @Benchmark
    @Fork(0)
    public int measure_3_c1_again() {
        return measure(c1);
    }

    /*
     * These two tests have explicit @Fork annotation.
     * JMH takes this annotation as the request to run the run in the forked JVM.
     * It's even simpler to force this behavior for all the tests via the command
     * line option "-f". The forking is default, but we still use the annotation
     * for the consistency.
     *
     * This is the run for Counter1.
     */

    @Benchmark
    @Fork(1)
    public int measure_4_forked_c1() {
        return measure(c1);
    }

    /*
     * ...and this is the run for Counter2.
     */

    @Benchmark
    @Fork(1)
    public int measure_5_forked_c2() {
        return measure(c2);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note that C1 is faster, C2 is slower, but the C1 is slow again! This is because
     * the profiles for C1 and C2 had merged together. Notice how flawless the measurement
     * is for forked runs.
     *
     * You can run this run:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_12 -wi 5 -i 5
     *    (we requested 5 warmup/measurement iterations)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_12_Forking.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
        main_jmh(args);
        main_pt_both(args);
//        main_pt_c1(args);
    }

    /**
     * The two tests use the same code but in two different classes.
     * Depending on which class is evaluated first the JVM will optimize
     * its code and this will give it an advantage over the other.
     * PerformanceTools executes both tests in the warmup phase so that
     * none is given the advantage and correctly reports the two to be equal.
     *
     * @param args
     */
    public static void main_pt_both(final String[] args) {
        final JMHSample_12_Forking test = new JMHSample_12_Forking();
        final Runnable r1 = () -> { SafeSink.drain(test.measure(test.c1)); };
        final Runnable r2 = () -> { SafeSink.drain(test.measure(test.c2)); };

        System.out.println("C1 & C2");

        PerformanceBuilder
            .config()
                .speedConfig()
                .end()
                .tests()
                    .addTest("c1", r1)
                    .addTest("c2", r2)
                .end()
            .end()
            .executeWithFullOutput();

        // this check make c1 and c2 them not evictable
//        SafeSink.drain(executeWithoutOutput.c1.inc());
//        SafeSink.drain(executeWithoutOutput.c2.inc());
    }

    /**
     * If one of the tests is executed alone the JVM might optimize it better
     * and it could result much faster.
     */
    public static void main_pt_c1(final String[] args) {
        final JMHSample_12_Forking test = new JMHSample_12_Forking();

        System.out.println("C1");
        PerformanceBuilder
            .config()
                .speedConfig()
                .end()
                .tests()
                    .addTest("c1", () -> { SafeSink.drain(test.measure(test.c1)); })
                .end()
            .end()
            .executeWithFullOutput();

        System.out.println("C2");
        PerformanceBuilder
            .config()
                .speedConfig()
                .end()
                .tests()
                    .addTest("c2", () -> { SafeSink.drain(test.measure(test.c2)); })
                .end()
            .end()
            .executeWithFullOutput();

        // this check make c1 and c2 them not evictable
        SafeSink.drain(test.c1.inc());
        SafeSink.drain(test.c2.inc());
    }
}
