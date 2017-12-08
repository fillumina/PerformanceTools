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

import static com.fillumina.jmh.examples.JMHSample_08_DeadCode.main_jhm;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.template.PerformanceBuilder;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHSample_09_Blackholes {

    /*
     * Should your benchmark require returning multiple results, you have to
     * consider two options (detailed below).
     *
     * NOTE: If you are only producing a single result, it is more readable to
     * use the implicit return, as in JMHSample_08_DeadCode. Do not make your benchmark
     * code less readable with explicit Blackholes!
     */

    double x1 = Math.PI;
    double x2 = Math.PI * 2;

    /*
     * Baseline measurement: how much single Math.log costs.
     */

    @Benchmark
    public double baseline() {
        return Math.log(x1);
    }

    /*
     * While the Math.log(x2) computation is intact, Math.log(x1)
     * is redundant and optimized out.
     */

    @Benchmark
    public double measureWrong() {
        Math.log(x1);
        return Math.log(x2);
    }

    /*
     * This demonstrates Option A:
     *
     * Merge multiple results into one and return it.
     * This is OK when is computation is relatively heavyweight, and merging
     * the results does not offset the results much.
     */

    @Benchmark
    public double measureRight_1() {
        return Math.log(x1) + Math.log(x2);
    }

    /*
     * This demonstrates Option B:
     *
     * Use explicit Blackhole objects, and sink the values there.
     * (Background: Blackhole is just another @State object, bundled with JMH).
     */

    @Benchmark
    public void measureRight_2(Blackhole bh) {
        bh.consume(Math.log(x1));
        bh.consume(Math.log(x2));
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will see measureWrong() running on-par with baseline().
     * Both measureRight() are measuring twice the baseline, so the logs are intact.
     *
     * You can run this executeWithoutOutput:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_09 -wi 5 -i 5 -f 1
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_09_Blackholes.class.getSimpleName())
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
     * To be sure that a result is consumed and the code is thus not evicted
 PerformanceTools uses the same mechanism as JMH: a consuming class
 named SafeSink. SafeSink is created to be as light and effective as
 possible (consider that its execution time is necessarily accounted in
 the executeWithoutOutput total time).
 <p>
     * It's worth noting that {@link Blackhole#consume(double)} methods
     * read from a volatile variable which might impact the performances
     * of a multi-threaded task.
     * @see https://brooker.co.za/blog/2012/09/10/volatile.html
     */
    public static void main_pt(final String[] args) {

        PerformanceBuilder
            .config()
                .speedConfig()
                .end()
                .tests()
                    .addTest("cos", new Runnable() {
                        private volatile double x = Math.PI;

                        @Override
                        public void run() {
                            SafeSink.drain(Math.cos(x));
                        }
                    })
                    .addTest("sin + cos", new Runnable() {
                        private volatile double x = Math.PI;

                        @Override
                        public void run() {
                            SafeSink.drain(Math.cos(x) + Math.sin(x));
                        }
                    })
                    .addTest("sin & cos", new Runnable() {
                        private volatile double x = Math.PI;

                        @Override
                        public void run() {
                            SafeSink.drain(Math.cos(x));
                            SafeSink.drain(Math.sin(x));
                        }
                    })
                .end()
            .end()
            .executeWithFullOutput();
    }
}
