package com.fillumina.performance.producer.progression;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class NullStandardDeviationConsumer
        implements StandardDeviationConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullStandardDeviationConsumer INSTANCE =
            new NullStandardDeviationConsumer();

    private NullStandardDeviationConsumer() {}

    @Override
    public void consume(double strDev) {
        // do nothing
    }
}
