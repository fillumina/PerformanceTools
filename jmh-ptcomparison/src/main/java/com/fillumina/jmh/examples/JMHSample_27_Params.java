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

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.template.PerformanceBuilder;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class JMHSample_27_Params {

    /**
     * In many cases, the experiments require walking the configuration space
     * for a benchmark. This is needed for additional control, or investigating
     * how the workload performance changes with different settings.
     */

    @Param({"1", "31", "65", "101", "103"})
    public int arg;

    @Param({"0", "1", "2", "4", "8", "16", "32"})
    public int certainty;

    @Benchmark
    public boolean bench() {
        return BigInteger.valueOf(arg).isProbablePrime(certainty);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note the performance is different with different parameters.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_27
     *
     *    You can juggle parameters through the command line, e.g. with "-p arg=41,42"
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_27_Params.class.getSimpleName())
//                .param("arg", "41", "42") // Use this to selectively constrain/override parameters
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
//        main_jmh(args);
        main_pt(args);
    }

    /**
     * Parameters can be any objects and can be inserted in many different
     * ways. Other than parameters PT supports sequences which change
     * tests groups.
     */
    public static void main_pt(final String[] args) {
        PerformanceBuilder
            .config()
                .speed()
                    .setIterations(1_000, 10_000, 100_000)
                    .setTimeout(10, TimeUnit.MINUTES)
                .end()
                .tests()
                    .addTest("bench", new Runnable() {
                        // because it's different from jmh's @Param
                        @com.fillumina.performance.annotation.Param
                        private int certainty;

                        @com.fillumina.performance.annotation.Param
                        private int arg;

                        @Override
                        public void run() {
                            Sink.drain(BigInteger.valueOf(arg)
                                    .isProbablePrime(certainty));
                        }
                    })
                    .addParameter("certainty")
                        .values(0, 1/*, 2, 4, 8, 16, 32*/).end()
                        .end()
                    .addParameter("arg")
                        .values(1, 31/*, 65, 101, 103*/).end()
                    .end() // TODO why 2 end()?
                .end()
            .end()
            .exec();

    }
}
