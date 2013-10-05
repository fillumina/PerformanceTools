Performance-Tools
=================

- __version:__ 0.1
- __release:__ 4 October 2013
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

__A framework to perform benchmarks on your Java code.__

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
benchmarks which usually concern full program execution.
A micro-benchmark has the disadvantage of making it difficult to compare
measures obtained from different systems.

Another approach is to take the measurements of two or more different codes
and consider the relative speed between them.
This technique allows some advantages over a micro-benchmark:
* The framework overhead is eliminated completely;
* Percentages are more reproducible between different systems;
* It's far more informative to know how much a code is faster in respect of
another known code;
* It's more robust against environment disturbances (CPU-time fluctuations)/

This is the approach chosen by this API (although nothing forbids to specify
only one test and so having a micro-benchmark).

It has the following features:
* It allows to test code in a __single__ and in __multi__ threaded environment;
* It allows to specify __parametrized codes__;
* It allows to use a __sequence__ as a second parameter of a parametrized codes;
* It allows to easily __export__ the performances or use them on place;
* It allows to __assert conditions__ on the tests so to use them in unit tests;
* The structure of the API is very open and interface centric so that it is
__highly customizable and expandable__;
* It can be used with two different paradigms: __fluent interface__ and
__templates__.

## Note about the current version:
This version has been extensively tested but has not
become public yet and so it has not been used on many different systems or
environments. For this reason *it cannot be considered totally stable
(the code may change) and 100% production ready*.
Please send me feedbacks about how it runs on your system or
if you have bugs or suggestions.

## Bibliography:
* [Java theory and practice: Anatomy of a flawed microbenchmark (Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html);
* [Java theory and practice: Dynamic compilation and performance measurement
(Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0)

## Compilation and installation:
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

## Usage
The easier way to use this library is by extending one of its templates.
Here is an example of a very simple test using a template that increases
the iterations at each round until it matches the given performance stability.

    public class DivisionByTwoPerformanceTest
            extends JUnitAutoProgressionPerformanceTemplate {

        // this allows to run the test with some useful output
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
            assertion.setPercentageTolerance(5)
                    .assertTest("math").fasterThan("binary");
        }
    }

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