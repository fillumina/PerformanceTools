package com.fillumina.performance.infrastructure;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerChainTest {

    @Test
    public void shouldConsumeIfChainEmpty() {
        PerformanceConsumerChain<AssertableImpl> chain =
                new PerformanceConsumerChain<>();

        chain.consume(new PHolder<>(new AssertableImpl()));
    }

    @Test
    public void shouldConsumeWithOneConsumer() {
        ConsumerImpl<AssertableImpl> one = new ConsumerImpl<>();
        PerformanceConsumerChain<AssertableImpl> chain =
                new PerformanceConsumerChain<>(one);

        chain.consume(new PHolder<>(new AssertableImpl("assertable")));

        assertEquals("assertable", one.getList().get(0));
    }

    @Test
    public void shouldConsumeWithTwoConsumers() {
        ConsumerImpl<AssertableImpl> one = new ConsumerImpl<>();
        ConsumerImpl<AssertableImpl> two = new ConsumerImpl<>();
        PerformanceConsumerChain<AssertableImpl> chain =
                new PerformanceConsumerChain<>(one, two);

        chain.consume(new PHolder<>(new AssertableImpl("assertable")));

        assertEquals("assertable", one.getList().get(0));
        assertEquals("assertable", two.getList().get(0));
    }

}
