package com.fillumina.performance.template;

import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.TableFormatter;

/**
 * Configures the tests using a <i>fluent interface</i>.
 * <p>
 * This configuration actually defaults to:
 * <ul>
 * <li>{@code baseIterations} = 1_000
 * <li>{@code maxStandardDeviation} = 10
 * <li>{@code message} = ""
 * <li>{@code standardDeviationConsumers} = empty
 * <li>{@code timeoutSeconds} = 10
 * <li>{@code threads} = 1 (means single thread)
 * <li>{@code workers} = 1
 * <li>no iteration consumers.
 * </ul>
 * <p>
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

    String getTestName() {
        return testName;
    }

    public SpeedConfiguration speed() {
        return speedConfigurator;
    }

    public MemConfiguration usedMem() {
        return usedMemConfigurator;
    }

    public MemConfiguration allocatedMem() {
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
