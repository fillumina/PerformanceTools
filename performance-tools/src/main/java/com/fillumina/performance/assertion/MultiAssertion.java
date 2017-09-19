package com.fillumina.performance.assertion;

/**
 * Assertions can be nested to create a tree. This interface identify
 * the nodes of this tree.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
public interface MultiAssertion extends Iterable<Assertion>, Assertion {

}
