package nary.algorithms;

import nary.types.BaseVariable;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents an association between a {@link BaseVariable}
 * and a {@link LinkedHashSet} of values that have been pruned.
 *
 * @author 150009974
 * @version 1.1
 */
public class BaseVarPrune {


    /** The {@link BaseVariable} from which values have been pruned. */
    private BaseVariable var;

    /** The removed values. */
    private LinkedHashSet<Integer> values;

    /**
     * Creates an association between the given {@link BaseVariable}
     * and the given values.
     *
     * @param variable the {@link BaseVariable} from which values were pruned
     * @param vals     the pruned values
     */
    public BaseVarPrune(BaseVariable variable, LinkedHashSet<Integer> vals) {
        var = variable;
        values = vals;
    }

    /** Undoes the pruning that created this {@link BaseVarPrune} instance. */
    public void undo() {
        values.forEach(var::addToDomain);
    }

    /**
     * Checks if this {@link BaseVarPrune} is empty.
     * In other words, if no values have been pruned.
     *
     * @return true iff the amount of pruned values is 0
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(var, values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseVarPrune)) {
            return false;
        }
        BaseVarPrune that = (BaseVarPrune) o;
        return var.equals(that.var) &&
                values.equals(that.values);
    }

    @Override
    public String toString() {
        return "BaseVarPrune{"
                + "var=" + var + ","
                + "values=" + values + ","
                + "}";
    }

}
