package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.StaticPath;

// TODO this is an example of generic consumer which works on all the hierarchy
public class AssertionImpl<T extends Assertable>
        extends ConsumerImpl<T>
        implements Assertion<T> {

    @Override
    public void check(PHolder<T> performances) {
        consume(performances);
    }

    @Override
    public String toString(PHolder<T> performances) {
        final StringBuilder buf = new StringBuilder();
        performances.traverseLeaves(new PHolder.LeafVisitor<AssertableImpl>() {
            @Override
            public void visitLeaf(StaticPath name, AssertableImpl stats) {
                buf.append(stats.getName());
            }
        });
        return buf.toString();
    }

}
