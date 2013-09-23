/**
 * This API helps to perform benchmarks on your code.
 * <p>
 * Instead of producing the
 * time an algorithm use to perform a task (like microbenchmarks),
 * which is very dependent on the
 * environment, it focuses on the <b>relative performance percentage of
 * different algorithms between each other</b>. By this way it is possible to
 * mitigate the differences of the environments. It is still possible
 * that an algorithm may run faster on a system than on another
 * (think about a system with an FPU against a system without)
 * but the results are at least comparable within similar ones.
 * <br>
 * Note that the <b>algorithms should be executed thousand
 * of times</b> to have accurate
 * results and so this API isn't appropriate to assess the performance of a long
 * operation (i.e. using I/O)
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
