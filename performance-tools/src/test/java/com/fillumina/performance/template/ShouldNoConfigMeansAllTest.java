package com.fillumina.performance.template;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.fillumina.performance.executor.generator.MixedPerformanceExecutorConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ShouldNoConfigMeansAllTest {

    @Test
    public void shouldNoConfigurationMeansToExecuteAllTests() {
        MixedConfigurationBuilder<?> configuration =
                new MixedConfigurationBuilder<>();

        MixedPerformanceExecutorConfiguration mixedConf = configuration.build();
        
        assertTrue(mixedConf.getSpeed().isActive());
        assertTrue(mixedConf.getUsedMem().isActive());
        assertTrue(mixedConf.getAllocatedMem().isActive());
    }
}
