package com.fillumina.performance.executor.sample;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import java.io.Serializable;
import java.util.Objects;

/**
 * The iteration performance sample value for a single test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleValue implements TNamed, Serializable {
    private static final long serialVersionUID = 1L;

    private final TName name;
    private final Quantity<?> quantity;

    public SampleValue(TName name, double value, Unit<?> unit) {
        this(name, Quantity.from(value, unit));
    }

    public SampleValue(TName name, Quantity<?> quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public TName getName() {
        return name;
    }

    public Quantity<?> getQuantity() {
        return quantity;
    }

    public String toCsv() {
        return name.toString() + ", " + quantity.getValue();
    }

    public String toStringValue() {
        return quantity.toString();
    }

    @Override
    public String toString() {
        return name.toString() + "=" + quantity.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.quantity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SampleValue other = (SampleValue) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        return true;
    }
}
