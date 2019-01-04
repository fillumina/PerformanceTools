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

import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.PerformanceBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMHSample_03_States {

    /*
     * Most of the time, you need to maintain some state while the benchmark is
     * running. Since JMH is heavily used to buildConfiguration concurrent benchmarks, we
     * opted for an explicit notion of state-bearing objects.
     *
     * Below are two state objects. Their class names are not essential, it
     * matters they are marked with @State. These objects will be instantiated
     * on demand, and reused during the entire benchmark trial.
     *
     * The important property is that state is always instantiated by one of
     * those benchmark threads which will then have the access to that state.
     * That means you can initialize the fields as if you do that in worker
     * threads (ThreadLocals are yours, etc).
     */

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double x = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }

    /*
     * Benchmark methods can reference the states, and JMH will inject the
     * appropriate states while calling these methods. You can have no states at
     * all, or have only one state, or have multiple states referenced. This
     * makes building multi-threaded benchmark a breeze.
     *
     * For this exercise, we have two methods.
     */

    @Benchmark
    public void measureUnshared(ThreadState state) {
        // All benchmark threads will call in this method.
        //
        // However, since ThreadState is the Scope.Thread, each thread
        // will have it's own copy of the state, and this benchmark
        // will measure unshared case.
        state.x++;
    }

    @Benchmark
    public void measureShared(BenchmarkState state) {
        // All benchmark threads will call in this method.
        //
        // Since BenchmarkState is the Scope.Benchmark, all threads
        // will share the state instance, and we will end up measuring
        // shared case.
        state.x++;
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the drastic difference in shared and unshared cases,
     * because you either contend for single memory location, or not. This effect
     * is more articulated on large machines.
     *
     * You can run this run:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_03 -wi 5 -i 5 -t 4 -f 1
     *    (we requested 5 measurement/warmup iterations, with 4 threads, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
        //main_jmh(args);
        main_pt(args);
    }

    /**
     * Because tests are simple {@link Runnable} classes their status is
     * determined programmatically using standard java behaviors.
     * There is no need for special notations.
     * The {@link Runnable} will be executed as is (eventually it
     * will be cloned to avoid some JVM optimizations but this will not
     * interfere with their variable access).
     */
    public static void main_pt(final String[] args) {

        /**
         * This is a normal class featuring no protection against concurrent
         * accesses.
         */
        class State {
            private double x = Math.PI;

            private double increment() {
                return x++;
            }
        }

        /**
         * That's what jmh's {@code @State(Scope.Benchmark)} is really doing:
         * the access to the method is serialized.
         */
        class GuardedState {
            private double x = Math.PI;

            private synchronized double increment() {
                return x++;
            }
        }

        final GuardedState safelyShared = new GuardedState();

        /**
         * This status is thread local (which is what JMH's
         * {@link org.openjdk.jmh.annotations.Scope#Thread} does.
         */
        final ThreadLocal<State> threadLocal =
                new ThreadLocal<State>() {
            @Override
            protected State initialValue() {
                return new State();
            }

        };

        // TODO fix this test doesn't end
        PerformanceBuilder
                .config()
                    .speedConfig()
                        .setMultiThreading(true)
                    .end()
                .tests()
                    .addTest("synchronized", () -> {
                            Sink.drain(safelyShared.increment());
                        })
                    .addTest("unsafe", new Runnable() {
                            /**
                             * This is NOT private state
                             * (all threads share the same field unsafely).
                             */
                            private final State shared = new State();
                            @Override
                            public void run() {
                                Sink.drain(shared.increment());
                            }
                        })
                    .addTest("thread local", () -> {
                            Sink.drain(threadLocal.get().increment());
                        })
                    .end()
                .end()
            .executeWithFullOutput();
    }

}
