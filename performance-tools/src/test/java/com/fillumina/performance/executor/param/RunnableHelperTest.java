package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.param.RunnableHelper.Cloner;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunnableHelperTest implements Runnable {

    @Param("one")
    private String oneField;

    @Param("two")
    private Integer twoField;

    @Param("three")
    private int threeField;

    @Param
    private Object four;

    private Date date;

    @Test
    public void shouldSetAStringField() {
        RunnableHelper setter =
                new RunnableHelper(this, Param.class);
        setter.set(this, "one", "hello");
        assertEquals("hello", oneField);
    }

    @Test
    public void shouldSetAnIntegerField() {
        RunnableHelper setter =
                new RunnableHelper(this, Param.class);
        setter.set(this, "two", 12);
        assertEquals(12, twoField, 0);
    }

    @Test
    public void shouldSetAnIntField() {
        RunnableHelper setter =
                new RunnableHelper(this, Param.class);
        setter.set(this, "three", 15);
        assertEquals(15, threeField, 0);
    }

    @Test
    public void shouldGetTheValueFromTheNameOfTheField() {
        RunnableHelper setter =
                new RunnableHelper(this, Param.class);
        setter.set(this, "four", 64);
        assertEquals(64, four);
    }

    @Test
    public void shouldCreateNewInstance() {
        RunnableHelper setter =
                new RunnableHelper(this, Param.class);
        Runnable runnable = setter.doClone().get();
        assertTrue(runnable instanceof RunnableHelperTest);
    }

    @Test
    public void shouldCloneWithFieldValues() {
        oneField = "one";
        twoField = 2;
        threeField = 3;
        Date d = new Date();
        date = d;
        final Object object = new Object();
        four = object;

        RunnableHelper setter =
                new RunnableHelper(this, Param.class);

        Cloner cloner = setter.doClone();
        RunnableHelperTest clone = (RunnableHelperTest)
                cloner.get();

        assertNotSame(this, clone);
        assertEquals("one", clone.oneField);
        assertEquals(2, clone.twoField, 0);
        assertEquals(3, clone.threeField, 0);
        assertEquals(object, clone.four);
        assertEquals(d, clone.date);
    }

    @Test
    public void shouldCopyAnInnerClass() {
        RunnableHelper setter =
                new RunnableHelper(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, Param.class);

        Runnable r = setter.doClone().get();
        assertNotNull(r);
    }

    @Override
    public void run() {
    }

}
