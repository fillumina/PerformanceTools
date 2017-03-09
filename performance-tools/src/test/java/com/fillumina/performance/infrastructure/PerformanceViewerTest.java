package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceViewerTest {


    private static class Formatter implements StringGenerator<AssertableMock> {
        static final String FORMATTED_TEXT = "formatted text";

        @Override
        public String toString(PHolder<AssertableMock> t) {
            return FORMATTED_TEXT;
        }
    }

    @Test
    public void shouldConsumeIfNotNullAssertableIsPassed() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableMock> viewer =
                new PerformanceViewer<>(formatter, buf);

        viewer.consume(new PHolder<>(new AssertableMock()));

        assertEquals(Formatter.FORMATTED_TEXT + System.lineSeparator(),
                buf.toString());
    }

    @Test
    public void shouldNotConsumeIfNullAssertableIsPassed() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableMock> viewer =
                new PerformanceViewer<>(formatter, buf);

        viewer.consume(null);

        assertEquals(0, buf.length());
    }

    @Test
    public void shouldConsumeNullAssertable() {
        Formatter formatter = new Formatter();
        StringBuilder buf = new StringBuilder();
        PerformanceViewer<AssertableMock> viewer =
                new PerformanceViewer<>(formatter, buf);

        PHolder<AssertableMock> holder = new PHolder<>((AssertableMock)null);
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
        PerformanceViewer<AssertableMock> pv =
                new PerformanceViewer<>(formatter, null);
        pv.consume(new PHolder<>(new AssertableMock("one")));
    }

    @Test
    public void shouldAcceptNullAssertable() {
        Formatter formatter = new Formatter();
        PerformanceViewer<AssertableMock> pv =
                new PerformanceViewer<>(formatter, null);
        pv.consume(new PHolder<>((AssertableMock)null));
    }
}
