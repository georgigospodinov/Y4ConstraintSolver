package binary.algorithms;

import binary.types.Variable;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents an association between a {@link Variable}
 * and a {@link LinkedHashSet} of values that have been pruned.
 *
 * @author 150009974
 * @version 2.1
 */
public class Prune {

    /** The {@link Variable} from which values have been pruned. */
    private Variable var;

    /** The removed values. */
    private LinkedHashSet<Integer> values;

    /**
     * Creates an association between the given {@link Variable}
     * and the given values.
     *
     * @param variable the {@link Variable} from which values were pruned
     * @param vals     the pruned values
     */
    public Prune(Variable variable, LinkedHashSet<Integer> vals) {
        var = variable;
        values = vals;
    }

    /** Undoes the pruning that created this {@link Prune} instance. */
    public void undo() {
        values.forEach(var::addToDomain);
    }

    /**
     * Checks if this {@link Prune} is empty.
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Prune)) {
            return false;
        }
        Prune other = (Prune) obj;
        return this.var.equals(other.var)
                && this.values.equals(other.values);
    }

    @Override
    public String toString() {
        return "Prune{"
                + "var=" + var + ","
                + "values=" + values + ","
                + "}";
    }

}
