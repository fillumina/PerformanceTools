package com.fillumina.performance.executor.param;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunnableOptionsContainer {

    private final Map<String,Option> optionContainer;
    private final Runnable runnable;

    public RunnableOptionsContainer(Runnable runnable,
            Map<String,Option> optionContainer) {
        this.optionContainer = optionContainer;
        this.runnable = runnable;
    }

    public Map<String,Option> getOptionContainer() {
        return optionContainer;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
