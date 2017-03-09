package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.ConsumerMock;
import com.fillumina.performance.mock.AssertableMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerChainTest {

    @Test
    public void shouldConsumeIfChainEmpty() {
        PerformanceConsumerChain<AssertableMock> chain =
                new PerformanceConsumerChain<>();

        chain.consume(new PHolder<>(new AssertableMock()));
    }

    @Test
    public void shouldConsumeWithOneConsumer() {
        ConsumerMock<AssertableMock> one = new ConsumerMock<>();
        PerformanceConsumerChain<AssertableMock> chain =
                new PerformanceConsumerChain<>(one);

        chain.consume(new PHolder<>(new AssertableMock("assertable")));

        assertEquals("assertable", one.getList().get(0));
    }

    @Test
    public void shouldConsumeWithTwoConsumers() {
        ConsumerMock<AssertableMock> one = new ConsumerMock<>();
        ConsumerMock<AssertableMock> two = new ConsumerMock<>();
        PerformanceConsumerChain<AssertableMock> chain =
                new PerformanceConsumerChain<>(one, two);

        chain.consume(new PHolder<>(new AssertableMock("assertable")));

        assertEquals("assertable", one.getList().get(0));
        assertEquals("assertable", two.getList().get(0));
    }

}
