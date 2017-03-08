package com.fillumina.performance.infrastructure;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceViewerTest {


    private static class Formatter implements StringGenerator<AssertableImpl> {
        static final String FORMATTED_TEXT = "formatted text";

        @Override
        public String toString(PHolder<AssertableImpl> t) {
            return FORMATTED_TEXT;
        }
    }

    @Test
    public void shouldConsumeIfNotNullAssertableIsPassed() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableImpl> viewer =
                new PerformanceViewer<>(formatter, buf);

        viewer.consume(new PHolder<>(new AssertableImpl()));

        assertEquals(Formatter.FORMATTED_TEXT + System.lineSeparator(),
                buf.toString());
    }

    @Test
    public void shouldNotConsumeIfNullAssertableIsPassed() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableImpl> viewer =
                new PerformanceViewer<>(formatter, buf);

        viewer.consume(null);

        assertEquals(0, buf.length());
    }

    @Test
    public void shouldConsumeNullAssertable() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableImpl> viewer =
                new PerformanceViewer<>(formatter, buf);

        PHolder<AssertableImpl> holder = new PHolder<>((AssertableImpl)null);
        viewer.consume(holder);

        assertEquals(Formatter.FORMATTED_TEXT + System.lineSeparator(),
                buf.toString());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullFormatter() {
        StringBuilder buf = new StringBuilder();
        new PerformanceViewer<>(null, buf);
    }

    @Test
    public void shouldAcceptNullAppender() {
        Formatter formatter = new Formatter();
        PerformanceViewer<AssertableImpl> pv =
                new PerformanceViewer<>(formatter, null);
        pv.consume(new PHolder<>(new AssertableImpl("one")));
    }

    @Test
    public void shouldAcceptNullAssertable() {
        Formatter formatter = new Formatter();
        PerformanceViewer<AssertableImpl> pv =
                new PerformanceViewer<>(formatter, null);
        pv.consume(new PHolder<>((AssertableImpl)null));
    }
}
