package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
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

        consumer.accept(assertable);

        assertEquals("title",
                consumer.getConsumedAssertableList().get(0).getName());
    }

}
