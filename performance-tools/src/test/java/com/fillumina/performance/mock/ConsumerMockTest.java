package com.fillumina.performance.mock;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.util.pathname.PathName;
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

        PathName testName = PN.pname("one", "two");

        consumer.accept(assertable);

        assertEquals("title",
                consumer.getConsumedAssertableList().get(0).getName());
    }

}
