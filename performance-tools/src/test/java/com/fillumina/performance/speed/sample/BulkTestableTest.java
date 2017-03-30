package com.fillumina.performance.speed.sample;

import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BulkTestableTest {

    @Test
    public void shouldInitializeObjectsAndData() {
        class TestValue {}

        class TestObject {
            TestValue value;
            boolean executed;
        }

        final Set<TestObject> set = new HashSet<>();

        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.addTest("bulk", new BulkTestable<TestObject, TestValue>() {

            @Override
            public TestObject createTestObject() {
                return new TestObject();
            }

            @Override
            public TestValue createTestValue() {
                return new TestValue();
            }

            @Override
            public void onBeforeSample(TestObject t, TestValue v) {
                t.value = v;
                set.add(t);
            }

            @Override
            public void test(TestObject t) {
                t.executed = true;
            }
        });

        pt.execute(10);

        assertEquals(10, set.size());

        for (TestObject o : set) {
            assertNotNull(o.value);
            assertTrue(o.executed);
        }
    }

    @Test
    public void shouldReInitializeObjectsAndDataIfDifferentSize() {
        class TestValue {}

        class TestObject {
            TestValue value;
            boolean executed;
        }

        final Set<TestObject> set = new HashSet<>();

        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.addTest("bulk", new BulkTestable<TestObject, TestValue>() {

            @Override
            public TestObject createTestObject() {
                return new TestObject();
            }

            @Override
            public TestValue createTestValue() {
                return new TestValue();
            }

            @Override
            public void onBeforeSample(TestObject t, TestValue v) {
                t.value = v;
                set.add(t);
            }

            @Override
            public void test(TestObject t) {
                t.executed = true;
            }
        });

        pt.execute(10);

        Set<TestObject> old = new HashSet<>(set);
        set.clear();

        pt.execute(5);

        assertNotEquals(old, set);
        assertEquals(5, set.size());
    }

    @Test
    public void shouldNotReInitializeObjectsAndDataIfSameSize() {
        class TestValue {}

        class TestObject {
            TestValue value;
            boolean executed;
        }

        final Set<TestObject> set = new HashSet<>();

        PerformanceTimer pt = new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor());

        pt.addTest("bulk", new BulkTestable<TestObject, TestValue>() {

            @Override
            public TestObject createTestObject() {
                return new TestObject();
            }

            @Override
            public TestValue createTestValue() {
                return new TestValue();
            }

            @Override
            public void onBeforeSample(TestObject t, TestValue v) {
                t.value = v;
                set.add(t);
            }

            @Override
            public void test(TestObject t) {
                t.executed = true;
            }
        });

        pt.execute(10);

        Set<TestObject> old = new HashSet<>(set);
        set.clear();

        pt.execute(10);

        assertEquals(old, set);
    }

}
