/**
 * This API helps to perform benchmarks on your code.
 * <p>
 * To asses performances of a java code can be tough.
 * First Java runs on a variety of
 * platforms (from mobiles to mainframes) and it's difficult to
 * tell how some code will perform in all of them (think about memory
 * or FPU constrains).
 * Then the JVM itself is not unique (there are many producers)
 * and may threat the byte-code at execution time in different ways.
 * Other than that most of the environments used to test the code use
 * multitasking to perform various operations simultaneously and even
 * the garbage collector may impact the performance results unpredictably.
 * <p>
 * Evaluating how long a (relatively small) code takes to execute is a kind
 * of performance test called <b>micro-benchmark</b> as opposed to the
 * classic benchmarks which usually concern full program execution.
 * A micro-benchmark has the disadvantage of making it difficult to compare
 * measures obtained from different systems.
 * <p>
 * Another approach is to take the measurements of two or more different codes
 * and <b>consider the relative speed</b>.
 * This would allow some advantages over a micro-benchmark:
 * <ul>
 * <li>The framework overhead is eliminated;
 * <li>Percentages are more reproducible between different systems;
 * <li>It's far more informative to know how much a code is faster in
 * respect of another known code;
 * <li>It's more robust against environment disturbances (CPU-time fluctuations)
 * </ul>
 *
 * This is the approach chosen by this API although nothing forbids
 * to specify only one test (so having a micro-benchmark).
 * <p>
 * It has the following features:
 * <ul>
 * <li>It allows to test code in a <b>single threaded and in
 * multi threaded environment</b>;
 * <li>It allows to specify <b>parametrized codes</b>;
 * <li>It allows to use a <b>sequence as a second parameter</b> of a parametrized
 * codes;
 * <li>It allows to <b>easily export the performances</b> or use them on place;
 * <li>It allows to <b>assert conditions about the codes</b> so to use
 * them in unit tests;
 * <li>The structure of the API is very open and interface centric
 * so that it is <b>highly customizable and expandable</b>;
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
