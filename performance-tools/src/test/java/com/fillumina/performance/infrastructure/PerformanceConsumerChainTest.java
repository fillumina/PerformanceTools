package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.ConsumerMock;
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

        chain.consume(new AssertableMock());
    }

    @Test
    public void shouldConsumeWithOneConsumer() {
        ConsumerMock<AssertableMock> one = new ConsumerMock<>();
        PerformanceConsumerChain<AssertableMock> chain =
                new PerformanceConsumerChain<>(one);

        chain.consume(new AssertableMock("assertable"));

        assertEquals("assertable",
                one.getConsumedAssertableList().get(0).getName());
    }

    @Test
    public void shouldConsumeWithTwoConsumers() {
        ConsumerMock<AssertableMock> one = new ConsumerMock<>();
        ConsumerMock<AssertableMock> two = new ConsumerMock<>();
        PerformanceConsumerChain<AssertableMock> chain =
                new PerformanceConsumerChain<>(one, two);

        chain.consume(new AssertableMock("assertable"));

        assertEquals("assertable",
                one.getConsumedAssertableList().get(0).getName());
        assertEquals("assertable",
                two.getConsumedAssertableList().get(0).getName());
    }

}
