package binary;

import binary.types.AscendingVariable;
import binary.types.BinaryConstraint;
import binary.types.BinaryConstraintStorage;
import binary.types.Variable;

import java.util.function.Consumer;

/**
 * Represents a binary constraint satisfaction problem.
 *
 * @version 3.1
 */
public final class BinaryCSP {

    /** The {@link Variable}s of this {@link BinaryCSP}. */
    private Variable[] variables;

    /** The {@link BinaryConstraint}s between the variables. */
    private BinaryConstraintStorage binaryConstraints;

    /**
     * Creates a {@link BinaryCSP} with
     * the given domain bounds and {@link BinaryConstraint}s.
     * It is expected that bounds[i][0] is the lower bound of variable i,
     * and that bounds[i][1] is the upper bound of variable i.
     *
     * @param bounds      the variables' domain bounds
     * @param constraints the {@link BinaryConstraint}s between variables
     */
    public BinaryCSP(int[][] bounds, BinaryConstraintStorage constraints) {
        binaryConstraints = constraints;
        variables = new Variable[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            variables[i] = new AscendingVariable(i, bounds[i][0], bounds[i][1]);
        }
    }

    /**
     * Creates and returns a {@link String} representation
     * of this {@link BinaryCSP}.
     *
     * @return the {@link String} representing this {@link BinaryCSP}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("CSP:\n");
        for (Variable variable : variables) {
            result.append(variable);
            result.append("\n");
        }
        for (BinaryConstraint bc : binaryConstraints.getConstraints()) {
            result.append(bc);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Retrieves the {@link BinaryConstraint} between
     * the specified {@link Variable}s.
     *
     * @param var1 the first {@link Variable}
     * @param var2 the second {@link Variable}
     *
     * @return the {@link BinaryConstraint} between
     * the specified {@link Variable}s or null if no such exists
     */
    public BinaryConstraint getBinaryConstraint(Variable var1, Variable var2) {
        return binaryConstraints.get(var1.getId(), var2.getId());
    }

    /**
     * Determines if a {@link BinaryConstraint} exists between
     * the two given {@link Variable}s.
     *
     * @param var1 the first {@link Variable}
     * @param var2 the second {@link Variable}
     *
     * @return true iff there is a {@link BinaryConstraint}
     * between the two {@link Variable}s
     */
    public boolean existsConstraint(Variable var1, Variable var2) {
        int id1 = var1.getId();
        int id2 = var2.getId();
        return binaryConstraints.get(id1, id2) != null
                || binaryConstraints.get(id2, id1) != null;
    }

    /**
     * Invokes the provided {@link Consumer}
     * on each {@link Variable} in this {@link BinaryCSP}.
     *
     * @param consumer the {@link Consumer} to invoke on each {@link Variable}
     */
    public void forEachVariable(Consumer<Variable> consumer) {
        for (Variable var : variables) {
            consumer.accept(var);
        }
    }

}
