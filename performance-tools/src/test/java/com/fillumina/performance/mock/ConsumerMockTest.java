package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerMockTest {

    @Test
    public void shouldRecordAssertion() {
        AssertableMock assertable =
                AssertableMock.create("title", "first", 10.1, "second", 20.0);

        ConsumerMock<AssertableMock> consumer = new ConsumerMock<>();

        TName testName = TN.tname("one", "two");

        consumer.consume(testName, assertable);

        assertEquals("title",
                consumer.getConsumedAssertableMap().get(testName).getName());
    }

}
