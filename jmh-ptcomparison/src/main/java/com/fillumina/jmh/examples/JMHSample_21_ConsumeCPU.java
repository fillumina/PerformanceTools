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

import com.fillumina.performance.infrastructure.CpuBurner;
import com.fillumina.performance.infrastructure.DoubleLfsrRunnable;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.RndRunnable;
import com.fillumina.performance.template.PerformanceBuilder;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_21_ConsumeCPU {

    /*
     * At times you require the test to burn some of the cycles doing nothing.
     * In many cases, you *do* want to burn the cycles instead of waiting.
     *
     * For these occasions, we have the infrastructure support. Blackholes
     * can not only consume the values, but also the time! Run this test
     * to get familiar with this part of JMH.
     *
     * (Note we use static method because most of the use cases are deep
     * within the testing code, and propagating blackholes is tedious).
     */

    @Benchmark
    public void consume_0000() {
        Blackhole.consumeCPU(0);
    }

    @Benchmark
    public void consume_0001() {
        Blackhole.consumeCPU(1);
    }

    @Benchmark
    public void consume_0002() {
        Blackhole.consumeCPU(2);
    }

    @Benchmark
    public void consume_0004() {
        Blackhole.consumeCPU(4);
    }

    @Benchmark
    public void consume_0008() {
        Blackhole.consumeCPU(8);
    }

    @Benchmark
    public void consume_0016() {
        Blackhole.consumeCPU(16);
    }

    @Benchmark
    public void consume_0032() {
        Blackhole.consumeCPU(32);
    }

    @Benchmark
    public void consume_0064() {
        Blackhole.consumeCPU(64);
    }

    @Benchmark
    public void consume_0128() {
        Blackhole.consumeCPU(128);
    }

    @Benchmark
    public void consume_0256() {
        Blackhole.consumeCPU(256);
    }

    @Benchmark
    public void consume_0512() {
        Blackhole.consumeCPU(512);
    }

    @Benchmark
    public void consume_1024() {
        Blackhole.consumeCPU(1024);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note the single token is just a few cycles, and the more tokens
     * you request, then more work is spent (almost linearly)
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_21 -w 1 -i 5 -f 1
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_21_ConsumeCPU.class.getSimpleName())
                .warmupIterations(1)
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
     * To linearly consume CPU cycles PerformanceTools has several
     * {@link Runnable} available (nothing forbid to add new ones of course):
     * <ul>
     * <li>{@link com.fillumina.performance.infrastructure.LfsrRunnable}
     * implements the LFSR algorithm which is stable and constant time;
     * <li>{@link com.fillumina.performance.infrastructure.DoubleLfsrRunnable}
     * implements two consecutive calls to LFSR algorithm. it is twice slower
     * than {@link LfsrRunnable} (use for accuracy tests).
     * <li>{@link com.fillumina.performance.infrastructure.CpuBurner}
     * repeat the LFSR algorith the given number of times.
     * <li>{@link com.fillumina.performance.infrastructure.RndRunnable}
     * another pseudo random algorithm slightly slower than LFSR.
     * </ul>
     * All those algorithm use no extra memory and are quite stable.
     */
    public static void main_pt(final String[] args) {
        JMHSample_21_ConsumeCPU test = new JMHSample_21_ConsumeCPU();

        PerformanceBuilder
            .config()
                .speed()
                .end()
                .tests()
                    .addTest("consume0", () -> { test.consume_0000(); })
                    .addTest("consume1", () -> { test.consume_0001(); })
                    .addTest("consume2", () -> { test.consume_0002(); })
                    .addTest("consume4", () -> { test.consume_0004(); })
                    .addTest("consume8", () -> { test.consume_0008(); })
                    .addTest("consume16", () -> { test.consume_0016(); })
                    .addTest("consume32", () -> { test.consume_0032(); })
                    .addTest("consume64", () -> { test.consume_0064(); })
                    .addTest("consume128", () -> { test.consume_0128(); })
                    .addTest("consume256", () -> { test.consume_0256(); })
                    .addTest("consume512", () -> { test.consume_0512(); })
                    .addTest("consume1024", () -> { test.consume_1024(); })
                .end()
            .end()
            .exec();
    }

    public static void main_pt_own(final String[] args) {
        PerformanceBuilder
            .config()
                .speed()
                .end()
                .tests()
                    .addTest("single lfsr", new LfsrRunnable())
                    .addTest("double lfsr", new DoubleLfsrRunnable())
                    .addTest("burn 2", () -> {CpuBurner.burn(2);} )
                    .addTest("burn 4", () -> {CpuBurner.burn(4);} )
                    .addTest("burn 8", () -> {CpuBurner.burn(8);} )
                    .addTest("burn 16", () -> {CpuBurner.burn(16);} )
                    .addTest("burn 32", () -> {CpuBurner.burn(32);} )
                    .addTest("burn 64", () -> {CpuBurner.burn(64);} )
                    .addTest("xorshift rnd", new RndRunnable() )
                .end()
            .end()
            .exec();
    }

}
