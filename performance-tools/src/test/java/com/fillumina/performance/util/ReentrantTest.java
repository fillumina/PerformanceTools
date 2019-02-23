package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReentrantTest {

    private static class Builder implements Reentrant<ReentrantTest> {
        private final ReentrantTest caller;

        public Builder(ReentrantTest caller) {
            this.caller = caller;
        }

        @Override
        public ReentrantTest end() {
            return caller;
        }
    }

    private Builder build() {
        return new Builder(this);
    }

    @Test
    public void shouldReturnTheCaller() {
        assertEquals(this, build().end() );
    }
}
