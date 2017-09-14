package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.ConsumerMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerAggregatorTest {

    @Test
    public void shouldConsumeIfChainEmpty() {
        AssertableConsumerAggregator chain =
                new AssertableConsumerAggregator();

        chain.accept(new AssertableMock());
    }

    @Test
    public void shouldConsumeWithOneConsumer() {
        ConsumerMock<AssertableMock> one = ConsumerMock.create();
        AssertableConsumerAggregator chain =
                new AssertableConsumerAggregator(one);

        chain.accept(new AssertableMock("assertable"));

        assertEquals("assertable",
                one.getConsumedAssertableList().get(0).getName());
    }

    @Test
    public void shouldConsumeWithTwoConsumers() {
        ConsumerMock<AssertableMock> one = ConsumerMock.create();
        ConsumerMock<AssertableMock> two = ConsumerMock.create();
        AssertableConsumerAggregator chain =
                new AssertableConsumerAggregator(one, two);

        chain.accept(new AssertableMock("assertable"));

        assertEquals("assertable",
                one.getConsumedAssertableList().get(0).getName());
        assertEquals("assertable",
                two.getConsumedAssertableList().get(0).getName());
    }

}
