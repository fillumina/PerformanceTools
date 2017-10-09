package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.MixedConfiguration;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ShouldNoConfigMeansAllTest {

    @Test
    public void shouldNoConfigurationMeansToExecuteAllTests() {
        MixedConfigurationBuilder<?> configuration =
                new MixedConfigurationBuilder<>();

        MixedConfiguration mixedConf = configuration.build();
        
        assertTrue(mixedConf.getSpeed().isActive());
        assertTrue(mixedConf.getUsedMem().isActive());
        assertTrue(mixedConf.getAllocatedMem().isActive());
    }
}
