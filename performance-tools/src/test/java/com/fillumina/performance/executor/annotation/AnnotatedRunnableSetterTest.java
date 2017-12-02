package com.fillumina.performance.executor.annotation;

import com.fillumina.performance.mock.RunnableMock;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotatedRunnableSetterTest {

    private RunnableMock runnable = new RunnableMock();

    @Test(expected = AssertionError.class)
    public void shouldNotCallSetup() {
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.SET_UP);
    }

    @Test(expected = AssertionError.class)
    public void shouldCallAnother() {
        AnnotatedRunnableSetter.INSTANCE.tearDown(runnable);
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.SET_UP);
    }

    @Test
    public void shouldCallSetUp() {
        AnnotatedRunnableSetter.INSTANCE.setUp(runnable);
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.SET_UP);
    }

    @Test
    public void shouldCallTearDown() {
        AnnotatedRunnableSetter.INSTANCE.tearDown(runnable);
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallBeforeSample() {
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, 12);
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.BEFORE_SAMPLE);
    }

    @Test
    public void shouldCallAfterSample() {
        AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, 12);
        runnable.assertTestableMethodBeenCalled(RunnableMock.TMethod.AFTER_SAMPLE);
    }

}
