package com.fillumina.performance.executor.param;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunnableOptionsContainer {

    private final OptionContainer optionContainer;
    private final Runnable runnable;

    public RunnableOptionsContainer(Runnable runnable,
            OptionContainer optionContainer) {
        this.optionContainer = optionContainer;
        this.runnable = runnable;
    }

    public OptionContainer getOptionContainer() {
        return optionContainer;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
