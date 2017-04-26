package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.TreeName;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class AssertionMock<A extends Assertable>
        extends ConsumerMock<A>
        implements Assertion<A> {

    @Override
    public void check(PHolder<A> performances) {
        consume(performances);
    }

    @Override
    public String toString(PHolder<A> performances) {
        final StringBuilder buf = new StringBuilder();
        performances.traverseLeaves(new PHolder.LeafVisitor<AssertableMock>() {
            @Override
            public void visitLeaf(TreeName name, AssertableMock stats) {
                buf.append(stats.getName());
            }
        });
        return buf.toString();
    }
}
