package com.fillumina.performance.infrastructure;

import com.fillumina.performance.time.sample.TimeSample;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractPerformanceProducerTest {

    private static class PerformanceProducerImpl
            extends AbstractPerformanceProducer
                    <PerformanceProducerImpl,
                     TimeSample,
                     Runnable> {

        @Override
        public PHolder<TimeSample> execute() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static class TestableImpl implements Runnable {

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private PerformanceProducerImpl producer = new PerformanceProducerImpl();

    @Test
    public void testClearTests() {
        final TestableImpl one = new TestableImpl();
        final TestableImpl two = new TestableImpl();
        producer.addTest("one", one);
        producer.addTest("two", two);

        producer.clearTests();

        assertTrue(producer.getTests().isEmpty());
    }

    @Test
    public void shouldAddMultipleTests() {
        final TestableImpl one = new TestableImpl();
        final TestableImpl two = new TestableImpl();
        producer.addTest("one", one);
        producer.addTest("two", two);

        assertEquals(2, producer.getTests().size());
    }

    @Test
    public void shouldAddTest() {
        final TestableImpl one = new TestableImpl();
        producer.addTest("one", one);

        assertTrue(one == producer.getTests().get(TN.tname("one")));
    }

    @Test
    public void testIgnoreTest() {
        final TestableImpl one = new TestableImpl();
        final TestableImpl two = new TestableImpl();
        producer.addTest("one", one);
        producer.ignoreTest("two", two);

        assertEquals(1, producer.getTests().size());
        assertNull(producer.getTests().get(TN.tname("two")));
    }
}
