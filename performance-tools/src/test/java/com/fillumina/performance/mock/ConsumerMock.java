package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.StaticPath;
import java.util.ArrayList;
import java.util.List;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <T>
 */
public class ConsumerMock<T extends Assertable> implements PerformanceConsumer<T> {

    private final List<String> list = new ArrayList<>();

    @Override
    public void consume(PHolder<T> performances) {
        performances.traverseLeaves(new PHolder.LeafVisitor<AssertableMock>() {
            @Override
            public void visitLeaf(StaticPath name, AssertableMock stats) {
                getList().add(stats.getName());
            }
        });
    }

    public List<String> getList() {
        return list;
    }
}
