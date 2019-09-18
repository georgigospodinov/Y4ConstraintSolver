package nary.algorithms;

import nary.constraints.Assignment;
import nary.constraints.NaryConstraint;
import nary.types.BaseVariable;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents an arc from one {@link BaseVariable} to another.
 *
 * @author 150009974
 * @version 1.2
 */
public class BaseVarArc {

    /** The {@link BaseVariable} whose domain depends on the supporter. */
    private BaseVariable dependent;

    /** The {@link BaseVariable} that supports values for the dependent. */
    private BaseVariable supporter;

    /** The {@link NaryConstraint}s that contain both {@link BaseVariable}s. */
    private LinkedHashSet<NaryConstraint> common;

    public BaseVarArc(BaseVariable dep, BaseVariable sup, LinkedHashSet<NaryConstraint> constraints) {
        dependent = dep;
        supporter = sup;
        common = constraints;
    }

    /** @return the {@link BaseVariable} whose domain depends on the supporter */
    public BaseVariable getDependent() {
        return dependent;
    }

    /** @return the {@link BaseVariable} that supports values for the dependent */
    public BaseVariable getSupporter() {
        return supporter;
    }

    /**
     * Prunes and returns values from the {@link #dependent}'s domain
     * based on the {@link #supporter}'s domain.
     *
     * @param current the current {@link Assignment} of values,
     *                that is passed to the constraints
     *
     * @return the performed {@link BaseVarPrune} on the dependent {@link BaseVariable}
     *
     * @see NaryConstraint#getSupported(Assignment, String)
     */
    public BaseVarPrune prune(Assignment current) {
        LinkedHashSet<Integer> supported;
        LinkedHashSet<Integer> allRemoved = new LinkedHashSet<>();
        LinkedHashSet<Integer> removed;
        // For each relevant constraint, remove unsupported values.
        for (NaryConstraint constraint : common) {
            supported = constraint.getSupported(current, dependent.getName());
            removed = dependent.retainValues(supported::contains);
            allRemoved.addAll(removed);
        }
        return new BaseVarPrune(dependent, allRemoved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependent, supporter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseVarArc)) {
            return false;
        }
        BaseVarArc that = (BaseVarArc) o;
        return dependent.equals(that.dependent) &&
                supporter.equals(that.supporter);
    }

    @Override
    public String toString() {
        return "BaseVarArc{"
                + "dependent=" + dependent + ","
                + "supporter=" + supporter + ","
                + "}";
    }

}
