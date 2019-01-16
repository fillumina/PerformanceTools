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

import static com.fillumina.performance.executor.test.Sink.drain;
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
public class JMHSample_10_ConstantFold {

    /*
     * The flip side of dead-code elimination is constant-folding.
     *
     * If JVM realizes the result of the computation is the same no matter what,
     * it can cleverly optimize it. In our case, that means we can move the
     * computation outside of the internal JMH loop.
     *
     * This can be prevented by always reading the inputs from non-final
     * instance fields of @State objects, computing the result based on those
     * values, and follow the rules to prevent DCE.
     */

    // IDEs will say "Oh, you can convert this field to local variable". Don't. Trust. Them.
    // (While this is normally fine advice, it does not work in the context of measuring correctly.)
    private double x = Math.PI;

    // IDEs will probably also say "Look, it could be final". Don't. Trust. Them. Either.
    // (While this is normally fine advice, it does not work in the context of measuring correctly.)
    private final double wrongX = Math.PI;

    @Benchmark
    public double baseline() {
        // simply return the value, this is a baseline
        return Math.PI;
    }

    @Benchmark
    public double measureWrong_1() {
        // This is wrong: the source is predictable, and computation is foldable.
        return Math.log(Math.PI);
    }

    @Benchmark
    public double measureWrong_2() {
        // This is wrong: the source is predictable, and computation is foldable.
        return Math.log(wrongX);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the source is not predictable.
        return Math.log(x);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the unrealistically fast calculation in with measureWrong_*(),
     * while realistic measurement with measureRight().
     *
     * You can run this run:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_10 -i 5 -f 1
     *    (we requested 5 iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jhm(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_10_ConstantFold.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
        main_jhm(args);
        main_pt(args);
    }

    /**
     * {@link Drain} and the use of a {@code volatile} field can avoid
     * constant fold optimization.<br>
     * This is the result of the test:
     * <pre>
        idx  name               ratio vs slower    average time
        0    baseline-final     3.05 +/- 0.03 %    0.837 +/- 0.006 ns/op
        1    final              3.05 +/- 0.03 %    0.837 +/- 0.007 ns/op
        2    baseline-standard  5.39 +/- 0.05 %    1.477 +/- 0.011 ns/op
        3    standard           5.34 +/- 0.04 %    1.465 +/- 0.010 ns/op
        4    baseline-volatile  10.14 +/- 0.08 %   2.780 +/- 0.019 ns/op
        5    volatile           100.00 +/- 0.58 %  27.410 +/- 0.112 ns/op
     * </pre>
     * This is what we can get from this (and by the way the usefulness
     * of the tool for investigations like this one):
     * <ul>
     * <li><b>final</b>
     * field tests have the same speed so the {@code Math.log()}
     * hasn't been executed and has been folded out.
     * <li><b>standard</b>
     * field tests have the same speed as well (although slightly slower)
     * so the {@code Math.log()} hasn't been executed and has been folded out.
     * <li><b>volatile</b>
     * field tests have very different speed so the {@code Math.log()} has
     * been executed and not folded out.
     * </ul>
     */
    public static void main_pt(final String[] args) {

        PerformanceBuilder
                .config()
                    .speedConfig()
                    .end()
                .tests()
                    .addTest("baseline-final", new Runnable() {
                        private final double x = Math.PI;

                        @Override
                        public void run() {
                            drain(x);
                        }
                    })
                    .addTest("final", new Runnable() {
                        private final double x = Math.PI;

                        @Override
                        public void run() {
                            drain(Math.log(x));
                        }
                    })
                    .addTest("baseline-standard", new Runnable() {
                        private double x = Math.PI;

                        @Override
                        public void run() {
                            drain(x);
                        }
                    })
                    .addTest("standard", new Runnable() {
                        private double x = Math.PI;

                        @Override
                        public void run() {
                            drain(Math.log(x));
                        }
                    })
                    .addTest("baseline-volatile", new Runnable() {
                        private volatile double x = Math.PI;

                        @Override
                        public void run() {
                            drain(x);
                        }
                    })
                    .addTest("volatile", new Runnable() {
                        private volatile double x = Math.PI;

                        @Override
                        public void run() {
                            drain(Math.log(x));
                        }
                    })
                .end()
            .end()
            .executeWithFullOutput();
    }

}
