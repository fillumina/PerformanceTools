Performance-Tools
=================

A framework for evaluating and **asserting** the comparative performance of code from
inside an ordinary JUnit or TestNG suite. It measures the code that actually ships, in
the environment that actually runs it, and reports the result as a ratio with a margin
of error, so a regression fails the build instead of being a number somebody has to
notice. See [why this rather than JMH](#why-this-rather-than-jmh).

- __version:__ 2.0-SNAPSHOT (not yet released)
- __last commit:__ 11 June 2019
- __tags:__ `1.0`, `1.1`, `v3.0` (the `v3.0` tag sits on the same commit as the
  current source, so no 2.x has ever been published from this tree)
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Index ##
- [Why this rather than JMH](#why-this-rather-than-jmh)
- [What it measures, and what it does not](#what-it-measures-and-what-it-does-not)
- [Note to version](#note-to-version)
- [Use with maven](#use-with-maven)
- [History](#history)
- [Summary](#summary)
- [Bibliography](#bibliography)
- [Compilation and installation](#compilation-and-installation)
- [Usage Example](#usage)
- [Documentation](./docs/documentation_index.md)

## Note to version ##

- completely rewritten new architecture (compatibility unfortunately lost, sorry)
- powerful statistical analysis functions and data presentation
- improved accuracy and reproducibility of results
- used and allocated memory estimator
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
| Outcome | a number | an assertion that fails the build |

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

### Also worth knowing

* no code generation, and multi-paradigm API (builder, fluent, template)
* automatic discovery of sampling parameters, so you do not tune them by hand
* outlier and convergence filters, which is why there is no warm-up phase to write
* a bulk mode for code that cannot be looped (removing an entry from a map, say)
* used and allocated memory analysis alongside time
* methods and whole applications are in scope, not only microbenchmarks

The cost of the in-situ approach is that you have less control over the tested code, and
the user should be aware of JVM optimisations such as dead code elimination, constant
folding, loop unrolling, lock coalescing, in-lining and code profiling. The framework
reports their effects rather than preventing them.

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
* **Samples are assumed independent.** Timings on a shared machine are autocorrelated,
  so the confidence interval can understate the real uncertainty. The outlier and
  convergence filters are heuristics, not a model. This is the direct price of
  measuring in situ, and it is the regime in which a small reported difference should
  be trusted least.
* **It does not fight the JIT.** Constant folding, dead-code elimination and friends
  still apply. Use `Sink` and `RndRunnable` where you need them excluded.
* **Memory measurement is retained heap, not allocation volume.** A transient
  `new Object()` measures as zero, because nothing is retained. Anything short-lived is
  invisible to it. Treat it as a footprint estimator rather than an allocation counter.

## Use with maven ##

The coordinates below are the intended ones for the 2.0 release. Nothing has been
published to Maven Central from this source tree, so these will not resolve yet;
build and install locally with `mvn clean install` to use them.

This project can be used with maven by adding the following dependencies to your project configuration `pom.xml`.

The core project can be used alone but it's easier to use through templates (just specify the one corresponding to the test unit framework you are using):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools</artifactId>
    <version>2.0</version>
</dependency>
```

This module contains the templates for [JUnit](http://junit.org/):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools-junit</artifactId>
    <version>2.0</version>
</dependency>
```

This module contains the templates for [TestNG](http://testng.org/):

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>performance-tools-testng</artifactId>
    <version>2.0</version>
</dependency>
```

Because templates depend on the core project you only need to specify the right template and the core project will be added automatically.

## History ##

 - **2.0** in preparation, never released: completely rewritten API. The poms
   are still `2.0-SNAPSHOT` and there is no `2.0` tag.
 - **v3.0** tag: present, but it points at the same commit as the current source,
   so it does not correspond to a published artifact.
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
* estimate __used__ and __allocated memory__;
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

The memory test infers used memory rather than reading it: the JVM reports usage at a
granularity usually far coarser than the amount being measured (about 1 MiB on x64 Linux), so
after the test the framework allocates incrementally until the reported figure moves, and the
amount allocated implies the amount used.

A garbage collection during the measurement invalidates the sample. Those are now detected and
the sample is retried rather than averaged in; ten consecutive failures raise an error instead
of returning a meaningless figure.

Note also that this measures *retained* heap, not allocation volume: a transient
`new Object()` measures as zero, because nothing survives it. Treat it as a footprint
estimator. See [what it measures, and what it does not](#what-it-measures-and-what-it-does-not).

## Usage ##

The easiest way to use this library is by extending its template or by using its builder. See the examples package.
