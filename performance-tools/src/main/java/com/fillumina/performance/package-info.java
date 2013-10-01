/**
 * This API helps to perform benchmarks on your code.
 * <p>
 * Asses performances of java code is very tough.
 * First Java runs on a variety of
 * platforms (from mobiles to supercomputers) and it's difficult to
 * say how some code will perform in all of them (think about memory
 * or FPU constrains).
 * Then the JVM itself is not univocal (there are many producers)
 * and may compile and optimize the code at execution time in different ways.
 * Other than that most of the environment used to test the code use
 * multitasking to perform various operations simultaneously and even
 * the garbage collection may impact the performance results unpredictably.
 * <p>
 * Evaluating how long a code takes to execute is a kind of performance test
 * called <b>micro-benchmark</b> because a full flagged benchmark usually concerns
 * an entire program execution (and often embraces more than one program).
 * But a micro-benchmark has a number of disadvantages that basically has
 * to do with the fact that their results are very hard to compare to each other
 * (especially if you consider different systems).
 * <p>
 * Another approach is to take the measurements of two or more different code
 * and <b>consider the respective speed in terms of percentages</b>.
 * This would allow many advantages over a micro-benchmark:
 * <ul>
 * <li>The framework overhead is eliminated by the use of percentages;
 * <li>A percentage is more reproducible between different systems;
 * <li>It's far more informative to know how much a code is faster in
 * respect of another known code;
 * <li>It's more robust against environment disturbances (CPU fluctuations)
 * </ul>
 *
 * This is the approach chosen by this API and though nothing forbid to specify
 * only one test (so having a micro-benchmark) the focus is all towards
 * having different codes be compared together.
 * <p>
 * It has the following features:
 * <ul>
 * <li>It allows to test algorithms in a <b>single threaded and in
 * multi threaded environment</b>;
 * <li>It allows to specify <b>parametrized algorithms</b>;
 * <li>It allows to use a <b>sequence as a second parameter</b> of a parametrized
 * algorithm;
 * <li>It allows to <b>easily export the performances</b> or use them on place;
 * <li>It allows to <b>assert conditions about the algorithms</b> so to use
 * them in unit tests;
 * <li>The structure of the API is very open and full of interfaces and
 * builders so that it is <b>highly customizable and expandable</b>;
 * <li>It can be used with two different paradigms: <i>fluent interface</i> and
 * <i>templates</i>.
 * </ul>
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html'>
 *      Java theory and practice: Anatomy of a flawed microbenchmark
 *      (Brian Goetz)
 *      </a>
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0'>
 *      Java theory and practice: Dynamic compilation and performance measurement
 *      (Brian Goetz)
 *      </a>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
package com.fillumina.performance;
