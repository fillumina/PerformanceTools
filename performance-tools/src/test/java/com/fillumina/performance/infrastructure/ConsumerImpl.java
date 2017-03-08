package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.StaticPath;
import java.util.ArrayList;
import java.util.List;

// TODO this is an example of generic consumer which works on all the hierarchy
class ConsumerImpl<T extends Assertable> implements PerformanceConsumer<T> {

    private final List<String> list = new ArrayList<>();

    @Override
    public void consume(PHolder<T> performances) {
        performances.traverseLeaves(new PHolder.LeafVisitor<AssertableImpl>() {
            @Override
            public void visitLeaf(StaticPath name, AssertableImpl stats) {
                getList().add(stats.getName());
            }
        });
    }

    public List<String> getList() {
        return list;
    }
}
