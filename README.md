Performance-Tools
=================

A framework based on a very configurable and expandable API to easily execute code performance speed and memory tests in a JVM agnostic way and to analyze the results with a powerful set of statistical functions. It aims in particular towards **micro-benchmarks**.

- __version:__ 2.0-SNAPSHOT (not yet released)
- __last commit:__ 11 June 2019
- __tags:__ `1.0`, `1.1`, `v3.0` (the `v3.0` tag sits on the same commit as the
  current source, so no 2.x has ever been published from this tree)
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Index ##
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


## Differences from JMH

__[JMH](http://openjdk.java.net/projects/code-tools/jmh/)__ is an outstanding
and accurate tool characterized by the fact that it analyzes the code
in a very controlled environment to avoid common pitfalls and external
influences. It is based on a deep knowledge of the JVM internals that it
uses to isolate the code under test and to make it run in ideal conditions.

__PerformnaceTools__ on the other hand tries to make minimal assumptions on the JVM internals and uses advanced statistics to extract a whole bunch of useful information. That means that you can run your test in whatever environment you need and results will apply to those particular conditions rather than being referred to a synthetic abstraction.

It doesn't fight optimizations or even pitfalls (which by the way you might expect in real world code), it just tries to
**evaluate** and **report** their *effects*.
In fact it should not be considered a **score generator** as much as a tool to investigate the comparative performances of different codes which is much more reliable and informative of a bare number.
It's a tool by which it is possible to evaluate quickly and reliably if a modification to an algorithm has improved its speed or not.

### PROS:

* it can be executed everywhere, even in unit tests
([JUnit](http://junit.org/) and [TestNG](http://testng.org/) supported
natively, other frameworks are easy to add)!
* doesn't generate code
* it supports complex assertions (so speed performances can be included into unit tests)
* automatic discovery of optimal testing parameters
* used and allocated memory analysis
* powerful statistical functions
* multi paradigm (builder, fluent, template) and easy to extend API
* supports a special bulk testing mode for objects that cannot be modified
* interleaves tests (and other tricks) to mitigate CPU throttling and OS multitasking.

### CONS:

* less control over tested code (user should be aware of JVM code optimizations such as dead code elimination, constant folding, loop unrolling, lock coalescing, in-lining, code profiling...).


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

The `jmh-ptcomparison` module is **not** part of the default build. It vendors Oracle's JMH sample
suite, which is long out of date and does not compile on current JDKs, so it must never be able to
break an ordinary build. It lives behind an opt-in profile:

    mvn -Pjmh-comparison clean package

It exists to compare this framework against JMH on the same problem; see the `main_pt` methods in
the samples. It has no tests and is never run as part of `mvn verify` without the profile.


### Failing tests

__Please note that performance tests cannot be assured to be stable under any possible condition!__

In particular unit tests environments are more prone to failure especially with strict tolerances. In unit tests execute avoid executing tests in parallel.

The memory test uses a trick to evaluate the used memory: because the used memory is reported with a granularity which is usually way bigger than the memory to measure (about 1 MiB on a x64 linux system), after the test has finished the memory is allocated incrementally until a change in the reported usage happens.
By knowing how much memory has been allocated the exact memory used by the test can be deduced.
This method works very well but sometimes it glitches and reports strange results (for example if a Garbage Collection has been executed in the meantime).

## Usage ##

The easiest way to use this library is by extending its template or by using its builder. See the examples package.
