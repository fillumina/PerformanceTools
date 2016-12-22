Performance-Tools
=================

__A framework based on a very configurable and expandable API to easily execute
performance speed and memory tests in a JVM agnostic way and to analyze results
with a powerful set of statistical functions.__

- __version:__ 2.0
- __released:__ 6 August 2016
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

- completely rewritten new architecture (similar external API)
- powerful statistical analysis functions and data presentation
- improved accuracy and reproducibility of results
- used and allocated memory estimator
- automatic discovery of optimal testing parameters
- special test mode to evaluate the performance of unmodifiable objects
(i.e. to estimate the speed of the removal of one entry from a map of given size)
- in-testing data presentation with estimation of test conclusion and preview
of results
- outliers and steady state filters remove the need to have a warm-up phase


## Differences from JMH

(JMH)[http://openjdk.java.net/projects/code-tools/jmh/] is an
accurate tool characterized by the fact that it analyzes the code
in a very controlled environment to avoid common pitfalls and external
influences. It is based on a deep knowledge of the JVM internals that it
uses to isolate the code under test and to make it run in ideal conditions.
This framework on the other hand tries to make less assumptions as possible
on the JVM and uses advanced statistics to extract as much
information as  possible from the code under test. It doesn't fight
optimizations or even pitfalls (which by the way you might expect in real
world code), it just tries to evaluate their effects.
It is very accurate and allows to test the code in a more interactive way,
directly in the same environment the code is running (unit tests, web pages,
everywhere really). This allows for a wide spectrum of analysis that can answer
to the final question: is this code faster or slower than this other one
in my application and context?

### PROS:

* it can be executed everywhere, even in unit tests
([JUnit](http://junit.org/) and [TestNG](http://testng.org/) supported
natively, other frameworks are easy to add)
* it supports assertions
* automatic discovery of optimal testing parameters
* used and allocated memory analysis
* powerful statistical functions to improve its accuracy
* easy to extend
* supports a special bulk testing mode for objects that cannot be modified
* interleaves tests to mitigate CPU throttling and OS multitasking

### CONS:

* less control over tested code (user should be aware of JVM code optimizations
such as dead code elimination, constant folding, loop unrolling,
lock coalescing, in-lining, code profiling...)
* less testing modes (only average measures over steady state or over a definite
number of samples and iterations are provided)
* less accurate for multi-threaded tests because it doesn't automatically defend
against false shared field access and doesn't use thread groups natively.


## Use with maven ##

This project can be used with maven by adding the following dependencies
to your project configuration `pom.xml`.

This is the core project, can be used alone but it's easier to use through
templates (just specify the one corresponding to the test unit
framework you are using):
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
Because templates depend on the core project you only need to specify
the right template and the core project will be added automatically.

## History ##
 - version 2.0 released 24 December 2016: completely rewritten API
 - version 1.0 released 28 July 2014: first version released to maven central
 - version 0.1 released 4 October 2013


## Summary ##


### Difficulties of a benchmarking

Java runs on a variety of platforms (from mobiles to mainframes) and there are
many different virtual machines (JVM), garbage collector (GC) algorithms
and optimization strategies available.
Just in time executors (JIT) optimize the code during execution
resulting in performance varying considerably even while running.

A benchmark is often a measure of an extreme and very unusual
case: the code under test is executed continuously for a long time giving the
best chances to the JVM to optimize the code, to the memory working set to be
cached and to the CPU to perform the best internal optimizations and caching.
Very often complex code will perform outstandingly on benchmarks but very
poorly in real world applications where their memory and code footprint might
not being executed enough to benefit from the optimizations. An unoptimized
complex code will always perform poorly.

Another factor of importance is the memory use of the code. If a code
uses a lot of heap memory allocating objects (some JVM uses the stack in some
cases now), this memory will required some time to be cleaned up.
This time is rarely accounted for
in benchmarks but might impact a running code. A benchmark value is just a
measure and should not be read as an absolute index of the value of some code.
Very often different algorithms have different trade-offs and should be used
on different situations. Benchmarking is only a tool that helps to investigate
different choices.


### Micro-benchmark

Evaluating how long a (relatively small) code takes to execute is a kind
of performance test called *micro-benchmark* as opposed to the classic
benchmarking which usually concern full program executions. The temptation is
to create a *synthetic benchmark* out of it, that is to run the test in a very
aseptic environment very different from the one it will run on.
For example consider that
to test a code we are forced to execute it in a tight loop so that all its code
will very probably resides on the CPU internal cache with all the needed data
pretty close to it. This is not really what we can usually expect.
Same thing applies to memory usage: usually speed tests don't consider memory
usage
but if a code use a lot of memory it adds a considerable overhead to the JVM and
that would probably reflect on the general performances of the running program.
To estimate the effective speed of
some code is very difficult even in the presence of measured data.
The aim of this frameworks is to help investigating the code performances in a
simple, accurate and reproducible way but results should be always pondered with
the experience and further investigations using a profiler.


### Compare is better than measure

Instead of just measuring the absolute speed of some code in a tightly
controlled environment (which involves a very high level of carefulness)
a different approach is to take the measurements of
two or more similar codes and consider the relative speed between them.
This technique has the following advantages over a single measurement:

* Percentages (fractions) are more reproducible and stable amongst different
  systems;
* It's far more informative to know the speed of some code relatively to some
  other known code than just it's absolute speed (which of course depends on
  the system it ran on);
* It's more robust against environment disturbances (CPU-time fluctuations...).


### Features

* It allows to test codes in a __single__ and in __multi__ threaded environment;
* It allows to specify __parametrized codes__;
* It allows to use a __sequence__ as a second parameter of a parametrized code;
* It allows to easily __export__ the performances;
* It allows to __assert conditions__ on the tests so to use them in unit tests;
* It runs the test __everywhere__ (no need of a separate environment);
* The structure of the API is very open and interface centric so that it is
__highly customizable and expandable__;
* It estimates __used and allocated memory__;
* It can be used with two different paradigms: __fluent interface__ and
__templates__;
* It heavily rely on __statistics__ to produce thrustable and reproducible
  results.


### Bulk test

Some code can be executed only once (i.e. the removal of an entry
from a map) and so they cannot be estimated by repeating it.
To overcome this problem the same test can be executed on a collection
of equal objects exactly once. A special test template can be extended to
accomplish exactly that
(see com.fillumina.performance.speed.sample.BulkTestable).

## Bibliography ##

benchmarking:

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

JHM:

* [Mikhail Vorontsov: Introduction to JMH](http://java-performance.info/jmh/)
* [Java Micro Benchmark with JMH](http://javapapers.com/java/java-micro-benchmark-with-jmh/)



## Compilation and installation ##
This is a multi-artifact maven project so you can build and install the whole
project by issuing

    mvn clean install

from the main directory. The actual API core is in the folder
'performance-tools' and its jar should be in 'performance-tools/target'.
The JUnit templates jar is in the folder 'performance-tools-junit/target'.


### Failing tests

__Please note that performance tests cannot be assured to be stable under
any possible condition!__ In particular unit tests environments are more
prone to failure specially with strict tolerances. In case of problems try to
execute the performance tests alone and, in particular, avoid executing
tests in parallel.

The memory test uses a trick to evaluate memory usage: because the JVM used
memory is reported with a granularity which is usually way bigger than the
memory measure we are interested in (about 1 MiB on a x64 linux system),
after the test
has finished the memory is allocated incrementally until a change in the
reported usage happens.
By knowing how much memory has been allocated the exact memory used by the test
can be deduced.
This method works very well but sometimes it glitches and reports
strange results (for example a GC could have happened during the measured
execution).

## Usage ##
The easiest way to use this library is by extending one of its templates.
To use a template is easy because the abstract methods are there to remind you
what it is needed and they are pretty self explanatory
(autocompletition should work with any decent IDE). Here is an example of a
very simple JUnit performance test using a template:

```java
public class DivisionByTwoPerformanceTest
        extends JUnitAutoProgressionPerformanceTemplate {

    // allows to run the test stand alone with some useful output
    public static void main(final String[] args) {
        new DivisionByTwoPerformanceTest().testWithIntermediateOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        // this is where the target stability is set
        config.setMaxStandardDeviation(2);
    }

    @Override
    public void addTests(TestsContainer tests) {
        final Random rnd = new Random(System.currentTimeMillis());

        tests.addTest("math", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() / 2;
            }
        });

        tests.addTest("binary", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() >> 1;
            }
        });
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
        assertion.setPercentageTolerance(2)
                .assertTest("binary").fasterThan("math");
    }
}
```

This is the result returned by calling the test's main():

```
Iterations: 1000	Samples: 10	Standard Deviation: 7.997750553879185
Iterations: 10000	Samples: 10	Standard Deviation: 22.840778409983503
Iterations: 10000	Samples: 10	Standard Deviation: 3.628900909423828
Iterations: 100000	Samples: 10	Standard Deviation: 10.10749740600586
Iterations: 100000	Samples: 10	Standard Deviation: 9.441978610152637
Iterations: 1000000	Samples: 10	Standard Deviation: 3.9505724269289453
Iterations: 10000000	Samples: 10	Standard Deviation: 0.3524486164596204

 (10,000,000 iterations)
math  	   0 :	     20.09 ns		    100.00 %
binary	   1 :	     19.35 ns		     96.34 %
           * :	     39.44 ns
```

Note that some iterations are executed twice because there weren’t any
improvement in the stability so the API automatically implies that some
disturbance had occurred and repeated the iteration.

There are other templates to manage parameters and sequence:
* The parameters are useful to test a single code against different similar
objects (like different implementation of a Map to see which one is faster).
* The sequence allows to test a parametrized code against various values (like
different Map sizes to see how size impacts performances).

Each test can be executed in a multi threaded environment in a very easy way
(by just modifying its configuration).

There are text and CSV viewers built-in, but if you need something different
(a Swing GUI or to save values on disk) you may implement an interface and call
the 'executeTest()' method of the templates.