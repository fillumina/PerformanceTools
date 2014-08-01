Performance-Tools
=================

__A framework to easily perform comparative benchmarks and assertions on
Java code.__

- __version:__ 1.1
- __released:__ 1 August 2014
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Knowing how fast a certain code is on a specific machine
(with a specific JDK, SO etc etc) tells almost nothing.
I want to know if a code or an algorithm is faster or slower in respect of an
*alternative*. Did I improved my code or is it slower? Is this the better
solution under some conditions? That’s what I ask myself and that’s what
this API is for: __to be able to compare different codes together and see
which one performs better__. And __I can even put this check in a unit test
to make sure my assumptions stay correct during development!__

## Index ##
- [Note to present version](#note-to-present-version)
- [Use with maven](#use-with-maven)
- [History](#history)
- [Summary](#summary)
- [Bibliography](#bibliography)
- [Compilation and installation](#compilation-and-installation)
- [Usage Example](#usage)
- [Documentation](./docs/documentation_index.md)

## Note to present version ##

Added `performance-tools-testng` templates.


## Use with maven

This project can be used with maven by adding the following dependencies
to your project configuration `pom.xml`.

This is the core project, can be used alone but it's easier to use throught
templates (just specify the one corresponding to the test unit
framework you are using):
```xml
    <dependency>
        <groupId>com.fillumina</groupId>
        <artifactId>performance-tools</artifactId>
        <version>1.1</version>
    </dependency>
```

This module contains the templates for [JUnit](http://junit.org/):
```xml
    <dependency>
        <groupId>com.fillumina</groupId>
        <artifactId>performance-tools-junit</artifactId>
        <version>1.1</version>
    </dependency>
```

This module contains the templates for [TestNG](http://testng.org/):
```xml
    <dependency>
        <groupId>com.fillumina</groupId>
        <artifactId>performance-tools-testng</artifactId>
        <version>1.1</version>
    </dependency>
```
Because the templates depend on the core project you only need to specify
the right template.

## History ##
 - version 1.0 released 28 July 2014: first version released to maven central
 - version 0.1 released 4 October 2013


## Summary ##

To asses performances of a java code can be tough:
first Java runs on a variety of platforms (from mobiles to mainframes)
and it's difficult to tell how some code will perform in all of them
(think about memory or FPU constrains);
then the JVM itself is not unique (there are many producers) and may
execute the byte-code in different ways.
Other than that multitasking, virtualization and even the Java
garbage collector may impact the performance results unpredictably.

Evaluating how long a (relatively small) code takes to execute is a kind
of performance test called *micro-benchmark* as opposed to the classic
benchmarks which usually concern full program executions.
A micro-benchmark has the disadvantage of making it difficult to compare
measures obtained from different systems.

Another approach is to take the measurements of two or more different codes
and consider the relative speed between them.
This technique has the following advantages over a micro-benchmark:
* The framework overhead is eliminated completely;
* Percentages are more reproducible between different systems;
* It's far more informative to know how much a code is faster in respect of
another known code;
* It's more robust against environment disturbances (CPU-time fluctuations).

This is the approach chosen by this API (although nothing forbids to specify
only one test and so having a micro-benchmark).

It has the following features:
* It allows to test codes in a __single__ and in __multi__ threaded environment;
* It allows to specify __parametrized codes__;
* It allows to use a __sequence__ as a second parameter of a parametrized code;
* It allows to easily __export__ the performances or use them in place;
* It allows to __assert conditions__ on the tests so to use them in unit tests;
* The structure of the API is very open and interface centric so that it is
__highly customizable and expandable__;
* It can be used with two different paradigms: __fluent interface__ and
__templates__.

## Bibliography ##
* [Java theory and practice: Anatomy of a flawed microbenchmark (Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html);
* [Java theory and practice: Dynamic compilation and performance measurement
(Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0)
* [Java benchmarking article]
(http://www.ellipticgroup.com/html/benchmarkingArticle.html)
* [Robust Java benchmarking, Part 1: Issues]
(http://www.ibm.com/developerworks/java/library/j-benchmark1/index.html)
* [Robust Java benchmarking, Part 2: Statistics and solutions]
(https://www.ibm.com/developerworks/java/library/j-benchmark2/)
* [Java theory and practice: Dynamic compilation and performance measurement]
(http://www.ibm.com/developerworks/library/j-jtp12214/)
* [Garbage First Garbage Collector Tuning]
(http://www.oracle.com/technetwork/articles/java/g1gc-1984535.html)


## Compilation and installation ##
This is a multi-artifact maven project so you can build and install the whole
project by issuing

    mvn clean install

from the main directory. The actual API core is in the folder
'performance-tools' and its jar should be in 'performance-tools/target'.
The JUnit templates jar is in the folder 'performance-tools-junit/target'.

### Failing tests
__Please note that performance tests cannot be assured to be stable under
any possible condition!__ In particular if the tolerance is strict you may
expect test failure quite frequently. To overcome this problem relax your
tolerances and try to run the performance test apart from the main
building task. In particular, avoid to use test multitasking ('maven' can be
configured to execute more than one test in parallel).

## Usage ##
The easiest way to use this library is by extending one of its templates.
To use a template is easy because the abstract methods are there to remind
what it is needed and they are pretty self explanatory on how to do it
(autocompletition should work with any decent IDE). Here is an example of a
very simple JUnit performance test using a template that increases the
iteration number at each round until it matches the required performance
stability. The main() method is added so it can be executed by itself
producing some useful output.

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