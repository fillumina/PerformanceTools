package com.fillumina.performance.util;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerAggregatorTest {

    @Test
    public void shouldConsumeIfChainEmpty() {
        ConsumerAggregator<?,Object> chain =
                new ConsumerAggregator<>();

        chain.accept(new Object());
    }

    @Test
    public void shouldConsumeWithOneConsumer() {
        List<String> list = new ArrayList<>();
        ConsumerAggregator<?,String> chain =
                new ConsumerAggregator<>(list::add);

        chain.accept("hello");

        assertEquals("hello", list.get(0));
    }

    @Test
    public void shouldConsumeWithTwoConsumers() {
        List<String> one = new ArrayList<>();
        List<String> two = new ArrayList<>();

        ConsumerAggregator<?,String> chain =
                new ConsumerAggregator<>(one::add, two::add);

        chain.accept("hello");

        assertEquals("hello", one.get(0));
        assertEquals("hello", two.get(0));
    }

}
