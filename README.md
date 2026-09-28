Performance-Tools
=================

A framework for measuring the comparative performance of code. Its statistical
intervals rely on independent samples; they are not calibrated for every machine.

It has two modes, and they work as a pair. Run it from a `static main` to **explore**: it
reports ratios with standard deviations, uncertainty and pairwise significance for the code
you point it at, which is how you find out *why* something is slow. Then **gate** the same
comparison as an assertion inside an ordinary JUnit or TestNG suite, so the regression
fails the build instead of being a number somebody has to notice.

Throughout, it measures the code that actually ships, in the environment that actually runs
it. See [why this rather than JMH](#why-this-rather-than-jmh) and
[exploring, and then gating](#exploring-and-then-gating).

- __version:__ 4.0.0-SNAPSHOT (v4.0.0 not yet released)
- __previous tags:__ `1.0`, `1.1`, `v3.0` (retained unchanged)
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Index ##
- [Exploring, and then gating](#exploring-and-then-gating)
- [Also worth knowing](#also-worth-knowing)
- [Exploring, and then gating](#exploring-and-then-gating)
- [Tracing a running code path](#tracing-a-running-code-path)
- [What it measures, and what it does not](#what-it-measures-and-what-it-does-not)
- [How the numbers are obtained](./docs/statistics.md)
- [Note to version](#note-to-version)
- [Use with maven](#use-with-maven)
- [History](#history)
- [Summary](#summary)
- [Bibliography](#bibliography)
- [Compilation and installation](#compilation-and-installation)
- [Usage Example](#usage)
- [Documentation](./docs/documentation_index.md)

## Note to version ##

Java 21 is required to build and run every module. Maven compiles against the
Java 21 API using `--release 21`. The optional JMH comparison profile remains
unsupported on modern JDKs.

- completely rewritten new architecture (compatibility unfortunately lost, sorry)
- powerful statistical analysis functions and data presentation
- improved accuracy and reproducibility of results
- experimental calling-thread allocated-byte comparison; GC-based heap estimates retired
- automatic discovery of optimal testing parameters
- special test mode to evaluate the performance of unmodifiable objects
(i.e. to estimate the speed of the removal of one entry from a map of given size)
- in-testing data presentation with ETA and partial results preview
of results
- outliers and steady state filters remove the need to have a warm-up phase


## Why this rather than JMH ##

[JMH](https://openjdk.java.net/projects/code-tools/jmh/) is an outstanding tool and this
project does not try to replace it. JMH isolates the code under test in a tightly
controlled environment, fights dead-code elimination, constant folding and loop
unrolling, and forks a fresh JVM per iteration. If you want to know **how many
nanoseconds something takes**, JMH is the right tool and an absolute figure produced
here is not a substitute for it.

The two answer different questions, and the difference is structural rather than a
matter of degree:

| | JMH | PerformanceTools |
|---|---|---|
| Question | how fast is this? | did my change make it slower? |
| Artefact | a synthetic measurement | a verdict in your test suite |
| Environment | isolated, forked, idealised | the system the code ships on |
| Code measured | a benchmark method | the real code path |
| Outcome | a number to interpret | a report to read, or an assertion to enforce |

This project is built for the second column. It runs **inside your test suite**,
against code that actually ships, in the environment that actually runs it, and it can
**assert** — so a regression becomes a build failure rather than a number somebody has
to remember to look at. A synthetic benchmark cannot do that, because what it would
have to measure is not the thing that regressed.

### It is aimed at comparison, not at absolute figures

The reliable output is the ratio, not the nanoseconds. Compared tests are interleaved
and their order shuffled on every round, so a disturbance — thermal throttling, a
frequency change, a scheduler move, a background task — hits both variants and largely
cancels. A constant canary workload is also timed between samples; if the canary
degrades, the CPU is treated as down-clocked and the run is paused to cool.

Measured on a 13th-generation Intel i9-13900HX (hybrid cores, CPU scaling reported at
39%), comparing two workloads in a true 20:25 ratio:

| Run | measured ratio | reported margin of error |
|---|---|---|
| 1 | 80.98% | 0.84% |
| 2 | 80.96% | 0.56% |
| 3 | 80.40% | 0.79% |

The measured figures and transcripts below predate independent validation.
They illustrate the output, not a measurement of the current version.
Three independent JVM runs, a spread under one percentage point against a ground truth
of 80.00%, with the framework reporting an error estimate that brackets its own spread.
The absolute figures moved by roughly 0.8% between those runs; the ratio barely did.
That asymmetry is the behaviour the design exists to produce.

### It measures the shipped artefact

`ShippedCodeGateTest` in the examples is a gate over shipped code rather than over
synthetic loops: it compares this library's own `IndexedHashMap` against
`java.util.HashMap`, the class real applications actually use. On the machine above the
shipped map is about **29% slower** — 643 against 499 ns/op, resolved over 33 samples
with a 1.5% margin. That is not a flattering result, and it is reported rather than
asserted away, which is the point. A synthetic benchmark would not have surfaced it, and
a tool that reports an unflattering truth about your own code is worth more than one
that flatters it.

## Exploring, and then gating ##

The assertion use is the one this project is built around, but it is not the only one, and
the other mode comes first in practice: a `static main` that produces information rather
than a verdict, for when you do not yet know what to assert.

`LookupExplorationApp` in the examples measures the same lookup two ways, at an easy position
and at the worst position, and deliberately declares no assertions. This is one real run,
trimmed only where the output repeats itself. It opens with the machine and the resolved
plan, so you can see what was decided on your behalf and which filters are in force:

```
CONFIGURATION
=============

# Date: 2026-09-27 14:17:45+0200
# CPU: null; 32 "procs"
# OS: Linux; 7.0.0-111031-tuxedo; amd64
# JVM: Ubuntu; 25.0.4.1

Experiment Plan
---------------
tests:
 linear_easy
 hash_easy
 linear_hard
 hash_hard

Speed
-----
concurrencyLevel       : 1
timeout                : 120.0000 s
coolDownCpu            : true
samples                : 33
iterations             : auto
millisecondsPerSample  : 250
filterSamples          : [OutlierEliminatorFilter{stdevFactor=3.0},
                          ConvergenceFilter{minStableSequenceLength=33,
                          minUnoptimizedSequnenceLength=10, stdevFactor=3.0}]
maxPercentageMargin    : 5.000 %
confidence             : 99.000 %
```

Then progress, live, with an ETA. The run pauses to cool the CPU whenever the canary
workload shows the machine is down-clocked, which is what those `CPU is cool` markers are:

```
EXECUTION
=========

 1 / 33 ETA=           --  iterations= 23325260, 154770077, 1274098, 109909356   required  CPU is cool
 2 / 33 ETA=               iterations= 24551038, 161994820, 1271050, 128364477   required
 3 / 33 ETA=          1 m  iterations= 23915348, 158046970, 1267775, 132987812   required  CPU is cool
```

And the results, in two views of the same samples:

```
Average Time:
Required measure confidence  :  99.000 %
Max ratio percentage error   :  3.936 %
ANOVA                        :  1.0

idx  name         ratio vs slower    average time             stdev         uncertainty  smpl  significance
0    linear_easy  5.44 +/- 0.18 %    12.210 +/- 0.200 ns/op   0.446 ns/op   1.637 %      33    0.999
1    hash_easy    0.75 +/- 0.03 %    1.673 +/- 0.038 ns/op    0.085 ns/op   2.268 %      33    0.999
2    linear_hard  100.08 +/- 3.94 %  224.572 +/- 6.155 ns/op  13.727 ns/op  2.741 %      33    0.100
3    hash_hard    0.91 +/- 0.03 %    2.034 +/- 0.041 ns/op    0.091 ns/op   2.017 %      33    0.999

Throughput:
Required measure confidence  :  99.000 %
Max ratio percentage error   :  2.895 %
ANOVA                        :  1.0

idx  name         ratio vs faster    throughput                stdev         uncertainty  smpl  significance
0    linear_easy  13.69 +/- 0.35 %   82.002 +/- 1.286 Mop/s    2.868 Mop/s   1.569 %      33    0.999
1    hash_easy    100.04 +/- 2.89 %  599.173 +/- 12.079 Mop/s  26.939 Mop/s  2.016 %      33    0.100
2    linear_hard  0.75 +/- 0.02 %    4.467 +/- 0.109 Mop/s     0.243 Mop/s   2.443 %      33    0.999
3    hash_hard    82.25 +/- 2.27 %   492.637 +/- 9.030 Mop/s   20.137 Mop/s  1.833 %      33    0.999

Performance test total time:  1m 32.427s
```

This is diagnostic, not merely comparative. The linear scan is 7x slower near the front
(12.210 against 1.673 ns/op) and 110x slower at the back (224.572 against 2.034), while
the map stays essentially flat regardless of position. That identifies the cost as the
*scan*, which is the actual answer to "why is this lookup slow" — and a single absolute
number would never have told you, because both positions would simply have returned a
number.

The two columns to read are `significance`, which separates a real difference from noise, and
`uncertainty`, which tells you whether the difference is worth acting on at all. Note that
this run reports a 3.936% maximum ratio error where an earlier run of the same test on the
same machine reported 0.675%: the margin is a property of the machine at that moment, which
is exactly why the ratio is the portable quantity and the absolute figures are not.

So the two modes are a workflow rather than a choice: **explore with a `main` until you
know what the real difference is, then encode it as an assertion** so it cannot silently
come back. `SearchTypePerformanceTest` is the second half of exactly this example. Debugging
a live application is also supported in-process through `Telemetry`, which samples named
sections of a running system.

## Also worth knowing ##

* no code generation, and multi-paradigm API (builder, fluent, template)
* automatic discovery of sampling parameters, so you do not tune them by hand
* outlier and convergence filters, which is why there is no warm-up phase to write
* a bulk mode for code that cannot be looped (removing an entry from a map, say)
* opt-in calling-thread allocated-byte comparison alongside timing tests
* methods and whole applications are in scope, not only microbenchmarks

The cost of the in-situ approach is that you have less control over the tested code, and
the user should be aware of JVM optimisations such as dead code elimination, constant
folding, loop unrolling, lock coalescing, in-lining and code profiling. The framework
reports their effects rather than preventing them.

## Tracing a running code path ##

The third leg, and the one that reaches beyond isolated snippets. `Telemetry` does not need
a benchmark at all: you drop a marker at a point of interest and it reports what share of the
time was spent there. That is how you find out where a long algorithm actually spends its
time, without first writing a benchmark for each stage.

```java
private static void handleRequest(final String user) {
    Telemetry.start();
    Telemetry.section("parse");
    final List<Integer> items = parse(user);
    Telemetry.section("transform");
    final int total = transform(items);
    Telemetry.section("serialize");
}
```

Each call records the time elapsed since the previous one, so the sequence of calls is what
defines the sections. A stage that runs a known number of times declares the count,
`Telemetry.section("retry", 10)`, so its share is of the work rather than of the calls.

`TelemetryExplorationApp` in the examples traces 2,000 requests through a small pipeline:

```
idx  name       ratio vs slower    mean                          stdev            uncertainty  smpl  significance
0    parse      0.10 +/- 0.01 %    29.740 +/- 4.181 ns/op        72.597 ns/op     14.060 %     2000  0.999
1    transform  68.88 +/- 2.80 %   20829.270 +/- 117.969 ns/op   2048.147 ns/op   0.566 %      2000  0.999
2    serialize  100.16 +/- 5.70 %  30287.089 +/- 1219.459 ns/op  29941.682 ns/op  4.026 %      4000  0.100
```

Parsing is 0.1% of the request and can be ignored; transformation and serialisation are both
material, and the figure tells you which to attack. Note that `serialize` shows 4,000
samples for 2,000 requests, because the section is closed twice per request and the
iteration count is accounted for.

Markers can sit anywhere, including inside code you would rather not restructure, and each
call returns `true` so that a marker can be wrapped in `assert` and compiled out entirely
in production. It also works per-thread, so a server can report where one request type spends
its time while others are in flight. The statistics, the filters and the interval estimates
are the same machinery as the other two modes.

## What it measures, and what it does not ##

Stated plainly, because the argument above only holds if these are respected.

* **Absolutes are session-local.** `28.7 us/op` describes that run, on that machine. It
  is not comparable across machines or across runs. Compare within a session.
* **The ratio is the reliable quantity.** It is what the interleaving, the filters and
  the stopping rule all exist to deliver.
* **No CPU pinning, and no awareness of core types.** On a hybrid CPU the operating
  system can migrate a test onto an efficiency core mid-run, a two to three times
  penalty. Neither the interleaving nor the canary corrects that, because the canary is
  a separate workload that may land on a different core. It is absorbed statistically —
  more samples, wider margin — rather than mechanically. When chasing a small delta,
  pin your threads.
* **Adaptive results use fresh validation samples.** Exploration still filters and
  stops adaptively, but the reported statistics use at least 33 new, unfiltered
  samples. A wide ratio margin or obvious lag-one dependence fails the run. This
  costs additional time; fixed-sample tests retain their existing behavior.
* **Independence is not guaranteed.** Strongly correlated timings can evade the
  lag-one check, and the confidence interval can still understate uncertainty on a
  shared machine. The percentage is conditional on the independent-sample model,
  not a calibrated guarantee for every environment.
* **It does not fight the JIT.** Constant folding, dead-code elimination and friends
  still apply. Use `Sink` and `RndRunnable` where you need them excluded.
* **GC-based memory estimates have been retired.** The opt-in allocated-byte
  comparison below measures allocations on the calling thread, not retained
  heap usage.

## Use with maven ##

The coordinates below are for the current development build. The v4.0.0 release
has not been tagged yet; build and install locally with `mvn clean install` to
use them.

This project can be used with maven by adding the following dependencies to your project configuration `pom.xml`.

The core project can be used alone but it's easier to use through templates (just specify the one corresponding to the test unit framework you are using):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

This module contains the templates for [JUnit](http://junit.org/):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools-junit</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

This module contains the templates for [TestNG](http://testng.org/):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools-testng</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Because templates depend on the core project you only need to specify the right template and the core project will be added automatically.

## History ##

 - **v4.0.0** in preparation: Java 21 is required, the API has changed, and
   GC-based memory estimates have been retired. Until the release, the Maven
   version is `4.0.0-SNAPSHOT`; do not tag it before CI verification.
 - **v3.0**: the last tag made by the maintainer, retained unchanged.
 - **2.0**: developed as a snapshot but never tagged as a release.
 - **1.1** tagged 1 August 2014
 - **1.0** tagged 31 July 2014: first version released to maven central


## Summary ##


### Challenges

Java runs on a variety of platforms (from mobiles to mainframes) and there are many different virtual machines (JVM), garbage collector (GC) algorithms and optimization strategies available.  

Just in time executor (JIT) continuously optimize the running code resulting in code performance varying considerably.

A benchmark is often a measure of an extreme and very unusual
case: the code under test is executed continuously for a long time giving the best chances to the JVM to optimize it, to the memory working set to be cached and to the CPU to perform the best internal optimizations and caching.

This is probably not your typical use of the code under test so an informed evaluation must be taken over synthetic figures.

Very often complex code will perform outstandingly on benchmarks but very poorly in real world applications where their memory and code footprint might not being executed enough to benefit from the optimizations. An unoptimized
complex code will always perform poorly (although that would not impact your code greatly).

Another factor of importance is the memory usage of the code. If a code uses a lot of heap memory (some JVM uses the stack in some cases now), it will required some time to be cleaned up. This time is rarely accounted for in benchmarks but might impact a running code because it will be executed in a different time.
Different algorithms have different trade-offs and should be used on different situations. 

Benchmarking is only a **tool that helps to investigate and evaluate different choices**.


### Comparing is better than scoring

Instead of just measuring the absolute speed of some code in a tightly controlled environment (which involves a very high level of carefulness and isolation from external disturbances) a different approach is to take the measurements of two or more codes and consider the relative speed between them.
This technique has the following advantages over a single measurement:

* Relative measures are more reproducible and stable among different systems;
* It's far more informative to know the speed of some code relatively to some other code than just it's absolute speed (which most of the time is quite useless and depends on the system over which the benchmark is executed);
* It's more robust against environment disturbances (CPU-speed fluctuations, heat throttling, SO scheduler, resource racing, background activities...) because both tests are executed at the same time (interleaved to average those disturbances).


### Features

It allow to:

* test codes in a __single__ and in __multi__ threaded
environment (with various sub types);
* specify __parameters__ to build complex tests;
* use a __sequences__ to repeat a test with different values;
* easily __export__ the performances in a full configurable way;
* __assert conditions__ with given tolerances on the tests so
to be used in unit tests;
* execute __everywhere__ (no need for a separate environment);
* The structure of the API is very open and interface centric so that it is
__highly customizable and expandable__;
* compare calling-thread allocated bytes with an experimental opt-in API;
* be used with two different paradigms: __fluent interface__ (builders) and __templates__;
* It heavily relies on __statistics__ to produce solid results;
* It supports __bulk tests__ to test codes that cannot be looped.


### Bulk test

Some code can be executed only once (i.e. the removal of an entry from a map) and so they cannot be estimated by repeating it in a tight loop.
To overcome this problem the same test can be executed on a collection of equal objects exactly once. A special test template can be extended to accomplish exactly that
(see `com.fillumina.performance.speed.sample.BulkTestable`).

## Bibliography ##

Doing a benchmarking tool is an incredible difficult task and of course a deep research is needed to avoid at least the most common pitfalls.

About benchmarking:

* [Brian Goetz: Java theory and practice: Anatomy of a flawed microbenchmark]
(http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html)
* [Brian Goetz: Java theory and practice: Dynamic compilation and performance
measurement]
(http://www.ibm.com/developerworks/library/j-jtp12214/)
* [Brent Boyer (2008): Java benchmarking article]
(http://www.ellipticgroup.com/html/benchmarkingArticle.html)
* [Brent Boyer (2008): Robust Java benchmarking, Part 1: Issues]
(http://www.ibm.com/developerworks/java/library/j-benchmark1/index.html)
* [Brent Boyer (2008): Robust Java benchmarking, Part 2: Statistics and solutions]
(https://www.ibm.com/developerworks/java/library/j-benchmark2/)
* [Julien Ponge 2014: Avoiding Benchmarking Pitfalls on the JVM]
(http://www.oracle.com/technetwork/articles/java/architect-benchmarking-2266277.html)
* A. Georges, D. Buytaert, L. Eeckhout, "Statistically Rigorous Java Performance
Evaluation", Department of Electronics and Information Systems,
Ghent University, Belgium, OOPSLA October 21-25 2007
([JavaStats](https://www.elis.ugent.be/en/JavaStats))
* [Peter Sestoft](https://www.itu.dk/~sestoft/) (sestoft@itu.dk),
"Microbenchmarks in Java and C#",
IT University of Copenhagen, Denmark, Version 0.8.0 of 2015-09-16
* [Aleksey Shipilёv: Nanotrusting the Nanotime]
(https://shipilev.net/blog/2014/nanotrusting-nanotime/)

About JHM:

* [Mikhail Vorontsov: Introduction to JMH](http://java-performance.info/jmh/)
* [Java Micro Benchmark with JMH](http://javapapers.com/java/java-micro-benchmark-with-jmh/)



## Compilation and installation ##

This is a multi-artifact maven project so you can build and install the whole project by issuing the standard

    mvn clean install

from the main directory. The default build covers four modules: `performance-tools` (the core),
`performance-tools-junit`, `performance-tools-testng` and `performance-tools-examples`.

The actual API core is in the folder `performance-tools` and its jar should be in `performance-tools/target`.

The JUnit templates jar is in the folder `performance-tools-junit/target`.

### The JMH comparison module

The `jmh-ptcomparison` module is **not** part of the default build. It vendors a selection of
Oracle's JMH samples, kept in this repository precisely so both tools can be built together and
compared on the same code, in the same JVM, on the same machine, at the same moment. That
shared build is the point: split across two repositories the versions drift and the result is
a comparison of two unrelated runs rather than a comparison of anything.

It is behind an opt-in profile so it can never break an ordinary build:

    mvn -Pjmh-comparison clean package

Only the 22 samples that run a real side-by-side are kept; each has a `main_pt` method that
runs the same problem through this framework. The remaining Oracle tutorial samples were
removed. The module has no tests and never runs as part of `mvn verify` without the profile.


### Failing tests

__Please note that performance tests cannot be assured to be stable under any possible condition!__

In particular unit tests environments are more prone to failure especially with strict tolerances. In unit tests execute avoid executing tests in parallel.
An assertion that matches no measure fails the gate rather than silently passing. If a
ratio interval is not valid, adaptive sampling continues until it becomes valid or the
run times out; a fixed-sample run fails instead. Exceptions from timed worker tasks
fail the test. A parallel task needs at least as many pool threads as simultaneous
workers, otherwise it is rejected before execution.

The GC-based `usedMemConfig()`, `allocatedMemConfig()`, `usedMemory()`,
`allocatedMemory()`, `MemStatsProducer`, `MemAnalyzer`, and `AssertMem` APIs have
been removed. Templates now run only timing tests. Use the experimental
allocated-byte comparison below when that narrower metric is appropriate.
It does not measure retained heap usage.

### Experimental allocated-byte comparison

`ThreadAllocationComparison.compare(reference, candidate, 33)` takes fixed,
randomized-order rounds after warming both operations. It returns `Stats` with
`reference` and `candidate` in bytes. The counters must be enabled on the JVM;
`isSupported()` checks that. It does not invoke garbage collection.

```java
Stats result = ThreadAllocationComparison.compare(reference, candidate, 33);
MeasureRatio change = result.getRatio("candidate", "reference", Ratio.P_99);
```

This opt-in path counts bytes allocated on the *calling thread*, including
short-lived objects. It excludes allocations made by threads started or used
by the workload. It does not measure retained memory or GC cost. The ratio
interval assumes independent samples; compilation changes or differential
workload drift can defeat a same-run reference. It is an experiment, not yet
a CI gate: do not treat a significant interval from one run as a calibrated
false-positive rate on a server.

In a local JDK 25 probe, 12 default-flag, four compact-header, two
interpreted, three Serial-GC, and four simultaneously running JVM processes
each ran 20 equal-workload, doubled-allocation, and
one-extra-array-after-sixteen comparisons at 99% nominal confidence and
33 rounds. That is 500 comparisons per case, with zero false gates, missed
increases, or invalid intervals. The probes use synthetic escaping arrays;
these results do not establish reliability on real workloads or remote CI.
`ThreadAllocationCalibration` reproduces the probe from `target/test-classes`.
The manual [allocation calibration workflow](.github/workflows/allocation-calibration.yml)
runs five fresh JVMs on JDK 21 and writes these counts to the job
summary. Unsupported counters or crashes fail that job; statistical misses are
recorded, not treated as a release gate. It does not yet include an application
workload. To run the same probe locally:

```sh
mvn -B -pl performance-tools -am -DskipTests test-compile
bash scripts/allocation-calibration.sh
```

The existing build workflow also runs its full suite on pushes to `master`;
the manual calibration workflow does not change that trigger.

## Usage ##

The easiest way to use this library is by extending its template or by using its builder. See the examples package.
