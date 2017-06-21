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

import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.util.Holder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class JMHSample_06_FixtureLevel {

    double x;

    /*
     * Fixture methods have different levels to control when they should be run.
     * There are at least three Levels available to the user. These are, from
     * top to bottom:
     *
     * Level.Trial: before or after the entire benchmark run (the sequence of iterations)
     * Level.Iteration: before or after the benchmark iteration (the sequence of invocations)
     * Level.Invocation; before or after the benchmark method invocation (WARNING: read the Javadoc before using)
     *
     * Time spent in fixture methods does not count into the performance
     * metrics, so you can use this to do some heavy-lifting.
     */

    @TearDown(Level.Iteration)
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }

    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see measureRight() yields the result, and measureWrong() fires
     * the assert at the end of first iteration! This will not generate the results
     * for measureWrong(). You can also prevent JMH for proceeding further by
     * requiring "fail on error".
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -ea -jar target/benchmarks.jar JMHSample_06 -wi 5 -i 5 -f 1
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     *    You can optionally supply -foe to fail the complete run.
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main_jmh(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_06_FixtureLevel.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .shouldFailOnError(false) // switch to "true" to fail the complete run
                .build();

        new Runner(opt).run();
    }

    public static void main(final String[] args) throws RunnerException {
//        main_jmh(args);
        main_pt(args);
    }

    /**
     * PerformanceTool uses annotations to specify life cycle event
     * methods in the {@link Runnable} tests.
     *
     * @param args
     */
    public static void main_pt(final String[] args) {
        Holder.Double sequenceHolder = new Holder.Double();

        PerformanceBuilder
            .config()
                .speed()
                    // requires only 1 execution of run()
                    .setSamples(1)
                    .setIterations(1)
                .end()
                .tests()
                    .addSingleTest(new Runnable() {
                        @com.fillumina.performance.annotation.SetUp
                        public void setup() {
                            checkSequenceAndIncrement(0);
                        }

                        @com.fillumina.performance.annotation.BeforeSample
                        public void beforeSample() {
                            checkSequenceAndIncrement(1);
                        }

                        /** It's executed only once by configuration. */
                        @Override
                        public void run() {
                            checkSequenceAndIncrement(2);
                        }

                        @com.fillumina.performance.annotation.AfterSample
                        public void afterSample() {
                            checkSequenceAndIncrement(3);
                        }

                        @com.fillumina.performance.annotation.TearDown
                        public void teardown() {
                            checkSequenceAndIncrement(4);
                        }

                        private void checkSequenceAndIncrement(double v) {
                            final double value = sequenceHolder.getValue();
                            if (value != v) {
                                throw new AssertionError(
                                        "value=" + value + ", expected=" + v);
                            }
                            sequenceHolder.setValue(value + 1);
                        }
                    })
                .end()
            .end()
            .exec();

        if (sequenceHolder.getValue() != 5) {
            throw new AssertionError("some event not executed, v=" +
                    sequenceHolder.getValue());
        }
    }

}
