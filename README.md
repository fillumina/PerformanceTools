Performance-Tools
=================

- __version:__ 0.1
- __release:__ 4 October 2013
- __author:__ Francesco Illuminati (fillumina@gmail.com)
- __license:__ [apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

*This API helps to perform benchmarks on your code.*

To asses performances of a java code can be tough:
first Java runs on a variety of platforms (from mobiles to mainframes)
and it's difficult to tell how some code will perform in all of them
(think about memory or FPU constrains);
then the JVM itself is not unique (there are many producers) and may
threats the byte-code at execution time in different ways.
Other than that most of the environments used to test the code use
multitasking to perform various operations simultaneously and even the Java's
garbage collector may impact the performance results unpredictably.

Evaluating how long a (relatively small) code takes to execute is a kind
of performance test called *micro-benchmark* as opposed to the classic
benchmarks which usually concern full program execution.
A micro-benchmark has the disadvantage of making it difficult to compare
measures obtained from different systems.
Another approach is to take the measurements of two or more different codes
and consider the relative speed.
This technique would allow some advantages over a micro-benchmark:
* The framework overhead is eliminated;
* Percentages are more reproducible between different systems;
* It's far more informative to know how much a code is faster in respect of
another known code;
* It's more robust against environment disturbances (CPU-time fluctuations)

This is the approach chosen by this API although nothing forbids to specify
only one test (so having a micro-benchmark).
It has the following features:
* It allows to test code in a single threaded and in multi threaded environment;
* It allows to specify parametrized codes;
* It allows to use a sequence as a second parameter of a parametrized codes;
* It allows to easily export the performances or use them on place;
* It allows to assert conditions about the codes so to use them in unit tests;
* The structure of the API is very open and interface centric so that it is
highly customizable and expandable;
* It can be used with two different paradigms: fluent interface and templates.

# Note about the current version:
This version has been extensively tested but has not
become public yet and so it has not been used on many different systems or
environments. Please send me feedbacks about how it runs in your system or
if you have bugs or suggestions.

# Bibliography:
* [Java theory and practice: Anatomy of a flawed microbenchmark (Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html);
* [Java theory and practice: Dynamic compilation and performance measurement
(Brian Goetz)]
(http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0)

# Compilation and installation:
This is a multi-artifact maven project so you can build and install the whole
project by issuing

    mvn clean install

from the main directory. The actual API core is in the folder
'performance-tools' and its jar should be in 'performance-tools/target'.
The JUnit templates jar is in the folder 'performance-tools-junit/target'.

# Failing tests
*Please note that performance tests cannot be assured to be stable under
any possible condition!* In particular if the tolerance is strict you may
expect test failure quite frequently. To overcome this problem relax your
tolerance and try to run the performance test apart from the main
building task. In particular, avoid to use test multitasking ('maven' can be
configured to execute more than one test in parallel).

# Usage:
The easier way to use this library is by extending one of its templates.
Here is an example of a very simple test using a template that increases
the iterations to match a target performance stability.

    public class DivisionByTwoPerformanceTest
            extends JUnitAutoProgressionPerformanceTemplate {

        // this allows to run the test with some useful output
        public static void main(final String[] args) {
            new DivisionByTwoPerformanceTest().testWithIntermediateOutput();
        }

        @Override
        public void init(ProgressionConfigurator config) {
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
different size to see how size impacts performances).

Each test can be executed in a multi threaded environment in a very easy way
(by just modifying its configuration).

There are text and CVS viewers built-in, but if you need something different
(a Swing GUI or save values to disk) you may implement an interface and call
the 'executeTest()' method of the templates.