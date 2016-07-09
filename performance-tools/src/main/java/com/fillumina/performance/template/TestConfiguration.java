package com.fillumina.performance.template;

import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.TableFormatter;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class TestConfiguration {

    private String testName;
    private final SpeedConfiguration speedConfigurator;
    private final MemConfiguration usedMemConfigurator;
    private final MemConfiguration allocatedMemConfigurator;

    public TestConfiguration() {
        speedConfigurator = new SpeedConfiguration(this);
        usedMemConfigurator = new MemConfiguration(this);
        allocatedMemConfigurator = new MemConfiguration(this);
    }

    public TestConfiguration setName(final String value) {
        this.testName = value;
        return this;
    }

    public SpeedConfiguration performSpeedTest() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    public MemConfiguration performUsedMemTest() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

    public MemConfiguration performAllocatedMemTest() {
        allocatedMemConfigurator.setActive(true);
        return allocatedMemConfigurator;
    }

    String getTestName() {
        return testName;
    }

    SpeedConfiguration getSpeed() {
        return speedConfigurator;
    }

    MemConfiguration getUsedMem() {
        return usedMemConfigurator;
    }

    MemConfiguration getAllocatedMem() {
        return allocatedMemConfigurator;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (testName != null) {
            buf.append(TableFormatter.title("CONFIGURATION OF " + testName, '='));
        } else {
            buf.append(TableFormatter.title("CONFIGURATION", '='));
        }
        append(buf, "Speed", speedConfigurator);
        append(buf, "Used Memory", usedMemConfigurator);
        append(buf, "Allocated Memory", allocatedMemConfigurator);
        return buf.toString();
    }

    private void append(StringBuilder buf,
            String title,
            Activable activable) {
        if (activable.isActive()) {
            buf
                .append(System.lineSeparator())
                .append(TableFormatter.title(title, '-'))
                .append(activable.toString())
                .append(System.lineSeparator());
        }
    }
}
